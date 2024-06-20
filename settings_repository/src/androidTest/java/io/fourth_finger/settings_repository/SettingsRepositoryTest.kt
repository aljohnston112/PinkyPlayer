package io.fourth_finger.settings_repository

import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Duration

class SettingsRepositoryTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val settingsRepository = SettingsRepository(context)

    @Test
    fun loadSettings_withNoSavedSettings_returnsDefaultSettings() =
        runTest(timeout = Duration.parse("60s")) {
            val initialProbabilityDown = settingsRepository.probabilityDown.first()
            assertEquals(
                initialProbabilityDown,
                66
            )

            val initialRespectAudioFocus = settingsRepository.respectAudioFocus.first()
            assertEquals(
                initialRespectAudioFocus,
                false
            )
        }

    @Test
    fun saveSettings_followedByLoadSettings_returnsSavedSettings() =
        runTest(timeout = Duration.parse("60s")) {
            val initialSettings = Settings(
                5,
                true
            )
            settingsRepository.saveSettings(
                context,
                initialSettings
            )

            val loadedProbabilityDown = settingsRepository.probabilityDown.first()
            assertEquals(
                loadedProbabilityDown,
                5
            )

            val loadedRespectAudioFocus = settingsRepository.respectAudioFocus.first()
            assertEquals(
                loadedRespectAudioFocus,
                true
            )
        }

}