base {
    group = "dev.cherryd"
    archivesName = "unibot-app"
    version = "1.0.0"
}

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
    implementation(project(":bot-discord"))
    implementation(project(":ub-core"))
    implementation(project(":ub-app"))
}

application {
    mainClass.set("dev.cherryd.MainKt")
}
