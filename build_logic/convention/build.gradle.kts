plugins {
    `kotlin-dsl`
}

group = "io.fourth_finger.build_logic"


dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        create("orchestrator") {
            id = "io.fourth_finger.convention.orchestrator"
            implementationClass = "io.fourth_finger.convention.Orchestrator"
        }
    }
}