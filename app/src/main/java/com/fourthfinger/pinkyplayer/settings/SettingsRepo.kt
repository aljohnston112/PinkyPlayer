package com.fourthfinger.pinkyplayer.settings

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepo @Inject constructor(private val settingsFileManager: SettingsFileManager) {

    @Volatile
    private var _settingsCache: Settings? = null

    fun settings() = _settingsCache

    suspend fun load(context: Context,
                     fileNames: List<String>,
                     saveFileVerificationNumber: Long,
    ): Settings {
        _settingsCache = settingsFileManager.load(context, fileNames, saveFileVerificationNumber)
        return _settingsCache!!
    }

    fun save(
            settings: Settings,
            context: Context,
            fileNames: List<String>,
            saveFileVerificationNumber: Long,
    ) = settingsFileManager.save(settings, context, fileNames, saveFileVerificationNumber)


}