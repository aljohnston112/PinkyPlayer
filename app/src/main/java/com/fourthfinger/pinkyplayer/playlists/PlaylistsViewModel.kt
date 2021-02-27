package com.fourthfinger.pinkyplayer.playlists

import android.app.Application
import androidx.lifecycle.*
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.settings.Settings
import com.fourthfinger.pinkyplayer.settings.SettingsRepo
import com.fourthfinger.pinkyplayer.songs.LoadingCallback
import com.fourthfinger.pinkyplayer.songs.Song
import com.fourthfinger.pinkyplayer.songs.SongRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MASTER_PLAYLIST_FILE_NAME = "MASTER_PLAYLIST_NAME"
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

    fun loadPlaylists(loadingCallback: LoadingCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingCallback.setLoadingProgress(0.0)
            loadingCallback.setLoadingProgress(0.25)
            loadingCallback.setLoadingProgress(0.5)
            loadingCallback.setLoadingText(
                    getApplication<Application>().applicationContext.getString(R.string.loadingPlaylists))
            playlistRepo.loadPlaylist(
                    getApplication(), MASTER_PLAYLIST_FILE_NAME, SAVE_FILE_VERIFICATION_NUMBER
            )
            loadingCallback.setLoadingProgress(0.75)
            loadingCallback.setLoadingProgress(1.0)
            loadingCallback.setPlaylistsLoaded(true)
        }
    }

    private val songObserver: Observer<List<Song>> = Observer<List<Song>> {
        viewModelScope.launch(Dispatchers.IO) {
            if (it.isNotEmpty()) {
                if (maxPercent == -1.0) {
                    maxPercent = (1.0 - ((it.size) * MIN_VALUE))
                }
                if (!this@PlaylistsViewModel::_masterPlaylist.isInitialized) {
                    val comparable = true
                    _masterPlaylist = RandomPlaylist(MASTER_PLAYLIST_FILE_NAME, it, maxPercent, comparable)
                } else {
                    _masterPlaylist.updateSongs(it)
                }
                masterPlaylistMLD.postValue(_masterPlaylist)
                playlistRepo.savePlaylist(
                        _masterPlaylist, getApplication(),
                        MASTER_PLAYLIST_FILE_NAME, SAVE_FILE_VERIFICATION_NUMBER
                )
            }
        }
    }

    private val settingsObserver: Observer<Settings> = Observer<Settings> {
        viewModelScope.launch(Dispatchers.IO) {
            maxPercent = it.maxPercent
            if (this@PlaylistsViewModel::_masterPlaylist.isInitialized) {
                if (maxPercent == 1.0) {
                    maxPercent = (1.0 - (_masterPlaylist.size() * MIN_VALUE))
                }
                this@PlaylistsViewModel._masterPlaylist.setMaxPercent(maxPercent)
                masterPlaylistMLD.postValue(_masterPlaylist)
                playlistRepo.savePlaylist(
                        _masterPlaylist, getApplication(),
                        MASTER_PLAYLIST_FILE_NAME, SAVE_FILE_VERIFICATION_NUMBER
                )
            }
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