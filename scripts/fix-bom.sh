#!/bin/bash

echo "Java 파일에서 BOM 문자 제거 중..."

count=0
find . -name "*.java" -type f | while read file; do
    if [[ -f "$file" ]]; then
        if head -c 3 "$file" | od -An -tx1 | grep -q "ef bb bf"; then
            sed -i '1s/^\xEF\xBB\xBF//' "$file"
            echo "BOM 제거: $file"
            ((count++))
        fi
    fi
done

echo "완료: $count 개 파일에서 BOM 제거됨"

