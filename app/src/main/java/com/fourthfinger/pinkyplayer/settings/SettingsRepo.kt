package com.fourthfinger.pinkyplayer.settings

import android.content.Context
import javax.inject.Inject

class SettingsRepo @Inject constructor(private val settingsFileManager: SettingsFileManager) {

    suspend fun load(context: Context,
                     fileNames: List<String>,
                     saveFileVerificationNumber: Long,
    ): Settings = settingsFileManager.load(context, fileNames, saveFileVerificationNumber)


    fun save(
            settings: Settings,
            context: Context,
            fileNames: List<String>,
            saveFileVerificationNumber: Long,
    ) = settingsFileManager.save(settings, context, fileNames, saveFileVerificationNumber)


}