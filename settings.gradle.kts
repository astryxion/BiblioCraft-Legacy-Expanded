pluginManagement {
    plugins {
        id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
    }
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.minecraftschurli.at/maven-public") }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "Bibliocraft-Legacy"
