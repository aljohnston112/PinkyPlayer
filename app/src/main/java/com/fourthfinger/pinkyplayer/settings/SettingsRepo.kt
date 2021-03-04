package com.fourthfinger.pinkyplayer.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fourthfinger.pinkyplayer.FileUtil
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

private const val MAX_PERCENT = 0.1
private const val PERCENT_CHANGE_UP = 0.5
private const val PERCENT_CHANGE_DOWN = 0.9
private const val LOWER_PROB = 0.0

@Singleton
class SettingsRepo @Inject constructor() {

    private val _settings: MutableLiveData<Settings> by lazy {
        MutableLiveData<Settings>()
    }

    val settings = _settings as LiveData<Settings>

    suspend fun load(
            context: Context,
            fileName: String,
            saveFileVerificationNumber: Long,
    ): Settings {
            var settings = FileUtil.load<Settings>(context, fileName, saveFileVerificationNumber)
            if (settings == null) {
                settings = Settings(MAX_PERCENT, PERCENT_CHANGE_UP, PERCENT_CHANGE_DOWN, LOWER_PROB)
            }
            _settings.postValue(settings)
            return settings
    }

    fun save(
            settings: Settings,
            context: Context,
            fileName: String,
            saveFileVerificationNumber: Long,
    ){
        this._settings.postValue(settings)
        FileUtil.save(settings, context, fileName, saveFileVerificationNumber)
    }

}