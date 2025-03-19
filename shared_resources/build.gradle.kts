import org.gradle.kotlin.dsl.androidTestImplementation

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "io.fourth_finger.shared_resources"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

        consumerProguardFiles("consumer-rules.pro")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments += (
                mapOf(
                    "clearPackageData" to "true",
                    "useTestStorageService" to "true"
                )
                )

    }

    buildTypes {

        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
            isMinifyEnabled = false
        }

        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    sourceSets {
        named("debug") {
            res.srcDirs("src/main/res") // Ensure debug sees main resources
        }
    }

}

dependencies {
    debugImplementation(libs.material)

    debugImplementation(libs.androidx.appcompat)
    debugImplementation(libs.androidx.espresso.core)
    debugImplementation (libs.hilt.android)
    debugImplementation (libs.hilt.android.testing)
    kspDebug (libs.hilt.android.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

}