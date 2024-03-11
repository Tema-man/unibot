plugins {
    id("library-conventions")
}

dependencies {
    implementation(libs.telegram.bots)
    implementation(libs.kotlin.coroutines.core)

    implementation(project(":ub-core"))
}