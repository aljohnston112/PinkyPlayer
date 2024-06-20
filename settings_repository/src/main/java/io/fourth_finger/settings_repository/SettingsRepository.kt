package io.fourth_finger.settings_repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository for app settings.
 *
 * @param context
 */
@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext context: Context
) {

    private val settingsDataSource = SettingsDataSource(context)

    val probabilityDown = settingsDataSource.songSkipMultiplier
    val respectAudioFocus = settingsDataSource.respectAudioFocus

    /**
     * Saves the given settings.
     *
     * @param context
     * @param settings The settings to save.
     */
    suspend fun saveSettings(
        context: Context,
        settings: Settings,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        withContext(dispatcher) {
            settingsDataSource.saveSettings(
                context,
                settings
            )
        }
    }

}