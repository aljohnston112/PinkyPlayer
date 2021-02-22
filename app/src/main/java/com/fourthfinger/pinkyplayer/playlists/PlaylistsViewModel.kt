package com.fourthfinger.pinkyplayer.playlists

import android.app.Application
import androidx.lifecycle.*
import com.fourthfinger.pinkyplayer.settings.Settings
import com.fourthfinger.pinkyplayer.settings.SettingsRepo
import com.fourthfinger.pinkyplayer.songs.Song
import com.fourthfinger.pinkyplayer.songs.SongRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val FILE_SAVE = "MASTER_PLAYLIST_NAME"
private const val FILE_SAVE2 = "MASTER_PLAYLIST_NAME2"
private const val FILE_SAVE3 = "MASTER_PLAYLIST_NAME3"
private val MASTER_PLAYLIST_FILES = listOf(FILE_SAVE, FILE_SAVE2, FILE_SAVE3)
private const val SAVE_FILE_VERIFICATION_NUMBER = 8479145830949658990L

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
        application: Application,
        savedStateHandle: SavedStateHandle,
        private val songRepo: SongRepo,
        private val settingsRepo: SettingsRepo,
        private val playlistRepo: PlaylistRepo,
) : AndroidViewModel(application) {

    private var maxPercent: Double = -1.0

    private lateinit var _masterPlaylist: RandomPlaylist

    private val masterPlaylistMLD: MutableLiveData<RandomPlaylist> by lazy {
        MutableLiveData<RandomPlaylist>()
    }

    val masterPlaylist = masterPlaylistMLD as LiveData<RandomPlaylist>

    fun loadPlaylists(){
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepo.loadPlaylist(
                    getApplication(), MASTER_PLAYLIST_FILES, SAVE_FILE_VERIFICATION_NUMBER
            )
        }
    }

    private val songObserver: Observer<List<Song>> = Observer<List<Song>> {
        if (it.isNotEmpty()) {
            if (maxPercent == -1.0) {
                val a = ((it.size) * MIN_VALUE)
                maxPercent = (1.0 - ((it.size) * MIN_VALUE))
            }
            if (!this::_masterPlaylist.isInitialized) {
                val comparable = true
                _masterPlaylist = RandomPlaylist(FILE_SAVE, it, maxPercent, comparable)
            } else {
                _masterPlaylist.updateSongs(it)
            }
            masterPlaylistMLD.postValue(_masterPlaylist)
            playlistRepo.savePlaylist(
                    _masterPlaylist, getApplication(),
                    MASTER_PLAYLIST_FILES, SAVE_FILE_VERIFICATION_NUMBER
            )
        }
    }

    private val settingsObserver: Observer<Settings> = Observer<Settings> {
        maxPercent = it.maxPercent
        if (this::_masterPlaylist.isInitialized) {
            if (maxPercent == 1.0) {
                maxPercent = (1.0 - (_masterPlaylist.size() * MIN_VALUE))
            }
            this._masterPlaylist.setMaxPercent(maxPercent)
            masterPlaylistMLD.postValue(_masterPlaylist)
            playlistRepo.savePlaylist(
                    _masterPlaylist, getApplication(),
                    MASTER_PLAYLIST_FILES, SAVE_FILE_VERIFICATION_NUMBER
            )
        }
    }

    init {
        songRepo.songs.observeForever(songObserver)
        settingsRepo.settings.observeForever(settingsObserver)
    }

    override fun onCleared() {
        super.onCleared()
        songRepo.songs.removeObserver(songObserver)
        settingsRepo.settings.removeObserver(settingsObserver)
    }

}