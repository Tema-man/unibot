plugins {
    id("library-conventions")
}

dependencies {
    implementation(libs.kotlin.lang)
    implementation(libs.kord)
    implementation(libs.logging.kotlin)
    implementation(libs.logging.logback)
    implementation(libs.kotlin.coroutines.core)

    implementation(project(":ub-core"))
}