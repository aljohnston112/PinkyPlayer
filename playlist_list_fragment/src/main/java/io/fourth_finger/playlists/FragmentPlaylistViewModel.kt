package io.fourth_finger.playlists

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fourth_finger.playlist_repository.PlaylistItem
import io.fourth_finger.playlist_repository.PlaylistRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentPlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val savedState: SavedStateHandle
) : ViewModel() {

    private var searchText = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val playlistItems = searchText.flatMapLatest { newText ->
        playlistRepository.playlistItems.map { playlistItems ->
            if (newText.isNotEmpty()) {
                getPlaylistsItemsWithText(
                    playlistItems,
                    newText
                )
            } else {
                playlistItems
            }
        }
    }

    private fun getPlaylistsItemsWithText(
        allPlaylists: List<PlaylistItem>,
        newText: String
    ): List<PlaylistItem> {
        val sifted: MutableList<PlaylistItem> = mutableListOf()
        for (playlistItems in allPlaylists) {
            if (playlistItems.name.lowercase().contains(newText.lowercase())) {
                sifted.add(playlistItems)
            }
        }
        return sifted
    }

    fun getSavedSearchText() = savedState.get<String>(SEARCH_TEXT_KEY)

    fun newSearch(searchText: String) {
        savedState[SEARCH_TEXT_KEY] = searchText
        this.searchText.value = searchText
    }

    /**
     * TODO
     *
     * @param context
     * @param id The id of the [io.fourth_finger.playlist_repository.PlaylistItem] corresponding to TODO.
     */
    fun playlistItemClicked(
        context: Context,
        id: Int
    ) {
        viewModelScope.launch {
            // TODO navigate to a Fragment that contains a list of songs in the playlist
        }
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
    }

}