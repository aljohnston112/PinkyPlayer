plugins {
    alias(libs.plugins.javaLibrary)
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}


dependencies {
    testImplementation(libs.junit)
}