#!/bin/bash

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

PROJECT_ROOT="${PROJECT_ROOT:-$(pwd)}"
TEST_RESULTS_DIR="${PROJECT_ROOT}/test-results"
COVERAGE_DIR="${PROJECT_ROOT}/coverage"

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

mkdir -p "${TEST_RESULTS_DIR}"
mkdir -p "${COVERAGE_DIR}"

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

TOTAL_SERVICES=0
PASSED_SERVICES=0
FAILED_SERVICES=0
SKIPPED_SERVICES=0

log_info "========================================="
log_info "전체 프로젝트 테스트 자동화 실행"
log_info "========================================="
echo ""

log_info "Backend 서비스 테스트 시작..."
echo ""

for service in "${SERVICES[@]}"; do
    TOTAL_SERVICES=$((TOTAL_SERVICES + 1))
    
    log_info "----------------------------------------"
    log_info "테스트 실행: ${service}"
    log_info "----------------------------------------"
    
    if [ ! -d "${PROJECT_ROOT}/${service}" ]; then
        log_warning "${service} 디렉토리를 찾을 수 없습니다. 건너뜁니다."
        SKIPPED_SERVICES=$((SKIPPED_SERVICES + 1))
        continue
    fi
    
    cd "${PROJECT_ROOT}/${service}"
    
    if [ ! -f "gradlew" ]; then
        log_warning "${service}에 gradlew가 없습니다. 건너뜁니다."
        SKIPPED_SERVICES=$((SKIPPED_SERVICES + 1))
        continue
    fi
    
    chmod +x gradlew || true
    
    if ./gradlew test --no-daemon > "${TEST_RESULTS_DIR}/${service}-test.log" 2>&1; then
        log_success "${service} 테스트 성공"
        PASSED_SERVICES=$((PASSED_SERVICES + 1))
        
        if [ -d "build/test-results" ]; then
            mkdir -p "${TEST_RESULTS_DIR}/${service}"
            cp -r build/test-results/* "${TEST_RESULTS_DIR}/${service}/" 2>/dev/null || true
        fi
        
        if [ -d "build/reports/tests" ]; then
            mkdir -p "${TEST_RESULTS_DIR}/${service}/reports"
            cp -r build/reports/tests/* "${TEST_RESULTS_DIR}/${service}/reports/" 2>/dev/null || true
        fi
    else
        log_error "${service} 테스트 실패"
        FAILED_SERVICES=$((FAILED_SERVICES + 1))
        echo "로그 파일: ${TEST_RESULTS_DIR}/${service}-test.log"
    fi
    
    echo ""
done

log_info "========================================="
log_info "모바일 앱 테스트 시작"
log_info "========================================="
echo ""

MOBILE_DIR="${PROJECT_ROOT}/mobile/healthcare_mobile"

if [ -d "${MOBILE_DIR}" ]; then
    cd "${MOBILE_DIR}"
    
    if ! command -v node &> /dev/null; then
        log_warning "Node.js가 설치되지 않았습니다. 모바일 앱 테스트를 건너뜁니다."
    elif ! command -v pnpm &> /dev/null && ! command -v npm &> /dev/null; then
        log_warning "pnpm 또는 npm이 설치되지 않았습니다. 모바일 앱 테스트를 건너뜁니다."
    else
        if ! command -v pnpm &> /dev/null; then
            log_info "pnpm 설치 중..."
            npm install -g pnpm || log_warning "pnpm 설치 실패"
        fi
        
        log_info "의존성 설치 중..."
        if command -v pnpm &> /dev/null; then
            pnpm install --frozen-lockfile || log_warning "의존성 설치 경고"
        else
            npm install || log_warning "의존성 설치 경고"
        fi
        
        log_info "모바일 앱 테스트 실행 중..."
        if command -v pnpm &> /dev/null; then
            if pnpm test:ci > "${TEST_RESULTS_DIR}/mobile-test.log" 2>&1; then
                log_success "모바일 앱 테스트 성공"
                PASSED_SERVICES=$((PASSED_SERVICES + 1))
                
                if [ -d "coverage" ]; then
                    cp -r coverage/* "${COVERAGE_DIR}/mobile/" 2>/dev/null || true
                fi
            else
                log_error "모바일 앱 테스트 실패"
                FAILED_SERVICES=$((FAILED_SERVICES + 1))
                echo "로그 파일: ${TEST_RESULTS_DIR}/mobile-test.log"
            fi
        else
            if npm test -- --ci --coverage > "${TEST_RESULTS_DIR}/mobile-test.log" 2>&1; then
                log_success "모바일 앱 테스트 성공"
                PASSED_SERVICES=$((PASSED_SERVICES + 1))
            else
                log_error "모바일 앱 테스트 실패"
                FAILED_SERVICES=$((FAILED_SERVICES + 1))
            fi
        fi
    fi
else
    log_warning "모바일 앱 디렉토리를 찾을 수 없습니다. 건너뜁니다."
    SKIPPED_SERVICES=$((SKIPPED_SERVICES + 1))
fi

echo ""
log_info "========================================="
log_info "테스트 결과 요약"
log_info "========================================="
echo ""
echo "총 서비스: ${TOTAL_SERVICES}"
log_success "성공: ${PASSED_SERVICES}"
if [ ${FAILED_SERVICES} -gt 0 ]; then
    log_error "실패: ${FAILED_SERVICES}"
fi
if [ ${SKIPPED_SERVICES} -gt 0 ]; then
    log_warning "건너뜀: ${SKIPPED_SERVICES}"
fi
echo ""
echo "테스트 결과 디렉토리: ${TEST_RESULTS_DIR}"
echo "커버리지 리포트 디렉토리: ${COVERAGE_DIR}"
echo ""

if [ ${FAILED_SERVICES} -gt 0 ]; then
    log_error "일부 테스트가 실패했습니다."
    exit 1
else
    log_success "모든 테스트가 성공적으로 완료되었습니다!"
    exit 0
fi

