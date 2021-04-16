package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fourthfinger.pinkyplayer.songs.AudioUri
import com.fourthfinger.pinkyplayer.songs.Song
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaController @Inject constructor() {

    enum class Loop {
        NONE, ALL, ONE
    }

    private val _currentAudioUri: MutableLiveData<AudioUri> by lazy {
        MutableLiveData<AudioUri>()
    }
    val currentAudioUri = _currentAudioUri as LiveData<AudioUri>
    fun setCurrentSong(context: Context, song: Song) {
        _currentAudioUri.postValue(AudioUri.getAudioUri(context, song.id))
    }

    private val _isPlaying: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    val isPlaying = _isPlaying as LiveData<Boolean>
    fun toggleIsPlaying() {
        if (_isPlaying.value == true) {
            _isPlaying.postValue(false)
        } else {
            _isPlaying.postValue(true)
        }
    }

    private val _looping: MutableLiveData<Loop> by lazy {
        MutableLiveData<Loop>(Loop.NONE)
    }
    val looping = _looping as LiveData<Loop>

    fun toggleLooping() {
        when (_looping.value) {
            Loop.NONE -> _looping.postValue(Loop.ALL)
            Loop.ALL -> _looping.postValue(Loop.ONE)
            else -> _looping.postValue(Loop.NONE)
        }
    }

    private val _shuffling: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }
    val shuffling = _shuffling as LiveData<Boolean>
    fun toggleShuffling() {
        if (_shuffling.value == true) {
            _shuffling.postValue(false)
        } else {
            _shuffling.postValue(true)
        }
    }


}