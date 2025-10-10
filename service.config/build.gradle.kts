plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.sleekydz86"
version = "0.1"
description = "service.discovery"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2025.0.0"

dependencies {
    implementation ("org.springframework.cloud:spring-cloud-config-server")
    //cloud bus
    implementation ("org.springframework.boot:spring-boot-starter-actuator")
    implementation ("org.springframework.cloud:spring-cloud-starter-bus-amqp")
    implementation ("org.springframework.cloud:spring-cloud-starter-bootstrap")
    testImplementation ("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
