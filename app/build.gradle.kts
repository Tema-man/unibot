base {
    group = "dev.cherryd"
    version = "1.0.0"
}

plugins {
    id("application-conventions")
    alias(libs.plugins.ktor) apply false
}

dependencies {
    implementation(libs.logging.logback)
    implementation(libs.ktor.core)
    implementation(libs.ktor.cio)
    implementation(libs.ktor.micrometer)

    implementation(project(":bot-telegram"))
    implementation(project(":bot-discord"))
    implementation(project(":ub-core"))
    implementation(project(":ub-app"))
}

application {
    mainClass.set("dev.cherryd.unibot.MainKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

tasks.withType<Jar> {
    archiveFileName.set("unibot.jar")
    destinationDirectory.set(File("$rootDir/output"))
    manifest {
        attributes("Main-Class" to "dev.cherryd.unibot.MainKt")
    }
    dependsOn(configurations.runtimeClasspath)
    from(configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
