FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY build/libs/service.llm-0.0.1-SNAPSHOT.jar app.jar

# 포트 노출
EXPOSE 8086

# 헬스체크
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8086/actuator/health || exit 1

# 실행
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dfile.encoding=UTF-8", \
    "-Xmx2g", \
    "-Xms1g", \
    "-jar", \
    "app.jar"]

