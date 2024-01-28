package io.fourth_finger.settings_repository

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsRepositoryTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val settingsRepository = SettingsRepository(context)

    @Test
    fun loadSettings_withNoSavedSettings_returnsDefaultSettings() {
        val initialSettings = settingsRepository.settings
        assertEquals(initialSettings, Settings(skipMultiplier = 10L))
    }

    @Test
    fun saveSettings_followedByLoadSettings_returnsSavedSettings() {
        val initialSettings = Settings(skipMultiplier = 5L)
        settingsRepository.saveSettings(context, initialSettings)

        val loadedSettings = settingsRepository.settings
        assertEquals(initialSettings, loadedSettings)
    }

}