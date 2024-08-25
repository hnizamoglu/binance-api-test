plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
    id("io.sentry.jvm.gradle") version "4.11.0"
}

group = "com.tekron"
version = "0.0.1-SNAPSHOT"

sentry {
    // Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
    // This enables source context, allowing you to see your source
    // code as part of your stack traces in Sentry.
    includeSourceContext = true

    org = "tekron"
    projectName = "kotlin"
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

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("jakarta.websocket:jakarta.websocket-api:2.2.0")
    implementation("io.github.binance:binance-connector-java:3.2.0")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("ch.qos.logback:logback-core:1.5.6")
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("com.logtail:logback-logtail:0.3.3")
    implementation("org.telegram:telegrambots-longpolling:7.7.3")
    implementation("org.telegram:telegrambots-client:7.7.3")


    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
