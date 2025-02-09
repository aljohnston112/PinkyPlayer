package io.fourth_finger.pinky_player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fourth_finger.music_repository.MusicItem
import io.fourth_finger.music_repository.MusicRepository
import javax.inject.Inject

@HiltViewModel
class FragmentMusicListViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private var searchText = MutableLiveData("")

    private val _musicItems = MediatorLiveData<List<MusicItem>>()
    val musicItems = _musicItems as LiveData<List<MusicItem>>

    init {
        _musicItems.addSource(musicRepository.musicItems) { songs ->
            _musicItems.postValue(
                getSiftedSongs(
                    songs,
                    searchText.value.orEmpty()
                )
            )
        }
        _musicItems.addSource(searchText) { newText ->
            _musicItems.postValue(
                getSiftedSongs(
                    musicRepository.musicItems.value.orEmpty(),
                    newText
                )
            )
        }
    }

    private fun getSiftedSongs(
        songs: List<MusicItem>,
        newText: String
    ): List<MusicItem> {
        val list: List<MusicItem>
        if (newText.isNotEmpty()) {
            val sifted = mutableListOf<MusicItem>()
            for (song in songs) {
                if (song.fullPath.lowercase().contains(newText.lowercase())) {
                    sifted.add(song)
                }
            }
            list = sifted
        } else {
            list = songs
        }
        return list
    }

    fun getSavedSearchText() = state.get<String>(SEARCH_TEXT_KEY)

    fun newSearch(searchText: String) {
        state[SEARCH_TEXT_KEY] = searchText
        this.searchText.postValue(searchText)
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
    }

}