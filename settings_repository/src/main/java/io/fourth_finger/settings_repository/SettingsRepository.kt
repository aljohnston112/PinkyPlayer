package io.fourth_finger.settings_repository

import android.content.Context

/**
 * A repository for app settings.
 *
 * @param context
 */
class SettingsRepository(context: Context) {

    private val settingsDataSource = SettingsDataSource()

    var settings = settingsDataSource.loadSettings(context) ?: Settings(skipMultiplier = 10)
        private set

    /**
     * Saves the given settings.
     *
     * @param context
     * @param settings The settings to save.
     */
    fun saveSettings(context: Context, settings: Settings) {
        this.settings = settings
        settingsDataSource.saveSettings(context, settings)
    }

}