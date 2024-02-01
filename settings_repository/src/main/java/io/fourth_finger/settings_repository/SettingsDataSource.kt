package io.fourth_finger.settings_repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * A data source for app settings
 */
internal class SettingsDataSource(context: Context) {

    private val SONG_SKIP_MULTIPLIER = longPreferencesKey("SONG_SKIP_MULTIPLIER")

    val songSkipMultiplier: Flow<Long> = context.dataStore.data.map {
        it[SONG_SKIP_MULTIPLIER] ?: 3
    }
    /**
     * Saves the given settings.
     * Any past settings will be overwritten.
     *
     * @param context
     * @param settings The settings to save.
     */
    suspend fun saveSettings(context: Context, settings: Settings) {
        context.dataStore.edit {
            it[SONG_SKIP_MULTIPLIER] = settings.skipMultiplier
        }
    }

}