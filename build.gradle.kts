plugins {
    kotlin("jvm") version "2.0.20"
}

group = "io.github.kaliber"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Jackson core library
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")

    // Jackson Kotlin module for Kotlin-specific handling
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}