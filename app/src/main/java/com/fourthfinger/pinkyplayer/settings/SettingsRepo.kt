package com.fourthfinger.pinkyplayer.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepo @Inject constructor(private val settingsFileManager: SettingsFileManager) {

    private val _settings: MutableLiveData<Settings> by lazy {
        MutableLiveData<Settings>()
    }

    val settings = _settings as LiveData<Settings>

    suspend fun load(
            context: Context,
            fileNames: List<String>,
            saveFileVerificationNumber: Long,
    ): Settings {
        val settings = settingsFileManager.load(context, fileNames, saveFileVerificationNumber)
        _settings.postValue(settings)
        return settings
    }

    fun save(
            settings: Settings,
            context: Context,
            fileNames: List<String>,
            saveFileVerificationNumber: Long,
    ){
        this._settings.postValue(settings)
        settingsFileManager.save(settings, context, fileNames, saveFileVerificationNumber)
    }

}