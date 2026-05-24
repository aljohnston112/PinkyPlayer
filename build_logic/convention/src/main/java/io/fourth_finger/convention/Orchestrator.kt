package io.fourth_finger.convention

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class Orchestrator : Plugin<Project> {
    override fun apply(target: Project) {
        println("*** Orchestrator invoked ***")
        val androidExt = target.extensions.findByType(ApplicationExtension::class.java)
            ?: error("Android plugin not applied")
        androidExt.apply {
            defaultConfig {
                testInstrumentationRunner =
                    "io.fourth_finger.shared_resources.test.PinkyRunner"
                testInstrumentationRunnerArguments["clearPackageData"] = "true"
                testInstrumentationRunnerArguments["useTestStorageService"] = "true"
            }

            testOptions {
                execution = "ANDROIDX_TEST_ORCHESTRATOR"
                installation {
                    timeOutInMs = 600000
                }
            }
        }
    }
}