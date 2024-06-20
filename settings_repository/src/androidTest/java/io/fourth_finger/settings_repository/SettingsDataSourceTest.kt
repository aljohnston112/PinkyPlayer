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
        val testSettings = Settings(
            42,
            true
        )
        settingsDataSource.saveSettings(
            context,
            testSettings
        )

        val loadedSettings = Settings(
            settingsDataSource.songSkipMultiplier.first(),
            settingsDataSource.respectAudioFocus.first()
        )
        assertEquals(
            testSettings,
            loadedSettings
        )
    }

    @Test
    fun load_nonexistentSettings_returnsDefaultSettings() =
        runTest(timeout = Duration.parse("60s")) {
            val testSettings = Settings(
                66,
                false
            )
            val loadedSettings = Settings(
                settingsDataSource.songSkipMultiplier.first(),
                settingsDataSource.respectAudioFocus.first()
            )
            assertEquals(testSettings, loadedSettings)
        }

}