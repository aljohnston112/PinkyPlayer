package com.fourthfinger.pinkyplayer.songs

import android.app.Application
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt


interface LoadingCallback {
    fun setLoadingText(text: String)
    fun setLoadingProgress(progress: Double)
}

@HiltViewModel
class SongsViewModel @Inject constructor(
        app: Application,
        savedStateHandle: SavedStateHandle,
        private val songRepo: SongRepo,
) : AndroidViewModel(app), LoadingCallback {

    private val _loadingText: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private val _loadingProgress: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    private val _isLoaded:  MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val loadingText get() = _loadingText as LiveData<String>

    override fun setLoadingText(text: String) {
        _loadingText.postValue(text)
    }

    val loadingProgress get() = _loadingProgress as LiveData<Int>

    override fun setLoadingProgress(progress: Double) {
        _loadingProgress.postValue((progress * 100).roundToInt())
    }

    val isLoaded get() = _isLoaded as LiveData<Boolean>

    val songs = songRepo.songs

    init {
        _isLoaded.postValue(false)
    }

    private var loadingStarted = false

    fun loadSongs() {
        if(!loadingStarted) {
            viewModelScope.launch(Dispatchers.IO) {
                _isLoaded.postValue(false)
                val songs = songRepo.scanSongs(
                        getApplication<Application>().applicationContext,
                        this@SongsViewModel,
                )
                _isLoaded.postValue(true)
            }
        }
        loadingStarted = true
    }

    fun insertAll(vararg songs: Song) = viewModelScope.launch(Dispatchers.IO) { songRepo.insertAll(*songs) }

}


