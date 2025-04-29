plugins {
	java
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
	id("io.freefair.lombok") version "8.4"
	id("org.flywaydb.flyway") version "9.16.0"
}

group = "com.softina"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(23)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	implementation("org.flywaydb:flyway-mysql:11.3.4")
	testAnnotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	implementation("org.apache.commons:commons-collections4:4.4")
	implementation("mysql:mysql-connector-java:8.0.33")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
	// Зависимости для валидации
	implementation ("org.hibernate:hibernate-validator:6.1.6.Final")
	implementation ("javax.validation:validation-api:2.0.1.Final")

	// Если не добавлен, добавьте также поддержку аннотаций
	implementation ("org.glassfish:javax.el:3.0.0")

}

tasks.withType<Test> {
	useJUnitPlatform()
}


