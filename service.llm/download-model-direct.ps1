Write-Host "Qwen2.5-7B-Instruct-GGUF 모델 다운로드를 시작합니다..." -ForegroundColor Green
$modelsDir = ".\models"
if (-not (Test-Path $modelsDir)) {
    New-Item -ItemType Directory -Path $modelsDir | Out-Null
    Write-Host "models 디렉토리를 생성했습니다." -ForegroundColor Yellow
}

$oldFile = Join-Path $modelsDir "qwen2.5-7b-instruct-q4_k_m.gguf"
if (Test-Path $oldFile) {
    Write-Host "기존 모델 파일을 삭제합니다..." -ForegroundColor Yellow
    Remove-Item $oldFile -Force
}

Write-Host "`n모델 다운로드 중... (약 4.7GB, 시간이 걸릴 수 있습니다)" -ForegroundColor Cyan
Write-Host "다운로드 URL: https://huggingface.co/Qwen/Qwen2.5-7B-Instruct-GGUF" -ForegroundColor Gray

$urls = @(
    "https://huggingface.co/Qwen/Qwen2.5-7B-Instruct-GGUF/resolve/main/Qwen2.5-7B-Instruct-Q4_K_M.gguf",
    "https://huggingface.co/Qwen/Qwen2.5-7B-Instruct-GGUF/resolve/main/qwen2.5-7b-instruct-q4_k_m.gguf",
    "https://huggingface.co/bartowski/Qwen2.5-7B-Instruct-GGUF/resolve/main/Qwen2.5-7B-Instruct-Q4_K_M.gguf",
    "https://huggingface.co/TheBloke/Qwen2.5-7B-Instruct-GGUF/resolve/main/qwen2.5-7b-instruct-q4_k_m.gguf"
)

$success = $false
foreach ($url in $urls) {
    try {
        Write-Host "`n시도 중: $url" -ForegroundColor Cyan
        $outputFile = Join-Path $modelsDir "qwen2.5-7b-instruct-q4_k_m.gguf"
        
        $ProgressPreference = 'Continue'
        Invoke-WebRequest -Uri $url -OutFile $outputFile -UseBasicParsing
        
        $fileInfo = Get-Item $outputFile
        $fileSizeGB = [math]::Round($fileInfo.Length / 1GB, 2)
        
        if ($fileSizeGB -ge 4.0) {
            Write-Host "`n다운로드 완료!" -ForegroundColor Green
            Write-Host "파일 크기: $fileSizeGB GB" -ForegroundColor Green
            Write-Host "파일 위치: $outputFile" -ForegroundColor Green
            $success = $true
            break
        } else {
            Write-Host "파일 크기가 너무 작습니다 ($fileSizeGB GB). 다른 URL을 시도합니다..." -ForegroundColor Yellow
            Remove-Item $outputFile -Force -ErrorAction SilentlyContinue
        }
    } catch {
        Write-Host "다운로드 실패: $_" -ForegroundColor Red
        continue
    }
}

if (-not $success) {
    Write-Host "`n자동 다운로드에 실패했습니다." -ForegroundColor Red
    Write-Host "`n수동 다운로드 방법:" -ForegroundColor Yellow
    Write-Host "1. 브라우저에서 https://huggingface.co/Qwen/Qwen2.5-7B-Instruct-GGUF/tree/main 접속" -ForegroundColor White
    Write-Host "2. 'Q4_K_M' 또는 'q4_k_m' 포함된 단일 .gguf 파일 찾기" -ForegroundColor White
    Write-Host "3. 파일 크기가 약 4.7GB인 단일 파일 다운로드" -ForegroundColor White
    Write-Host "4. 다운로드한 파일을 $modelsDir 폴더에 복사" -ForegroundColor White
    Write-Host "5. 파일 이름을 'qwen2.5-7b-instruct-q4_k_m.gguf'로 변경" -ForegroundColor White
    exit 1
}

