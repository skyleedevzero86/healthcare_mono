# Qwen2.5-7B-Instruct-GGUF 모델 다운로드 스크립트 (Windows PowerShell)

Write-Host "Qwen2.5-7B-Instruct-GGUF 모델 다운로드를 시작합니다..." -ForegroundColor Green

# models 디렉토리 생성
if (-not (Test-Path "models")) {
    New-Item -ItemType Directory -Path "models" | Out-Null
    Write-Host "models 디렉토리를 생성했습니다." -ForegroundColor Yellow
}

# huggingface_hub 설치 확인 및 설치
Write-Host "`nhuggingface_hub 설치 확인 중..." -ForegroundColor Cyan
try {
    $null = python -c "import huggingface_hub" 2>$null
    Write-Host "huggingface_hub가 이미 설치되어 있습니다." -ForegroundColor Green
} catch {
    Write-Host "huggingface_hub를 설치합니다..." -ForegroundColor Yellow
    pip install -U huggingface_hub
}

# 모델 다운로드
Write-Host "`n모델 다운로드 중... (약 4.7GB, 시간이 걸릴 수 있습니다)" -ForegroundColor Cyan
python -c @"
from huggingface_hub import hf_hub_download, list_repo_files
import os

model_dir = './models'
os.makedirs(model_dir, exist_ok=True)

print('저장소 파일 목록 확인 중...')
try:
    # 저장소의 파일 목록 확인
    files = list_repo_files('Qwen/Qwen2.5-7B-Instruct-GGUF', repo_type='model')
    
    # q4_k_m 파일 찾기
    model_file = None
    for f in files:
        if 'q4_k_m' in f.lower() and f.endswith('.gguf'):
            model_file = f
            print(f'찾은 파일: {f}')
            break
    
    if not model_file:
        print('q4_k_m 파일을 찾을 수 없습니다. 사용 가능한 파일 목록:')
        for f in files[:20]:
            print(f'  - {f}')
        raise Exception('q4_k_m 모델 파일을 찾을 수 없습니다.')
    
    print(f'다운로드할 파일: {model_file}')
    print('다운로드 시작...')
    file_path = hf_hub_download(
        repo_id='Qwen/Qwen2.5-7B-Instruct-GGUF',
        filename=model_file,
        local_dir=model_dir
    )
    
    # 파일 이름을 표준 이름으로 변경
    target_file = os.path.join(model_dir, 'qwen2.5-7b-instruct-q4_k_m.gguf')
    if file_path != target_file and os.path.exists(file_path):
        if os.path.exists(target_file):
            os.remove(target_file)
        os.rename(file_path, target_file)
        print(f'파일 이름 변경: {os.path.basename(file_path)} -> qwen2.5-7b-instruct-q4_k_m.gguf')
    
    print(f'다운로드 완료: {target_file}')
except Exception as e:
    print(f'에러 발생: {e}')
    print('대안: 다른 저장소에서 시도합니다...')
    # 대안: 다른 저장소 시도
    try:
        file_path = hf_hub_download(
            repo_id='Qwen/Qwen2.5-7B-Instruct-GGUF',
            filename='Qwen2.5-7B-Instruct-Q4_K_M.gguf',
            local_dir=model_dir
        )
        target_file = os.path.join(model_dir, 'qwen2.5-7b-instruct-q4_k_m.gguf')
        if os.path.exists(file_path) and file_path != target_file:
            if os.path.exists(target_file):
                os.remove(target_file)
            os.rename(file_path, target_file)
        print(f'다운로드 완료: {target_file}')
    except Exception as e2:
        print(f'대안 시도도 실패: {e2}')
        print('수동으로 모델을 다운로드하세요:')
        print('https://huggingface.co/Qwen/Qwen2.5-7B-Instruct-GGUF')
        raise
"@

if (Test-Path "models\qwen2.5-7b-instruct-q4_k_m.gguf") {
    Write-Host "`n모델 다운로드가 완료되었습니다!" -ForegroundColor Green
    $fileSize = (Get-Item "models\qwen2.5-7b-instruct-q4_k_m.gguf").Length / 1GB
    Write-Host "파일 크기: $([math]::Round($fileSize, 2)) GB" -ForegroundColor Green
} else {
    Write-Host "`n모델 다운로드에 실패했습니다." -ForegroundColor Red
    exit 1
}

