package io.fourth_finger.pinky_player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class FragmentMusicListViewModel(
    private val state: SavedStateHandle
) : ViewModel() {

    fun getSavedSearchText() = state.get<String>(SEARCH_TEXT_KEY)

    fun newSearch(searchText: String){
        state[SEARCH_TEXT_KEY] = searchText
    }

    companion object{
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
    }

}