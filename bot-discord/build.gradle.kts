plugins {
    id("library-conventions")
}

dependencies {
    implementation(libs.kord)
    implementation(libs.kotlin.coroutines.core)

    implementation(project(":ub-core"))
}