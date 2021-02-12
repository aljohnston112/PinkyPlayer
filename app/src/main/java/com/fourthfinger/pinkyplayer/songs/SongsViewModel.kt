package com.fourthfinger.pinkyplayer.songs

import android.app.Application
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SongsViewModel @Inject constructor(
        private val app: Application,
        // savedStateHandle: SavedStateHandle,
       // private val songsRepo: SongsRepo,
        ) : ViewModel() {

    // val songs = songsRepo.songs.asLiveData()

   // fun insert(vararg songs : Song) = viewModelScope.launch { songsRepo.insertAll(*songs) }

}


