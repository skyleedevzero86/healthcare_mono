#!/bin/bash

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

TARGET_SERVER="${DEPLOY_TARGET_SERVER:-localhost}"
TARGET_USER="${DEPLOY_TARGET_USER:-ec2-user}"
TARGET_DIR="${DEPLOY_TARGET_DIR:-/app/services}"
SSH_KEY="${DEPLOY_SSH_KEY:-~/.ssh/id_rsa}"

SERVICES=(
    "service.discovery"
    "service.config"
    "api.gateway"
    "service.auth"
    "service.comm"
    "service.healthcare"
    "service.usermanagement"
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
    ["web.healthcare"]="8981"
)

DRY_RUN=false
DEPLOY_ALL=true
SPECIFIC_SERVICE=""
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

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

show_help() {
    cat << EOF
Healthcare Monorepo 배포 스크립트

사용법:
    ./deploy.sh [옵션]

옵션:
    --dry-run              실제 배포 없이 시뮬레이션만 수행
    --service <서비스명>   특정 서비스만 배포
    --all                  모든 서비스 배포 (기본값)
    --help                 이 도움말 표시

환경 변수:
    DEPLOY_TARGET_SERVER   배포 대상 서버 주소 (기본값: localhost)
    DEPLOY_TARGET_USER     배포 대상 서버 사용자 (기본값: ec2-user)
    DEPLOY_TARGET_DIR      배포 대상 디렉토리 (기본값: /app/services)
    DEPLOY_SSH_KEY         SSH 개인 키 경로 (기본값: ~/.ssh/id_rsa)

예제:
    # 모든 서비스 배포 (시뮬레이션)
    ./deploy.sh --dry-run

    # 특정 서비스만 배포
    ./deploy.sh --service api.gateway

    # 모든 서비스 배포
    ./deploy.sh --all

EOF
}

check_build() {
    local service=$1
    local jar_path="$PROJECT_ROOT/$service/build/libs"
    
    if [ ! -d "$jar_path" ]; then
        log_error "$service: 빌드 디렉토리가 없습니다: $jar_path"
        return 1
    fi
    
    local jar_file=$(find "$jar_path" -name "*.jar" ! -name "*-plain.jar" | head -1)
    
    if [ -z "$jar_file" ]; then
        log_error "$service: JAR 파일을 찾을 수 없습니다"
        return 1
    fi
    
    log_success "$service: 빌드 확인 완료 - $(basename $jar_file)"
    echo "$jar_file"
}

deploy_local() {
    local service=$1
    local jar_file=$2
    
    log_info "$service: 로컬 배포 시작..."
    
    if [ "$DRY_RUN" = true ]; then
        log_warning "$service: [DRY RUN] 배포 시뮬레이션 - $jar_file"
        return 0
    fi
    
    local service_dir="$TARGET_DIR/$service"
    mkdir -p "$service_dir"
    
    cp "$jar_file" "$service_dir/"
    
    log_success "$service: 로컬 배포 완료"
}

deploy_remote() {
    local service=$1
    local jar_file=$2
    
    log_info "$service: 원격 배포 시작 ($TARGET_SERVER)..."
    
    if [ "$DRY_RUN" = true ]; then
        log_warning "$service: [DRY RUN] 원격 배포 시뮬레이션"
        log_warning "  서버: $TARGET_USER@$TARGET_SERVER"
        log_warning "  대상: $TARGET_DIR/$service"
        log_warning "  파일: $jar_file"
        return 0
    fi
    
    # 원격 디렉토리 생성
    ssh -i "$SSH_KEY" "$TARGET_USER@$TARGET_SERVER" \
        "mkdir -p $TARGET_DIR/$service" || {
        log_error "$service: 원격 디렉토리 생성 실패"
        return 1
    }
    
    scp -i "$SSH_KEY" "$jar_file" \
        "$TARGET_USER@$TARGET_SERVER:$TARGET_DIR/$service/" || {
        log_error "$service: 파일 복사 실패"
        return 1
    }
    
    local service_name=$(echo "$service" | tr '.' '-')
    ssh -i "$SSH_KEY" "$TARGET_USER@$TARGET_SERVER" \
        "sudo systemctl restart $service_name || echo 'Service restart skipped'" || {
        log_warning "$service: 서비스 재시작 건너뜀 (수동 재시작 필요)"
    }
    
    log_success "$service: 원격 배포 완료"
}

health_check() {
    local service=$1
    local port=${SERVICE_PORTS[$service]}
    
    if [ -z "$port" ]; then
        log_warning "$service: 포트 정보가 없어 헬스 체크를 건너뜁니다"
        return 0
    fi
    
    log_info "$service: 헬스 체크 중... (포트: $port)"
    
    if [ "$DRY_RUN" = true ]; then
        log_warning "$service: [DRY RUN] 헬스 체크 시뮬레이션"
        return 0
    fi
    
    local health_url="http://$TARGET_SERVER:$port/actuator/health"
    local max_attempts=10
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f -s "$health_url" > /dev/null 2>&1; then
            log_success "$service: 헬스 체크 성공"
            return 0
        fi
        
        log_info "$service: 헬스 체크 대기 중... ($attempt/$max_attempts)"
        sleep 3
        attempt=$((attempt + 1))
    done
    
    log_error "$service: 헬스 체크 실패 (최대 시도 횟수 초과)"
    return 1
}

deploy_service() {
    local service=$1
    
    log_info "========================================="
    log_info "서비스 배포: $service"
    log_info "========================================="
    
    local jar_file=$(check_build "$service")
    if [ $? -ne 0 ]; then
        log_error "$service: 빌드 확인 실패, 배포 중단"
        return 1
    fi
    
    if [ "$TARGET_SERVER" = "localhost" ]; then
        deploy_local "$service" "$jar_file"
    else
        deploy_remote "$service" "$jar_file"
    fi
    
    if [ $? -ne 0 ]; then
        log_error "$service: 배포 실패"
        return 1
    fi
    
    if [ "$DRY_RUN" = false ]; then
        sleep 5
        health_check "$service" || {
            log_warning "$service: 헬스 체크 실패했지만 계속 진행합니다"
        }
    fi
    
    log_success "$service: 배포 완료"
    echo ""
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
log_info "Healthcare Monorepo 배포 시작"
log_info "========================================="
log_info "배포 모드: $([ "$DRY_RUN" = true ] && echo "DRY RUN (시뮬레이션)" || echo "실제 배포")"
log_info "대상 서버: $TARGET_SERVER"
log_info "대상 사용자: $TARGET_USER"
log_info "대상 디렉토리: $TARGET_DIR"
log_info "========================================="
echo ""

if [ "$DEPLOY_ALL" = true ]; then
    SERVICES_TO_DEPLOY=("${SERVICES[@]}")
    log_info "모든 서비스 배포: ${SERVICES_TO_DEPLOY[*]}"
else
    if [ -z "$SPECIFIC_SERVICE" ]; then
        log_error "서비스 이름을 지정해주세요: --service <서비스명>"
        exit 1
    fi
    
    if [[ ! " ${SERVICES[@]} " =~ " ${SPECIFIC_SERVICE} " ]]; then
        log_error "알 수 없는 서비스: $SPECIFIC_SERVICE"
        log_info "사용 가능한 서비스: ${SERVICES[*]}"
        exit 1
    fi
    
    SERVICES_TO_DEPLOY=("$SPECIFIC_SERVICE")
    log_info "특정 서비스 배포: $SPECIFIC_SERVICE"
fi

echo ""

FAILED_SERVICES=()
SUCCESS_COUNT=0

for service in "${SERVICES_TO_DEPLOY[@]}"; do
    if deploy_service "$service"; then
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
    log_success "모든 서비스 배포 완료!"
    exit 0
fi

