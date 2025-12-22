#!/bin/bash

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

TARGET_SERVER="${DEPLOY_TARGET_SERVER:-localhost}"
TARGET_USER="${DEPLOY_TARGET_USER:-ec2-user}"
DOCKER_REGISTRY="${DOCKER_REGISTRY:-}"
SSH_KEY="${DEPLOY_SSH_KEY:-~/.ssh/id_rsa}"

SERVICES=(
    "service.discovery"
    "service.config"
    "api.gateway"
    "service.auth"
    "service.comm"
    "service.healthcare"
    "service.usermanagement"
    "service.llm"
    "web.healthcare"
)

declare -A SERVICE_PORTS=(
    ["service.discovery"]="8761"
    ["service.config"]="8888"
    ["api.gateway"]="8080"
    ["service.auth"]="8082"
    ["service.comm"]="8085"
    ["service.healthcare"]="8084"
    ["service.usermanagement"]="8087"
    ["service.llm"]="8086"
    ["web.healthcare"]="8981"
)

DRY_RUN=false
DEPLOY_ALL=true
SPECIFIC_SERVICE=""
BUILD_NUMBER="${BUILD_NUMBER:-latest}"

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

health_check() {
    local service=$1
    local port=${SERVICE_PORTS[$service]}
    local host="${2:-$TARGET_SERVER}"
    
    if [ -z "$port" ]; then
        return 0
    fi
    
    local health_url="http://${host}:${port}/actuator/health"
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f -s "$health_url" > /dev/null 2>&1; then
            return 0
        fi
        sleep 2
        attempt=$((attempt + 1))
    done
    
    return 1
}

deploy_service_blue_green() {
    local service=$1
    local port=${SERVICE_PORTS[$service]}
    
    local image_name
    if [ -n "$DOCKER_REGISTRY" ]; then
        image_name="${DOCKER_REGISTRY}/${service}:${BUILD_NUMBER}"
    else
        image_name="${service}:latest"
    fi
    
    log_info "========================================="
    log_info "무중단 배포 시작: ${service}"
    log_info "========================================="
    
    if [ "$DRY_RUN" = true ]; then
        log_warning "[DRY RUN] ${service} 배포 시뮬레이션"
        log_warning "  이미지: ${image_name}"
        log_warning "  서버: ${TARGET_SERVER}"
        return 0
    fi
    
    if [ "$TARGET_SERVER" = "localhost" ]; then
        deploy_local_docker "$service" "$image_name" "$port"
    else
        deploy_remote_docker "$service" "$image_name" "$port"
    fi
    
    if [ "$service" = "api.gateway" ] && [ "$TARGET_SERVER" != "localhost" ]; then
        log_info "${service}: Nginx 설정 확인 중..."
        ssh -i "$SSH_KEY" "$TARGET_USER@$TARGET_SERVER" "
            if command -v nginx &> /dev/null; then
                sudo nginx -t && sudo systemctl reload nginx || echo 'Nginx 재시작 실패'
            else
                echo 'Nginx가 설치되지 않았습니다'
            fi
        " || log_warning "${service}: Nginx 설정 확인 실패 (무시)"
    fi
}

deploy_local_docker() {
    local service=$1
    local image_name=$2
    local port=$3
    local container_name=$(echo "$service" | tr '.' '-')
    local new_container="${container_name}-new-${BUILD_NUMBER}"
    local old_container="${container_name}-old"
    
    log_info "${service}: 로컬 Docker 배포 시작..."
    
    if [ -n "$DOCKER_REGISTRY" ]; then
        docker pull "$image_name" || {
            log_warning "${service}: 이미지 pull 실패, 로컬 이미지 사용"
        }
    fi
    
    if docker ps -a --format '{{.Names}}' | grep -q "^${container_name}$"; then
        log_info "${service}: 기존 컨테이너를 ${old_container}로 이름 변경"
        docker stop "${container_name}" 2>/dev/null || true
        docker rename "${container_name}" "${old_container}" 2>/dev/null || true
    fi
    
    log_info "${service}: 새 컨테이너 시작 중..."
    docker run -d \
        --name "${new_container}" \
        -p "${port}:${port}" \
        --restart unless-stopped \
        "${image_name}" || {
        log_error "${service}: 새 컨테이너 시작 실패"
        if docker ps -a --format '{{.Names}}' | grep -q "^${old_container}$"; then
            docker rename "${old_container}" "${container_name}" 2>/dev/null || true
            docker start "${container_name}" 2>/dev/null || true
        fi
        return 1
    }
    
    log_info "${service}: 헬스 체크 대기 중..."
    sleep 10
    
    if health_check "$service" "localhost"; then
        log_success "${service}: 새 컨테이너 헬스 체크 성공"
        
        if docker ps -a --format '{{.Names}}' | grep -q "^${old_container}$"; then
            log_info "${service}: 기존 컨테이너 중지 및 제거"
            docker stop "${old_container}" 2>/dev/null || true
            sleep 2
            docker rm "${old_container}" 2>/dev/null || true
        fi
        
        docker rename "${new_container}" "${container_name}" 2>/dev/null || true
        log_success "${service}: 무중단 배포 완료"
    else
        log_error "${service}: 새 컨테이너 헬스 체크 실패, 롤백"
        docker stop "${new_container}" 2>/dev/null || true
        docker rm "${new_container}" 2>/dev/null || true
        
        if docker ps -a --format '{{.Names}}' | grep -q "^${old_container}$"; then
            docker rename "${old_container}" "${container_name}" 2>/dev/null || true
            docker start "${container_name}" 2>/dev/null || true
            log_info "${service}: 기존 컨테이너로 롤백 완료"
        fi
        return 1
    fi
}

deploy_remote_docker() {
    local service=$1
    local image_name=$2
    local port=$3
    local container_name=$(echo "$service" | tr '.' '-')
    local new_container="${container_name}-new-${BUILD_NUMBER}"
    local old_container="${container_name}-old"
    
    log_info "${service}: 원격 Docker 배포 시작 (${TARGET_SERVER})..."
    
    ssh -i "$SSH_KEY" "$TARGET_USER@$TARGET_SERVER" bash << EOF
        set -e
        
        if [ -n "${DOCKER_REGISTRY}" ]; then
            docker pull ${image_name} || echo "이미지 pull 실패, 로컬 이미지 사용"
        fi
        
        if docker ps -a --format '{{.Names}}' | grep -q "^${container_name}$"; then
            echo "기존 컨테이너를 ${old_container}로 이름 변경"
            docker stop ${container_name} 2>/dev/null || true
            docker rename ${container_name} ${old_container} 2>/dev/null || true
        fi
        
        echo "새 컨테이너 시작 중..."
        docker run -d \
            --name ${new_container} \
            -p ${port}:${port} \
            --restart unless-stopped \
            ${image_name} || {
            echo "새 컨테이너 시작 실패"
            if docker ps -a --format '{{.Names}}' | grep -q "^${old_container}$"; then
                docker rename ${old_container} ${container_name} 2>/dev/null || true
                docker start ${container_name} 2>/dev/null || true
            fi
            exit 1
        }
        
        echo "헬스 체크 대기 중..."
        sleep 10
        
        max_attempts=30
        attempt=1
        while [ \$attempt -le \$max_attempts ]; do
            if curl -f -s http://localhost:${port}/actuator/health > /dev/null 2>&1; then
                echo "새 컨테이너 헬스 체크 성공"
                
                if docker ps -a --format '{{.Names}}' | grep -q "^${old_container}$"; then
                    echo "기존 컨테이너 중지 및 제거"
                    docker stop ${old_container} 2>/dev/null || true
                    sleep 2
                    docker rm ${old_container} 2>/dev/null || true
                fi
                
                docker rename ${new_container} ${container_name} 2>/dev/null || true
                echo "무중단 배포 완료"
                exit 0
            fi
            sleep 2
            attempt=\$((attempt + 1))
        done
        
        echo "새 컨테이너 헬스 체크 실패, 롤백"
        docker stop ${new_container} 2>/dev/null || true
        docker rm ${new_container} 2>/dev/null || true
        
        if docker ps -a --format '{{.Names}}' | grep -q "^${old_container}$"; then
            docker rename ${old_container} ${container_name} 2>/dev/null || true
            docker start ${container_name} 2>/dev/null || true
            echo "기존 컨테이너로 롤백 완료"
        fi
        exit 1
EOF
    
    if [ $? -eq 0 ]; then
        if health_check "$service" "$TARGET_SERVER"; then
            log_success "${service}: 원격 무중단 배포 완료"
        else
            log_warning "${service}: 원격 헬스 체크 실패 (서비스는 배포됨)"
        fi
    else
        log_error "${service}: 원격 배포 실패"
        return 1
    fi
}

show_help() {
    cat << EOF
Docker 무중단 배포 스크립트

사용법:
    ./deploy-docker.sh [옵션]

옵션:
    --dry-run              실제 배포 없이 시뮬레이션만 수행
    --service <서비스명>   특정 서비스만 배포
    --all                  모든 서비스 배포 (기본값)
    --help                 이 도움말 표시

환경 변수:
    DEPLOY_TARGET_SERVER   배포 대상 서버 주소 (기본값: localhost)
    DEPLOY_TARGET_USER     배포 대상 서버 사용자 (기본값: ec2-user)
    DEPLOY_SSH_KEY         SSH 개인 키 경로 (기본값: ~/.ssh/id_rsa)
    DOCKER_REGISTRY        Docker 레지스트리 주소 (선택)
    BUILD_NUMBER           빌드 번호/태그 (기본값: latest)

예제:
    ./deploy-docker.sh --all
    ./deploy-docker.sh --service api.gateway
    BUILD_NUMBER=v1.0.0 ./deploy-docker.sh --all

EOF
}

while [[ $# -gt 0 ]]; do
    case $1 in
        --dry-run)
            DRY_RUN=true
            shift
            ;;
        --service)
            DEPLOY_ALL=false
            SPECIFIC_SERVICE="$2"
            shift 2
            ;;
        --all)
            DEPLOY_ALL=true
            shift
            ;;
        --help)
            show_help
            exit 0
            ;;
        *)
            log_error "알 수 없는 옵션: $1"
            show_help
            exit 1
            ;;
    esac
done

log_info "========================================="
log_info "Docker 무중단 배포 시작"
log_info "========================================="
log_info "배포 모드: $([ "$DRY_RUN" = true ] && echo "DRY RUN" || echo "실제 배포")"
log_info "대상 서버: $TARGET_SERVER"
log_info "빌드 번호: $BUILD_NUMBER"
log_info "========================================="
echo ""

if [ "$DEPLOY_ALL" = true ]; then
    SERVICES_TO_DEPLOY=("${SERVICES[@]}")
else
    if [ -z "$SPECIFIC_SERVICE" ]; then
        log_error "서비스 이름을 지정해주세요: --service <서비스명>"
        exit 1
    fi
    
    if [[ ! " ${SERVICES[@]} " =~ " ${SPECIFIC_SERVICE} " ]]; then
        log_error "알 수 없는 서비스: $SPECIFIC_SERVICE"
        exit 1
    fi
    
    SERVICES_TO_DEPLOY=("$SPECIFIC_SERVICE")
fi

FAILED_SERVICES=()
SUCCESS_COUNT=0

for service in "${SERVICES_TO_DEPLOY[@]}"; do
    if deploy_service_blue_green "$service"; then
        SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
    else
        FAILED_SERVICES+=("$service")
    fi
done

echo ""
log_info "========================================="
log_info "배포 결과 요약"
log_info "========================================="
log_info "총 서비스 수: ${#SERVICES_TO_DEPLOY[@]}"
log_success "성공: $SUCCESS_COUNT"

if [ ${#FAILED_SERVICES[@]} -gt 0 ]; then
    log_error "실패: ${#FAILED_SERVICES[@]}"
    log_error "실패한 서비스: ${FAILED_SERVICES[*]}"
    exit 1
else
    log_success "모든 서비스 무중단 배포 완료!"
    exit 0
fi

