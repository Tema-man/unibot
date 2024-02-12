plugins {
    id("library-conventions")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.kotlin.lang)
    implementation(libs.logging.kotlin)
    implementation(libs.logging.logback)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialisation.bom)
    implementation(libs.kotlin.serialisation.json)
    implementation(libs.tomlj)

    implementation(project(":ub-core"))
}