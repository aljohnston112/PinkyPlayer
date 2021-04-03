package com.fourthfinger.pinkyplayer.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.fourthfinger.pinkyplayer.FileUtil
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.songs.LoadingCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
        app: Application,
        savedStateHandle: SavedStateHandle,
        private val settingsRepo: SettingsRepo
) : AndroidViewModel(app) {

    val settings = settingsRepo.settings

    fun loadSettings(loadingCallback: LoadingCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtil.mutex.withLock {
                loadingCallback.setLoadingProgress(0.5)
                loadingCallback.setLoadingText(
                        getApplication<Application>().applicationContext.getString(R.string.loadingSettings))
                runBlocking {
                    settingsRepo.load(getApplication())
                }
                loadingCallback.setLoadingProgress(1.0)
                loadingCallback.setSettingsLoaded(true)
            }
        }
    }

    fun save(settings: Settings) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepo.save(
                    settings,
                    getApplication<Application>().applicationContext,
            )
        }
    }

}