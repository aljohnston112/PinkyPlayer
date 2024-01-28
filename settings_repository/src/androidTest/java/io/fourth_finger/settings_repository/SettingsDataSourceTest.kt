package io.fourth_finger.settings_repository

import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SettingsDataSourceTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val settingsDataSource = SettingsDataSource()

    @Test
    fun saveAndLoadSettings() {
        val testSettings = Settings(42)
        settingsDataSource.saveSettings(context, testSettings)

        val loadedSettings = settingsDataSource.loadSettings(context)
        assertEquals(testSettings, loadedSettings)
    }

    @Test
    fun load_nonexistentSettings_returnsNull() {
        val loadedSettings = settingsDataSource.loadSettings(context)
        assertNull(loadedSettings)
    }

}