plugins {
    `kotlin-dsl`
}

group = "io.fourth_finger.build_logic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}