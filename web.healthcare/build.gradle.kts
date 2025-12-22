plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.sleekydz86"
version = "0.0.1-SNAPSHOT"
description = "web.healthcare"

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
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-tomcat")
    implementation("org.apache.tomcat.embed:tomcat-embed-jasper")
    implementation("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:3.0.0")
    implementation("org.glassfish.web:jakarta.servlet.jsp.jstl:3.0.1")

    implementation("org.apache.tiles:tiles-jsp:3.0.8")
    implementation("org.apache.tiles:tiles-core:3.0.8")
    implementation("org.apache.tiles:tiles-servlet:3.0.8")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-core")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.bgee.log4jdbc-log4j2:log4jdbc-log4j2-jdbc4:1.16")
    implementation("org.json:json:20231013")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation("org.codehaus.janino:janino:3.1.12")
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
    dependencies {
        dependency("io.jsonwebtoken:jjwt-api:0.12.3")
        dependency("io.jsonwebtoken:jjwt-impl:0.12.3")
        dependency("io.jsonwebtoken:jjwt-jackson:0.12.3")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set("web.healthcare")
    archiveVersion.set("")
    archiveExtension.set("jar")
    enabled = false
}

tasks.bootJar {
    archiveBaseName.set("web.healthcare")
    archiveVersion.set("")
    archiveExtension.set("jar")
}

tasks.bootRun {
    jvmArgs = listOf("-Dfile.encoding=UTF-8", "-Dspring.profiles.active=dev")
}