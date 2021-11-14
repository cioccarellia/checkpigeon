import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
}

group = "com.cioccarellia"
version = "1.0.0"

repositories {
    jcenter()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

dependencies {
    implementation(project(":core:logic"))
    implementation(project(":core:checkpigeon"))
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-server-netty:1.6.4")
    implementation("io.ktor:ktor-html-builder:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("ServerKt")
}