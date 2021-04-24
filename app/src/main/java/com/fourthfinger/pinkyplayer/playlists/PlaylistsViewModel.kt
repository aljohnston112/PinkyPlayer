package com.fourthfinger.pinkyplayer.playlists

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.Observer
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
import java.util.*
import javax.inject.Inject

private const val MASTER_PLAYLIST_NAME = "MASTER_PLAYLIST_NAME"

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

    private val playlistsBeforeLoad = mutableSetOf<RandomPlaylist>()
    private val playlistsToRemove = mutableSetOf<RandomPlaylist>()
    private lateinit var _playlists: MutableSet<RandomPlaylist>
    private val playlistsMLD: MutableLiveData<Set<RandomPlaylist>> by lazy {
        MutableLiveData<Set<RandomPlaylist>>()
    }
    val playlists = playlistsMLD as LiveData<Set<RandomPlaylist>>
    fun getPlaylistTitles(): Array<String> {
        val titles: MutableList<String> = ArrayList(_playlists.size)
        for (randomPlaylist in _playlists) {
            titles.add(randomPlaylist.name)
        }
        return Array(titles.size) { titles[it] }
    }

    fun addSongsToPlaylist(playlistTitle: String, songs: Set<Song>) {
        for (p in _playlists) {
            if (p.name == playlistTitle) {
                for (s in songs) {
                    p.add(s)
                }
            }
        }
        var changed = false
        for (s in songs) {
            if (!_masterPlaylist.contains(s)){
                _masterPlaylist.add(s)
                changed = true
            }
        }
        if(changed) {
            masterPlaylistMLD.postValue(_masterPlaylist)
            playlistRepo.saveMasterPlaylist(_masterPlaylist, getApplication())
        }
        viewModelScope.launch(Dispatchers.IO) {
            FileUtil.mutex.withLock {
                playlistsMLD.postValue(_playlists)
                playlistRepo.savePlaylists(_playlists.toList(), getApplication())
            }
        }
    }

    private val userPickedPlaylistMLD: MutableLiveData<RandomPlaylist?> by lazy {
        MutableLiveData<RandomPlaylist?>(null)
    }
    val userPickedPlaylist = userPickedPlaylistMLD as LiveData<RandomPlaylist?>
    fun setUserPickedPlaylist(randomPlaylist: RandomPlaylist?) {
        userPickedPlaylistMLD.postValue(randomPlaylist)
    }

    private val _userPickedSongs = mutableSetOf<Song>()
    private val userPickedSongsMLD: MutableLiveData<Set<Song>> by lazy {
        MutableLiveData<Set<Song>>(_userPickedSongs)
    }
    val userPickedSongs = userPickedSongsMLD as LiveData<Set<Song>>
    fun addUserPickedSongs(vararg songs: Song) {
        _userPickedSongs.addAll(songs)
        userPickedSongsMLD.postValue(_userPickedSongs)
    }

    fun clearUserPickedSongs() {
        _userPickedSongs.clear()
        userPickedSongsMLD.postValue(_userPickedSongs)
    }

    fun loadPlaylists(loadingCallback: LoadingCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtil.mutex.withLock {
                loadingCallback.setLoadingProgress(0.5)
                loadingCallback.setLoadingText(
                        getApplication<Application>().applicationContext.getString(R.string.loadingPlaylists))
                runBlocking {
                    masterPlaylistMLD.postValue(playlistRepo.loadMasterPlaylist(getApplication()))
                    val ps = playlistRepo.loadPlaylists(getApplication())
                    _playlists = mutableSetOf()
                    if (ps != null) {
                        for (p in ps) {
                            _playlists.add(p)
                        }
                    }
                    var save = false
                    if (playlistsBeforeLoad.isNotEmpty()) {
                        for (rp in playlistsBeforeLoad) {
                            _playlists.add(rp)
                        }
                        playlistsBeforeLoad.clear()
                        save = true
                    }
                    if(playlistsToRemove.isNotEmpty()){
                        for(rp in playlistsToRemove){
                            _playlists.remove(rp)
                        }
                        playlistsToRemove.clear()
                        save = true
                    }
                    if(save){
                        viewModelScope.launch(Dispatchers.IO) {
                            FileUtil.mutex.withLock {
                                playlistRepo.savePlaylists(_playlists.toList(), getApplication())
                            }
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
        if (this::_playlists.isInitialized) {
            _playlists.add(randomPlaylist)
            playlistsMLD.postValue(_playlists)
            viewModelScope.launch(Dispatchers.IO) {
                FileUtil.mutex.withLock {
                    playlistRepo.savePlaylists(_playlists.toList(), getApplication())
                }
            }
        } else {
            playlistsBeforeLoad.add(randomPlaylist)
        }
    }

    fun deletePlaylist(randomPlaylist: RandomPlaylist) {
        if (this::_playlists.isInitialized) {
            _playlists.remove(randomPlaylist)
            viewModelScope.launch(Dispatchers.IO) {
                FileUtil.mutex.withLock {
                    playlistsMLD.postValue(_playlists)
                    playlistRepo.savePlaylists(_playlists.toList(), getApplication())
                }
            }
        } else {
            playlistsToRemove.add(randomPlaylist)
        }
    }

    private val songObserver: Observer<List<Song>> = Observer {
        viewModelScope.launch(Dispatchers.IO) {
            if (it.isNotEmpty()) {
                FileUtil.mutex.withLock {
                    if (!this@PlaylistsViewModel::_masterPlaylist.isInitialized) {
                        val comparable = true
                        _masterPlaylist = RandomPlaylist(MASTER_PLAYLIST_NAME, it.toSet(), maxPercent, comparable)
                    } else {
                        _masterPlaylist.updateSongs(it.toSet())
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
                    for (rp in _playlists) {
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

}