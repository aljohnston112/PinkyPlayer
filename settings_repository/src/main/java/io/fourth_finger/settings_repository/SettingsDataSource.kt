package io.fourth_finger.settings_repository

import android.content.Context
import io.fourth_finger.file_util.FileUtil

/**
 * A data source for app settings
 */
internal class SettingsDataSource {

    /**
     * Saves the given settings.
     * Any past settings will be overwritten.
     *
     * @param context
     * @param settings The settings to save.
     */
    fun saveSettings(context: Context, settings: Settings) {
        FileUtil.save(
            settings,
            context,
            FILE_NAME_SETTINGS,
            SAVE_FILE_VERIFICATION_NUMBER
        )
    }

    /**
     * Tries to load settings.
     *
     * @param context
     * @return The loaded settings, or null if there was a problem loading them.
     */
    fun loadSettings(context: Context): Settings? {
        return FileUtil.load(
            context,
            FILE_NAME_SETTINGS,
            SAVE_FILE_VERIFICATION_NUMBER
        )
    }

    companion object {
        private const val FILE_NAME_SETTINGS = "SETTINGS"
        private const val SAVE_FILE_VERIFICATION_NUMBER = 4596834290567902435L

    }

}