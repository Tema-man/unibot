// Needs to be able to use versions catalog in convenience plugins https://github.com/gradle/gradle/issues/15383
import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.kotlin.lang)
    implementation(libs.logging.kotlin)
//    implementation(libs.logging.logback)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

tasks.test {
    useJUnit()
}
