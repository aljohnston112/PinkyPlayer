package io.fourth_finger.settings_repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * A data source for app settings
 */
internal class SettingsDataSource(context: Context) {

    private val SONG_SKIP_MULTIPLIER = intPreferencesKey("SONG_SKIP_MULTIPLIER")

    val songSkipMultiplier: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[SONG_SKIP_MULTIPLIER] ?: 66
    }

    private val RESPECT_AUDIO_FOCUS = booleanPreferencesKey("RESPECT_AUDIO_FOCUS")

    val respectAudioFocus: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[RESPECT_AUDIO_FOCUS] ?: false
    }

    /**
     * Saves the given settings.
     * Any past settings will be overwritten.
     *
     * @param context
     * @param newSettings The settings to save.
     */
    suspend fun saveSettings(
        context: Context,
        newSettings: Settings
    ) {
        context.dataStore.edit { settings ->
            settings[SONG_SKIP_MULTIPLIER] = newSettings.probabilityDownMultiplier
            settings[RESPECT_AUDIO_FOCUS] = newSettings.respectsAudioFocus
        }
    }

}