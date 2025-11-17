// Dependency versions
val lombokVersion = "1.18.38"
val mapstructVersion = "1.6.3"
val plantUmlVersion = "8059"

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
    //	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
	compileOnly("org.projectlombok:lombok:$lombokVersion")
	annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

    // SECURITY
    // implementation("org.springframework.boot:spring-boot-starter-security")

    // FRONTEND
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // DATA STORAGE
    // implementation("mysql:mysql-connector-java:${mysqlConnectorJavaVersion}")

    // DOCUMENTATION
    implementation("net.sourceforge.plantuml:plantuml:${plantUmlVersion}")

    // TEST
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
