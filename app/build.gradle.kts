base {
    group = "dev.cherryd"
    version = "1.0.0"
}

plugins {
    id("application-conventions")
}

dependencies {
    implementation(libs.telegram.bots)
    implementation(libs.logging.logback)
    implementation(libs.kotlin.coroutines.core)

    implementation(project(":bot-telegram"))
    implementation(project(":bot-discord"))
    implementation(project(":ub-core"))
    implementation(project(":ub-app"))
}

application {
    mainClass.set("dev.cherryd.unibot.MainKt")
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
