plugins {
	kotlin("jvm") version "2.2.21"
	kotlin("plugin.spring") version "2.2.21"
	id("org.springframework.boot") version "4.0.4"
	id("io.spring.dependency-management") version "1.1.7"
    id("io.sentry.jvm.gradle") version "6.0.0"
}

group = "vehnixauto.server"
version = "0.0.1-SNAPSHOT"
sentry {
    includeSourceContext = true
	org = "casanayo"
	projectName = "vehnixauto"
	//System.getenv("SENTRY_AUTH_TOKEN_SOMBA")
    authToken = "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"
}
java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

extra["springCloudGcpVersion"] = "7.3.1"
extra["springCloudVersion"] = "2025.0.0"
extra["sentryVersion"] = "8.27.0"
dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	implementation("org.springframework.boot:spring-boot-starter-quartz")
	implementation("org.springframework.boot:spring-boot-starter-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-security-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.springframework:spring-jdbc")
	// Redis
	implementation("org.springframework.boot:spring-boot-starter-session-data-redis")
    // @sentry
    implementation("io.sentry:sentry:8.31.0")
    //rate limiting
    implementation("com.bucket4j:bucket4j-core:8.7.0")
    //libphone
    implementation("com.googlecode.libphonenumber:libphonenumber:8.13.38")
    // crypto
    implementation("org.springframework.security:spring-security-crypto")
    //websocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    //gcs
    implementation("com.google.cloud:spring-cloud-gcp-starter-storage")
    //twilio
	implementation("com.twilio.sdk:twilio:9.2.1") {
		exclude(group = "io.jsonwebtoken", module = "jjwt-api")
		exclude(group = "io.jsonwebtoken", module = "jjwt-impl")
		exclude(group = "io.jsonwebtoken", module = "jjwt-jackson")
	}
    //patch vulnerabilities dependencies
    implementation("commons-io:commons-io:2.21.0")
    implementation("org.json:json:20251224")
    implementation("com.ongres.scram:scram-common:3.2")
    implementation("io.netty:netty-codec-http2:4.2.9.Final")
    implementation("io.grpc:grpc-netty-shaded:1.78.0")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("io.r2dbc:r2dbc-pool:1.0.2.RELEASE")
    implementation("org.postgresql:r2dbc-postgresql:1.1.1.RELEASE")
    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")
    // jwt
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.postgresql:r2dbc-postgresql")
	// sender mail
	implementation("org.springframework.boot:spring-boot-starter-mail")
	// redis
	testImplementation("org.springframework.boot:spring-boot-starter-session-data-redis-test")
	// TEST MAIL
	testImplementation("org.springframework.boot:spring-boot-starter-mail-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-r2dbc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
	testImplementation("org.springframework.boot:spring-boot-starter-quartz-test")
	testImplementation("org.springframework.boot:spring-boot-starter-r2dbc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-client-test")
	testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
dependencyManagement {
    imports {
        mavenBom("com.google.cloud:spring-cloud-gcp-dependencies:${property("springCloudGcpVersion")}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
//		mavenBom("io.sentry:sentry-bom:${property("sentryVersion")}")
    }
}
kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
