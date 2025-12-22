Write-Host "Java 파일에서 BOM 문자 제거 중..." -ForegroundColor Yellow

$javaFiles = Get-ChildItem -Path . -Include *.java -Recurse -File

$count = 0
foreach ($file in $javaFiles) {
    $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8
    
    if ($content.Length -gt 0 -and [System.Text.Encoding]::UTF8.GetBytes($content)[0] -eq 0xEF -and 
        [System.Text.Encoding]::UTF8.GetBytes($content)[1] -eq 0xBB -and 
        [System.Text.Encoding]::UTF8.GetBytes($content)[2] -eq 0xBF) {
        
        $utf8NoBom = New-Object System.Text.UTF8Encoding $false
        [System.IO.File]::WriteAllText($file.FullName, $content, $utf8NoBom)
        
        Write-Host "BOM 제거: $($file.FullName)" -ForegroundColor Green
        $count++
    }
}

Write-Host "완료: $count 개 파일에서 BOM 제거됨" -ForegroundColor Green

