$ErrorActionPreference = "Continue"

$PROJECT_ROOT = if ($env:PROJECT_ROOT) { $env:PROJECT_ROOT } else { Get-Location }
$TEST_RESULTS_DIR = Join-Path $PROJECT_ROOT "test-results"
$COVERAGE_DIR = Join-Path $PROJECT_ROOT "coverage"

function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Blue
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

New-Item -ItemType Directory -Force -Path $TEST_RESULTS_DIR | Out-Null
New-Item -ItemType Directory -Force -Path $COVERAGE_DIR | Out-Null

$SERVICES = @(
    "service.discovery",
    "service.config",
    "api.gateway",
    "service.auth",
    "service.comm",
    "service.healthcare",
    "service.usermanagement",
    "service.llm",
    "web.healthcare"
)

$script:TOTAL_SERVICES = 0
$script:PASSED_SERVICES = 0
$script:FAILED_SERVICES = 0
$script:SKIPPED_SERVICES = 0

Write-Info "========================================="
Write-Info "전체 프로젝트 테스트 자동화 실행"
Write-Info "========================================="
Write-Host ""

Write-Info "Backend 서비스 테스트 시작..."
Write-Host ""

foreach ($service in $SERVICES) {
    $script:TOTAL_SERVICES++
    
    Write-Info "----------------------------------------"
    Write-Info "테스트 실행: $service"
    Write-Info "----------------------------------------"
    
    $servicePath = Join-Path $PROJECT_ROOT $service
    
    if (-not (Test-Path $servicePath)) {
        Write-Warning "$service 디렉토리를 찾을 수 없습니다. 건너뜁니다."
        $script:SKIPPED_SERVICES++
        continue
    }
    
    Set-Location $servicePath
    
    $gradlewPath = Join-Path $servicePath "gradlew.bat"
    if (-not (Test-Path $gradlewPath)) {
        Write-Warning "$service에 gradlew.bat가 없습니다. 건너뜁니다."
        $script:SKIPPED_SERVICES++
        continue
    }
    
    $logFile = Join-Path $TEST_RESULTS_DIR "$service-test.log"
    
    try {
        & $gradlewPath test --no-daemon *> $logFile
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "$service 테스트 성공"
            $script:PASSED_SERVICES++
            
            $testResultsPath = Join-Path $servicePath "build\test-results"
            if (Test-Path $testResultsPath) {
                $targetDir = Join-Path $TEST_RESULTS_DIR $service
                New-Item -ItemType Directory -Force -Path $targetDir | Out-Null
                Copy-Item -Path "$testResultsPath\*" -Destination $targetDir -Recurse -Force -ErrorAction SilentlyContinue
            }
        } else {
            Write-Error "$service 테스트 실패"
            $script:FAILED_SERVICES++
            Write-Host "로그 파일: $logFile"
        }
    } catch {
        Write-Error "$service 테스트 실행 중 오류: $_"
        $script:FAILED_SERVICES++
    }
    
    Write-Host ""
}

Write-Info "========================================="
Write-Info "모바일 앱 테스트 시작"
Write-Info "========================================="
Write-Host ""

$MOBILE_DIR = Join-Path $PROJECT_ROOT "mobile\healthcare_mobile"

if (Test-Path $MOBILE_DIR) {
    Set-Location $MOBILE_DIR
    
    $nodeExists = Get-Command node -ErrorAction SilentlyContinue
    $pnpmExists = Get-Command pnpm -ErrorAction SilentlyContinue
    $npmExists = Get-Command npm -ErrorAction SilentlyContinue
    
    if (-not $nodeExists) {
        Write-Warning "Node.js가 설치되지 않았습니다. 모바일 앱 테스트를 건너뜁니다."
        $script:SKIPPED_SERVICES++
    } elseif (-not $pnpmExists -and -not $npmExists) {
        Write-Warning "pnpm 또는 npm이 설치되지 않았습니다. 모바일 앱 테스트를 건너뜁니다."
        $script:SKIPPED_SERVICES++
    } else {
        if (-not $pnpmExists) {
            Write-Info "pnpm 설치 중..."
            npm install -g pnpm
        }
        
        Write-Info "의존성 설치 중..."
        if ($pnpmExists -or (Get-Command pnpm -ErrorAction SilentlyContinue)) {
            pnpm install --frozen-lockfile
        } else {
            npm install
        }
        
        Write-Info "모바일 앱 테스트 실행 중..."
        $logFile = Join-Path $TEST_RESULTS_DIR "mobile-test.log"
        
        try {
            if (Get-Command pnpm -ErrorAction SilentlyContinue) {
                pnpm test:ci *> $logFile
            } else {
                npm test -- --ci --coverage *> $logFile
            }
            
            if ($LASTEXITCODE -eq 0) {
                Write-Success "모바일 앱 테스트 성공"
                $script:PASSED_SERVICES++
                
                $coveragePath = Join-Path $MOBILE_DIR "coverage"
                if (Test-Path $coveragePath) {
                    $targetDir = Join-Path $COVERAGE_DIR "mobile"
                    New-Item -ItemType Directory -Force -Path $targetDir | Out-Null
                    Copy-Item -Path "$coveragePath\*" -Destination $targetDir -Recurse -Force -ErrorAction SilentlyContinue
                }
            } else {
                Write-Error "모바일 앱 테스트 실패"
                $script:FAILED_SERVICES++
                Write-Host "로그 파일: $logFile"
            }
        } catch {
            Write-Error "모바일 앱 테스트 실행 중 오류: $_"
            $script:FAILED_SERVICES++
        }
    }
} else {
    Write-Warning "모바일 앱 디렉토리를 찾을 수 없습니다. 건너뜁니다."
    $script:SKIPPED_SERVICES++
}

Write-Host ""
Write-Info "========================================="
Write-Info "테스트 결과 요약"
Write-Info "========================================="
Write-Host ""
Write-Host "총 서비스: $script:TOTAL_SERVICES"
Write-Success "성공: $script:PASSED_SERVICES"
if ($script:FAILED_SERVICES -gt 0) {
    Write-Error "실패: $script:FAILED_SERVICES"
}
if ($script:SKIPPED_SERVICES -gt 0) {
    Write-Warning "건너뜀: $script:SKIPPED_SERVICES"
}
Write-Host ""
Write-Host "테스트 결과 디렉토리: $TEST_RESULTS_DIR"
Write-Host "커버리지 리포트 디렉토리: $COVERAGE_DIR"
Write-Host ""

if ($script:FAILED_SERVICES -gt 0) {
    Write-Error "일부 테스트가 실패했습니다."
    exit 1
} else {
    Write-Success "모든 테스트가 성공적으로 완료되었습니다!"
    exit 0
}

