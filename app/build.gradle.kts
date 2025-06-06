plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.androidx.navigation.safe.args.plugin)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "io.fourth_finger.pinky_player"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "io.fourth_finger.pinky_player"
        // May be as high as 28, if needed
        minSdk = 28
        targetSdk = 35
        versionCode = 3
        versionName = "0.3.3"

        testInstrumentationRunner = "io.fourth_finger.shared_resources.test.PinkyRunner"
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

    flavorDimensions += "version"

    productFlavors {
        create("full") {}
        create("empty") {
            applicationIdSuffix = ".empty"
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
    implementation(project(":event_processor"))
    implementation(project(":music_list_fragment"))
    implementation(project(":music_repository"))
    implementation(project(":playlist_repository"))
    implementation(project(":playlist_list_fragment"))
    implementation(project(":probability_map"))
    implementation(project(":settings_repository"))
    implementation(project(":shared_resources"))

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.concurrent.futures.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    // https://github.com/androidx/media/issues/1352
    // Do not upgrade until commit 1a5cf67 is in the release
    // It will be in 1.4.0-alpha02
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.kotlinx.coroutines.core)

    debugImplementation(libs.androidx.monitor)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.androidx.espresso.contrib)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.media3.test.utils)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestUtil(libs.androidx.orchestrator)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.runner)
    androidTestUtil(libs.androidx.test.services)
    androidTestImplementation(libs.androidx.uiautomator)
    androidTestImplementation(libs.core.ktx)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
