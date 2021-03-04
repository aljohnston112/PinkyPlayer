package com.fourthfinger.pinkyplayer.songs

import android.app.Application
import androidx.lifecycle.*
import com.fourthfinger.pinkyplayer.FileUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
        app: Application,
        savedStateHandle: SavedStateHandle,
        private val songRepo: SongRepo,
) : AndroidViewModel(app) {

    val songs = songRepo.songs

    private var loadingStarted = false

    fun loadSongs(loadingCallback: LoadingCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtil.mutex.withLock {
                if (!loadingStarted) {
                    runBlocking {
                        val songs = songRepo.scanSongsAndWriteAudioUris(
                                getApplication<Application>().applicationContext,
                                loadingCallback,
                        )
                        loadingCallback.setSongsLoaded(true)
                    }
                    loadingStarted = true
                }
            }
        }
    }

}


