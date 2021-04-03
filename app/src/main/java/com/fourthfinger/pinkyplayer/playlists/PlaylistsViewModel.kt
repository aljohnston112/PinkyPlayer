package com.fourthfinger.pinkyplayer.playlists

import android.app.Application
import androidx.lifecycle.*
import com.fourthfinger.pinkyplayer.FileUtil
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.settings.Settings
import com.fourthfinger.pinkyplayer.settings.SettingsRepo
import com.fourthfinger.pinkyplayer.songs.LoadingCallback
import com.fourthfinger.pinkyplayer.songs.Song
import com.fourthfinger.pinkyplayer.songs.SongRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
        application: Application,
        savedStateHandle: SavedStateHandle,
        private val songRepo: SongRepo,
        private val settingsRepo: SettingsRepo,
        private val playlistRepo: PlaylistRepo,
) : AndroidViewModel(application) {

    private var maxPercent: Double = 1.0

    private lateinit var _masterPlaylist: RandomPlaylist
    private val masterPlaylistMLD: MutableLiveData<RandomPlaylist> by lazy {
        MutableLiveData<RandomPlaylist>()
    }
    val masterPlaylist = masterPlaylistMLD as LiveData<RandomPlaylist>

    private lateinit var _playlists: MutableList<RandomPlaylist>
    private val playlistsMLD: MutableLiveData<List<RandomPlaylist>> by lazy {
        MutableLiveData<List<RandomPlaylist>>()
    }
    val playlists = playlistsMLD as LiveData<List<RandomPlaylist>>

    fun loadPlaylists(loadingCallback: LoadingCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtil.mutex.withLock {
                loadingCallback.setLoadingProgress(0.5)
                loadingCallback.setLoadingText(
                        getApplication<Application>().applicationContext.getString(R.string.loadingPlaylists))
                runBlocking {
                    masterPlaylistMLD.postValue(playlistRepo.loadMasterPlaylist(getApplication()))
                    val ps = playlistRepo.loadPlaylists(getApplication())
                    _playlists = mutableListOf()
                    if (ps != null) {
                        for(p in ps){
                            _playlists.add(p)
                        }
                    }
                    playlistsMLD.postValue(_playlists)
                }
                loadingCallback.setLoadingProgress(1.0)
                loadingCallback.setPlaylistsLoaded(true)
            }
        }
    }

    fun savePlaylist(randomPlaylist: RandomPlaylist) {
        _playlists.add(randomPlaylist)
        viewModelScope.launch(Dispatchers.IO) {
            FileUtil.mutex.withLock {
                playlistsMLD.postValue(_playlists)
                playlistRepo.savePlaylists(_playlists, getApplication())
            }
        }
    }

    fun deletePlaylist(randomPlaylist: RandomPlaylist){
        _playlists.remove(randomPlaylist)
        viewModelScope.launch(Dispatchers.IO) {
            FileUtil.mutex.withLock {
                playlistsMLD.postValue(_playlists)
                playlistRepo.savePlaylists(_playlists, getApplication())
            }
        }
    }

    private val songObserver: Observer<List<Song>> = Observer {
        viewModelScope.launch(Dispatchers.IO) {
            if (it.isNotEmpty()) {
                FileUtil.mutex.withLock {
                    if (!this@PlaylistsViewModel::_masterPlaylist.isInitialized) {
                        val comparable = true
                        _masterPlaylist = RandomPlaylist(MASTER_PLAYLIST_NAME, it, maxPercent, comparable)
                    } else {
                        _masterPlaylist.updateSongs(it)
                    }
                    masterPlaylistMLD.postValue(_masterPlaylist)
                    playlistRepo.saveMasterPlaylist(_masterPlaylist, getApplication())
                }
            }
        }
    }

    private val settingsObserver: Observer<Settings> = Observer {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtil.mutex.withLock {
                maxPercent = it.maxPercent
                if (this@PlaylistsViewModel::_masterPlaylist.isInitialized) {
                    this@PlaylistsViewModel._masterPlaylist.setMaxPercent(maxPercent)
                    for(rp in _playlists){
                        rp.setMaxPercent(maxPercent)
                    }
                    masterPlaylistMLD.postValue(_masterPlaylist)
                    playlistRepo.saveMasterPlaylist(_masterPlaylist, getApplication())
                }
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

    companion object {
        const val MASTER_PLAYLIST_NAME = "MASTER_PLAYLIST_NAME"
    }

}