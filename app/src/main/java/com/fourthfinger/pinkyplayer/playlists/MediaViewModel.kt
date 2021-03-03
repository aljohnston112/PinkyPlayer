package com.fourthfinger.pinkyplayer.playlists

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fourthfinger.pinkyplayer.songs.AudioUri
import com.fourthfinger.pinkyplayer.songs.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
        application: Application,
        private val mediaController: MediaController,
) : AndroidViewModel(application) {

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

    fun setIsPlaying(playing: Boolean){
        _isPlaying.postValue(playing)
    }

    private val _looping: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val looping = _looping as LiveData<Boolean>

    fun setLooping(looping: Boolean){
        _looping.postValue(looping)
    }

    private val _loopingOne: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val loopingOne = _loopingOne as LiveData<Boolean>

    fun setLoopingOne(loopingOne: Boolean){
        _loopingOne.postValue(loopingOne)
    }

    private val _shuffling: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }

    val shuffling = _shuffling as LiveData<Boolean>

    fun setShuffling(shuffling: Boolean){
        _shuffling.postValue(shuffling)
    }

    private val _currentSongBitmap: MutableLiveData<Bitmap> by lazy {
        MutableLiveData<Bitmap>()
    }

    val currentSongBitmap = _currentSongBitmap as LiveData<Bitmap>

    fun setCurrentSongBitmap(bitmap: Bitmap){
        _currentSongBitmap.postValue(bitmap)
    }

    private val _currentSongTime: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val currentSongTime = _currentSongTime as LiveData<String>

    fun setCurrentSongTime(currentTime: String){
        _currentSongTime.postValue(currentTime)
    }

    private val _currentSongEndTime: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val currentSongEndTime = _currentSongEndTime as LiveData<String>

    fun setCurrentSongEndTime(currentEndTime: String){
        _currentSongEndTime.postValue(currentEndTime)
    }

}