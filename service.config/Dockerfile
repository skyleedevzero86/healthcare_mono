FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY build/libs/service.config-0.1-SNAPSHOT.jar service.config.jar
ENTRYPOINT ["java","-jar","service.config.jar"]