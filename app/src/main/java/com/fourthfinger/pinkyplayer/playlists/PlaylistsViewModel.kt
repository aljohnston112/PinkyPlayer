package com.fourthfinger.pinkyplayer.playlists

import android.app.Application
import androidx.lifecycle.*
import com.fourthfinger.pinkyplayer.songs.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel  @Inject constructor(
        savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private lateinit var _masterPlaylist: RandomPlaylist

    private val masterPlaylistMLD: MutableLiveData<RandomPlaylist> by lazy {
        MutableLiveData<RandomPlaylist>()
    }

    val masterPlaylist = masterPlaylistMLD as LiveData<RandomPlaylist>

    fun updateSongs(songs: List<Song>, maxPercent: Double, ){
        if(!this::_masterPlaylist.isInitialized){
            val comparable = true
            _masterPlaylist = RandomPlaylist(MASTER_PLAYLIST_NAME, songs, maxPercent, comparable)
        } else{
            _masterPlaylist.updateSongs(songs)
        }
        masterPlaylistMLD.postValue(_masterPlaylist)
    }

    fun updateMaxPercent(maxPercent: Double){
        _masterPlaylist.setMaxPercent(maxPercent)
        masterPlaylistMLD.postValue(_masterPlaylist)
    }

    companion object{
        private const val MASTER_PLAYLIST_NAME: String = "MASTER_PLAYLIST_NAME"
    }
}