import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.10"
}

group = "com.cioccarellia"
version = "1.0.0"


tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "19"
}