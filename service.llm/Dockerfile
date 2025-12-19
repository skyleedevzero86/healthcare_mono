FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY build/libs/service.llm-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8086 # service.llm 포트

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8086/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dfile.encoding=UTF-8", \
    "-Xmx2g", \
    "-Xms1g", \
    "-jar", \
    "app.jar"]

