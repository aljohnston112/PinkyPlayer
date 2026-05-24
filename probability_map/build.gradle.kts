plugins {
    alias(libs.plugins.javaLibrary)
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_24
    targetCompatibility = JavaVersion.VERSION_24
}

kotlin {
    jvmToolchain(24)
}

dependencies {
    testImplementation(libs.junit)
}