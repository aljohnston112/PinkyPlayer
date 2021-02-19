package com.fourthfinger.pinkyplayer.songs

import android.app.Application
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt


interface LoadingCallback {
    fun setLoadingText(text: String)
    fun setLoadingProgress(progress: Double)
}

@HiltViewModel
class SongsViewModel @Inject constructor(
        app: Application,
        savedStateHandle: SavedStateHandle,
        private val songsRepo: SongsRepo,
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

    val loadingProgress get() = _loadingProgress as LiveData<Int>

    val isLoaded get() = _isLoaded as LiveData<Boolean>

    val songs = songsRepo.songs

    init {
        _isLoaded.postValue(false)
    }

    fun loadSongs() {
        if(!loadingStarted) {
            viewModelScope.launch(Dispatchers.IO) {
                _isLoaded.postValue(false)
                val songs = songsRepo.scanSongs(
                        getApplication<Application>().applicationContext,
                        this@SongsViewModel,
                )
                _isLoaded.postValue(true)
            }
        }
        loadingStarted = true
    }

    fun insertAll(vararg songs: Song) = viewModelScope.launch(Dispatchers.IO) { songsRepo.insertAll(*songs) }

    override fun setLoadingText(text: String) {
        _loadingText.postValue(text)
    }

    override fun setLoadingProgress(progress: Double) {
        _loadingProgress.postValue((progress * 100).roundToInt())
    }

    companion object{
        private var loadingStarted = false
    }

}


