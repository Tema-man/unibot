plugins {
    id("library-conventions")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.logging.kotlin)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialisation.bom)
    implementation(libs.kotlin.serialisation.json)
}