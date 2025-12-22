#!/bin/bash

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

JENKINS_URL="${JENKINS_URL:-http://localhost:8080}"
JENKINS_USER="${JENKINS_USER:-admin}"
JENKINS_PASSWORD="${JENKINS_PASSWORD:-}"
JENKINS_CONTAINER="${JENKINS_CONTAINER:-jenkins}"

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

wait_for_jenkins() {
    log_info "Jenkins가 시작될 때까지 대기 중..."
    
    local max_attempts=60
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f -s "$JENKINS_URL/login" > /dev/null 2>&1; then
            log_success "Jenkins가 시작되었습니다"
            return 0
        fi
        
        log_info "대기 중... ($attempt/$max_attempts)"
        sleep 5
        attempt=$((attempt + 1))
    done
    
    log_error "Jenkins가 시작되지 않았습니다"
    return 1
}

get_initial_password() {
    log_info "초기 비밀번호 확인 중..."
    
    local password=$(docker exec "$JENKINS_CONTAINER" \
        cat /var/jenkins_home/secrets/initialAdminPassword 2>/dev/null)
    
    if [ -z "$password" ]; then
        log_warning "초기 비밀번호를 찾을 수 없습니다 (이미 설정되었을 수 있음)"
        return 1
    fi
    
    echo "$password"
    return 0
}

REQUIRED_PLUGINS=(
    "git"
    "github"
    "workflow-aggregator"
    "pipeline-stage-view"
    "docker-workflow"
    "gradle"
    "ssh"
    "blueocean"
)

install_plugins() {
    log_info "필수 플러그인 설치 중..."
    
    local plugin_list=$(IFS=,; echo "${REQUIRED_PLUGINS[*]}")
    
    log_info "설치할 플러그인: $plugin_list"
    
    log_warning "플러그인은 Jenkins 웹 UI에서 수동으로 설치해야 합니다:"
    log_info "1. Jenkins 관리 → 플러그인 관리 → 설치 가능 탭"
    log_info "2. 다음 플러그인 검색 후 설치:"
    for plugin in "${REQUIRED_PLUGINS[@]}"; do
        echo "   - $plugin"
    done
}

main() {
    log_info "========================================="
    log_info "Jenkins 초기 설정 스크립트"
    log_info "========================================="
    
    if ! docker ps | grep -q "$JENKINS_CONTAINER"; then
        log_error "Jenkins 컨테이너가 실행 중이지 않습니다: $JENKINS_CONTAINER"
        log_info "다음 명령으로 Jenkins를 시작하세요:"
        log_info "  docker-compose -f docker-compose.jenkins.yml up -d"
        exit 1
    fi
    
    if ! wait_for_jenkins; then
        exit 1
    fi
    
    local initial_password=$(get_initial_password)
    if [ -n "$initial_password" ]; then
        echo ""
        log_info "========================================="
        log_info "Jenkins 초기 비밀번호"
        log_info "========================================="
        echo "$initial_password"
        echo ""
        log_info "위 비밀번호를 사용하여 Jenkins에 로그인하세요:"
        log_info "  URL: $JENKINS_URL"
        echo ""
    fi
    
    install_plugins
    
    log_info "========================================="
    log_success "초기 설정 안내 완료"
    log_info "========================================="
    log_info "다음 단계:"
    log_info "1. $JENKINS_URL 에서 Jenkins에 로그인"
    log_info "2. 초기 비밀번호 입력"
    log_info "3. 'Install suggested plugins' 선택"
    log_info "4. 관리자 계정 생성"
    log_info "5. 필수 플러그인 설치 (위 목록 참고)"
    log_info "6. GitHub Webhook 설정 (JENKINS_SETUP.md 참고)"
}

main "$@"

