plugins {
    id("library-conventions")
}

dependencies {
    implementation(libs.logging.kotlin)
    implementation(libs.logging.logback)
    implementation(libs.kotlin.coroutines.core)
}