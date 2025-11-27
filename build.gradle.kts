// Dependency versions
val lombokVersion = "1.18.42"
val mapstructVersion = "1.6.3"
val jedisVersion = "5.1.2"
val plantUmlVersion = "8059"
val mysqlConnectorVersion = "8.0.33"
val h2Version = "2.4.240"
val openApiVersion = "2.8.13"

plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.kabisa"
version = "0.0.1-SNAPSHOT"
description = "Quote Api"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {

    // UTILITY
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("redis.clients:jedis:$jedisVersion")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openApiVersion")
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

    // FRONTEND
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // DATA STORAGE
    implementation("mysql:mysql-connector-java:$mysqlConnectorVersion")
    runtimeOnly("com.h2database:h2:$h2Version")

    // DOCUMENTATION
    implementation("net.sourceforge.plantuml:plantuml:$plantUmlVersion")

    // TEST
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

tasks.withType<Test> {
	useJUnitPlatform()
}
