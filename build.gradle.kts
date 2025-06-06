// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply(false)
    alias(libs.plugins.android.library) apply(false)
    alias(libs.plugins.androidx.navigation.safe.args.plugin) apply(false)
    alias(libs.plugins.kotlin.android) apply(false)
    alias(libs.plugins.kotlin.jvm) apply(false)
    alias(libs.plugins.ksp) apply(false)
    alias(libs.plugins.hilt.android) apply(false)
    alias(libs.plugins.protobuf) apply(false)
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}