plugins {
    id("library-conventions")
}

dependencies {
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.telegram.bots)
    constraints {
        implementation(libs.apache.commons.codec) {
            because("version 1.11 has a vulnerability")
        }
    }

    implementation(project(":ub-core"))
}