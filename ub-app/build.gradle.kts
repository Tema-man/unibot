plugins {
    id("library-conventions")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialisation.bom)
    implementation(libs.kotlin.serialisation.json)
    implementation(libs.tomlj)
    implementation(libs.hikaricp)
    implementation(libs.postgresql.driver)
    implementation(libs.logging.logback)

    implementation(project(":ub-core"))
}