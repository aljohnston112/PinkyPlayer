package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fourthfinger.pinkyplayer.songs.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
        private val mediaController: MediaController,
): ViewModel() {

    val looping = mediaController.looping
    fun toggleLooping(){
        mediaController.toggleLooping()
    }

    val shuffling = mediaController.shuffling
    fun toggleShuffling(){
        mediaController.toggleShuffling()
    }

    val isPlaying = mediaController.isPlaying
    fun toggleIsPlaying(){
        mediaController.toggleIsPlaying()
    }
    val currentAudioUri = mediaController.currentAudioUri
    fun setCurrentSong(context: Context, song: Song){
        mediaController.setCurrentSong(context, song)
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