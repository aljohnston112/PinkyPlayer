package io.fourth_finger.settings_repository

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * A repository for app settings.
 *
 * @param context
 */
class SettingsRepository(context: Context) {

    private val settingsDataSource = SettingsDataSource(context)

    var settings = settingsDataSource.songSkipMultiplier.map { songSkipMultiplier ->
        Settings(songSkipMultiplier)
    }

    /**
     * Saves the given settings.
     *
     * @param context
     * @param settings The settings to save.
     */
    suspend fun saveSettings(
        context: Context,
        settings: Settings,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ) {
        withContext(dispatcher) {
            settingsDataSource.saveSettings(context, settings)
        }
    }

}