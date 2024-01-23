package io.fourth_finger.settings_repository

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsRepositoryTest {

    @Test
    fun loadSettings_withNoSavedSettings_returnsDefaultSettings() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val settingsRepository = SettingsRepository(context)

        val initialSettings = settingsRepository.settings
        assertEquals(initialSettings, Settings(skipMultiplier = 10L))
    }

    @Test
    fun saveSettings_followedByLoadSettings_returnsSavedSettings() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val settingsRepository = SettingsRepository(context)

        val newSettings = Settings(skipMultiplier = 5)
        settingsRepository.saveSettings(context, newSettings)

        val loadedSettings = settingsRepository.settings
        assertEquals(newSettings, loadedSettings)
    }

}