package com.fourthfinger.pinkyplayer.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class LoadingCallback private constructor() {

    private val _songsLoaded: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    val songsLoaded get() = _songsLoaded as LiveData<Boolean>
    fun setSongsLoaded(loaded: Boolean) {
        _songsLoaded.postValue(loaded)
    }

    private val _playlistsLoaded: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    val playlistsLoaded get() = _playlistsLoaded as LiveData<Boolean>
    fun setPlaylistsLoaded(loaded: Boolean) {
        _playlistsLoaded.postValue(loaded)
    }

    private val _settingsLoaded: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    val settingsLoaded get() = _settingsLoaded as LiveData<Boolean>
    fun setSettingsLoaded(loaded: Boolean) {
        _settingsLoaded.postValue(loaded)
    }

    private val _loadingText: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val loadingText get() = _loadingText as LiveData<String>
    fun setLoadingText(text: String) {
        _loadingText.postValue(text)
    }

    private val _loadingProgress: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val loadingProgress get() = _loadingProgress as LiveData<Int>
    fun setLoadingProgress(progress: Double) {
        _loadingProgress.postValue((progress * 100).roundToInt())
    }

    companion object {
        private val loadingCallback = LoadingCallback()
        fun getInstance() = loadingCallback
    }

}