plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "io.fourth_finger.music_repository"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments += (
                mapOf(
                        "clearPackageData" to "true",
                        "useTestStorageService" to "true"
                )
        )
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        installation {
            timeOutInMs = 600000
        }
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

}

dependencies {

    implementation(project(":shared_resources"))

    implementation(libs.lifecycle.livedata.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    debugImplementation(libs.androidx.monitor)

    testImplementation (libs.junit)
    testImplementation (libs.kotlinx.coroutines.test)

    androidTestImplementation (libs.androidx.core.testing)
    androidTestUtil (libs.androidx.orchestrator)
    androidTestImplementation (libs.androidx.rules)
    androidTestImplementation (libs.hilt.android.testing)
    kspAndroidTest (libs.hilt.android.compiler)
    androidTestImplementation (libs.kotlinx.coroutines.test)

}

