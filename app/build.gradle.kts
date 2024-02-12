group = "dev.cherryd"
version = "1.0.0"

plugins {
    id("application-conventions")
}

dependencies {
    implementation(libs.kotlin.lang)
    implementation(libs.telegram.bots)
    implementation(libs.logging.kotlin)
    implementation(libs.logging.logback)
    implementation(libs.kotlin.coroutines.core)

    implementation(project(":bot-telegram"))
    implementation(project(":ub-core"))
    implementation(project(":ub-app"))
}

application {
    mainClass.set("dev.cherryd.MainKt")
}