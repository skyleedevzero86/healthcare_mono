set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

DOMAIN="${DOMAIN:-www.naver.com}"
EMAIL="${EMAIL:-admin@naver.com}"

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_info "========================================="
log_info "Nginx 설정 스크립트"
log_info "========================================="
log_info "도메인: ${DOMAIN}"
log_info "이메일: ${EMAIL}"
log_info "========================================="

if [ "$EUID" -ne 0 ]; then
    log_error "이 스크립트는 root 권한으로 실행해야 합니다"
    log_info "사용법: sudo ./setup-nginx.sh"
    exit 1
fi

log_info "Nginx 설치 확인 중..."
if ! command -v nginx &> /dev/null; then
    log_info "Nginx 설치 중..."
    apt-get update
    apt-get install -y nginx certbot python3-certbot-nginx
else
    log_success "Nginx가 이미 설치되어 있습니다"
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
NGINX_CONF="${SCRIPT_DIR}/nginx.conf"

if [ ! -f "$NGINX_CONF" ]; then
    log_error "nginx.conf 파일을 찾을 수 없습니다: ${NGINX_CONF}"
    exit 1
fi

log_info "Nginx 설정 파일 복사 중..."
sed "s/www.naver.com/${DOMAIN}/g; s/naver.com/${DOMAIN#www.}/g" "$NGINX_CONF" > "/etc/nginx/sites-available/healthcare"

log_info "Nginx 설정 활성화 중..."
ln -sf /etc/nginx/sites-available/healthcare /etc/nginx/sites-enabled/

if [ -f /etc/nginx/sites-enabled/default ]; then
    log_info "기본 설정 비활성화 중..."
    rm -f /etc/nginx/sites-enabled/default
fi

log_info "Nginx 설정 테스트 중..."
if nginx -t; then
    log_success "Nginx 설정이 유효합니다"
else
    log_error "Nginx 설정에 오류가 있습니다"
    exit 1
fi

log_info "Nginx 재시작 중..."
systemctl reload nginx || systemctl restart nginx

log_success "Nginx 설정 완료"

log_info "SSL 인증서 설치 중..."
if certbot --nginx -d "${DOMAIN}" -d "${DOMAIN#www.}" --non-interactive --agree-tos --email "${EMAIL}" --redirect; then
    log_success "SSL 인증서 설치 완료"
else
    log_warning "SSL 인증서 설치 실패 (HTTP로만 동작)"
fi

log_info "========================================="
log_success "Nginx 설정 완료!"
log_info "========================================="
log_info "HTTP: http://${DOMAIN}"
log_info "HTTPS: https://${DOMAIN}"
log_info "========================================="

