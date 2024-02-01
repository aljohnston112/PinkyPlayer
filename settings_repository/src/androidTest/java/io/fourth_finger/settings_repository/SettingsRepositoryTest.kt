package io.fourth_finger.settings_repository

import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Duration

class SettingsRepositoryTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val settingsRepository = SettingsRepository(context)

    @Test
    fun loadSettings_withNoSavedSettings_returnsDefaultSettings() = runTest(timeout = Duration.parse("60s")) {
        val initialSettings = settingsRepository.settings.first()
        assertEquals(initialSettings, Settings(skipMultiplier = 3L))
    }

    @Test
    fun saveSettings_followedByLoadSettings_returnsSavedSettings() = runTest(timeout = Duration.parse("60s")) {
        val initialSettings = Settings(skipMultiplier = 5L)
        settingsRepository.saveSettings(context, initialSettings)

        val loadedSettings = settingsRepository.settings.first()
        assertEquals(initialSettings, loadedSettings)
    }

}