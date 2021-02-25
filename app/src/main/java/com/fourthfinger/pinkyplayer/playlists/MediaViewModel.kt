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

}