package io.fourth_finger.settings_repository

import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.time.Duration

class SettingsDataSourceTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val settingsDataSource = SettingsDataSource(context)

    @Test
    fun saveAndLoadSettings() = runTest(timeout = Duration.parse("60s")) {
        val testSettings = Settings(42)
        settingsDataSource.saveSettings(context, testSettings)

        val loadedSettings = Settings(settingsDataSource.songSkipMultiplier.first())
        assertEquals(testSettings, loadedSettings)
    }

    @Test
    fun load_nonexistentSettings_returnsDefaultSettings() = runTest(timeout = Duration.parse("60s")) {
        val testSettings = Settings(3)
        val loadedSettings = Settings(settingsDataSource.songSkipMultiplier.first())
        assertEquals(testSettings, loadedSettings)
    }

}