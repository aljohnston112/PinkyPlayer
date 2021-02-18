package com.fourthfinger.pinkyplayer.playlists

import android.app.Application
import androidx.lifecycle.*
import com.fourthfinger.pinkyplayer.settings.SettingsRepo
import com.fourthfinger.pinkyplayer.songs.Song
import com.fourthfinger.pinkyplayer.songs.SongsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
        savedStateHandle: SavedStateHandle,
        songsRepo: SongsRepo,
        settingsRepo: SettingsRepo,
) : ViewModel() {

    private lateinit var _masterPlaylist: RandomPlaylist

    private val masterPlaylistMLD: MutableLiveData<RandomPlaylist> by lazy {
        MutableLiveData<RandomPlaylist>()
    }

    val masterPlaylist = masterPlaylistMLD as LiveData<RandomPlaylist>

    init {
        viewModelScope.launch {
            val comparable = true
            var songs: List<Song>? = songsRepo.songs.first()
            var tempSongs: List<Song>? = songs
            while (tempSongs != null) {
                songs = tempSongs
                tempSongs = songsRepo.songs.firstOrNull()
            }
            val maxPercent = settingsRepo.settings()?.maxPercent!!
            _masterPlaylist = RandomPlaylist(MASTER_PLAYLIST_NAME, songs!!, maxPercent, comparable)
            masterPlaylistMLD.postValue(_masterPlaylist)
        }
    }

    fun updateMaxPercent(maxPercent: Double) {
        _masterPlaylist.setMaxPercent(maxPercent)
        masterPlaylistMLD.postValue(_masterPlaylist)
    }

    companion object {
        private const val MASTER_PLAYLIST_NAME: String = "MASTER_PLAYLIST_NAME"
    }
}