package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fourthfinger.pinkyplayer.songs.AudioUri
import com.fourthfinger.pinkyplayer.songs.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
        private val mediaController: MediaController,
) : ViewModel() {

    private val _currentAudioUri: MutableLiveData<AudioUri> by lazy {
        MutableLiveData<AudioUri>()
    }

    val currentAudioUri = _currentAudioUri as LiveData<AudioUri>

    fun setCurrentSong(context: Context, song: Song){
        _currentAudioUri.postValue(AudioUri.getAudioUri(context, song.id))
    }

    private val _isPlaying: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val isPlaying = _isPlaying as LiveData<Boolean>

    fun setIsPlaying(context: Context, playing: Boolean){
        _isPlaying.postValue(playing)
    }

    private val _looping: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val looping = _looping as LiveData<Boolean>

    fun setLooping(context: Context, looping: Boolean){
        _looping.postValue(looping)
    }

    private val _loopingOne: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val loopingOne = _loopingOne as LiveData<Boolean>

    fun setLoopingOne(context: Context, loopingOne: Boolean){
        _loopingOne.postValue(loopingOne)
    }

    private val _shuffling: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }

    val shuffling = _shuffling as LiveData<Boolean>

    fun setShuffling(context: Context, shuffling: Boolean){
        _shuffling.postValue(shuffling)
    }

}