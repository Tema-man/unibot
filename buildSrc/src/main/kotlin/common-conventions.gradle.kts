val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.findLibrary("kotlin-lang").get())

//    testImplementation(libs.findLibrary("testing-junit-api").get())
//    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(libs.findVersion("jdk").get().displayName.toInt())
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
