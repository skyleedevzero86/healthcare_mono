plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com"
version = "0.0.1-SNAPSHOT"
description = "service.commu"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2025.0.0"

configurations {
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}

dependencies {
    dependencies {
        implementation ("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux") 
        implementation ("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
        implementation ("org.springframework.cloud:spring-cloud-starter-config")
        implementation ("org.springframework.boot:spring-boot-starter-actuator")
        implementation ("org.projectlombok:lombok")
        annotationProcessor ("org.projectlombok:lombok")
        implementation ("org.springframework.boot:spring-boot-starter-validation")
        implementation ("org.springframework.boot:spring-boot-starter-log4j2")
        testImplementation ("org.springframework.boot:spring-boot-starter-test")
    }

}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}