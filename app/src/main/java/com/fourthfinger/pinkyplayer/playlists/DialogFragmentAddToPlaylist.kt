package com.fourthfinger.pinkyplayer.playlists

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.findNavController
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.songs.Song
import dagger.hilt.android.AndroidEntryPoint

// TODO add a cancel button
@AndroidEntryPoint
class DialogFragmentAddToPlaylist : DialogFragment() {

    private val viewModelPlaylist: PlaylistsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = requireArguments()
        val playlistTitles = viewModelPlaylist.getPlaylistTitles()
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.add_to_playlist)
        val selectedPlaylistIndices: MutableList<Int> = mutableListOf()
        setUpChoices(builder, playlistTitles, selectedPlaylistIndices)
        setUpButtons(builder, bundle, playlistTitles, selectedPlaylistIndices)
        return builder.create()
    }

    private fun setUpChoices(
            builder: AlertDialog.Builder,
            playlistTitles: Array<String>,
            selectedPlaylistIndices: MutableList<Int>
    ) {
        builder.setMultiChoiceItems(playlistTitles, null)
        { _: DialogInterface?, which: Int, isChecked: Boolean ->
            if (isChecked) {
                selectedPlaylistIndices.add(which)
            } else {
                selectedPlaylistIndices.remove(which)
            }
        }
    }

    private fun setUpButtons(
            builder: AlertDialog.Builder,
            bundle: Bundle,
            playlistTitles: Array<String>,
            selectedPlaylistIndices: List<Int>) {
        val song = bundle.getSerializable(BUNDLE_KEY_ADD_TO_PLAYLIST_SONG) as Song?
        val randomPlaylist = bundle.getSerializable(BUNDLE_KEY_ADD_TO_PLAYLIST_PLAYLIST) as RandomPlaylist?
        builder.setPositiveButton(R.string.add) { _: DialogInterface?, _: Int ->
            if (song != null) {
                for (index in selectedPlaylistIndices) {
                    viewModelPlaylist.addSongsToPlaylist(playlistTitles[index], setOf(song))
                }
            }
            if (randomPlaylist != null) {
                for (index in selectedPlaylistIndices) {
                    viewModelPlaylist.addSongsToPlaylist(playlistTitles[index], randomPlaylist.songs())
                }
            }
        }
        builder.setNeutralButton(R.string.new_playlist) { _: DialogInterface?, _: Int ->
            startCreateNewPlaylist(randomPlaylist, song)
        }
    }

    private fun startCreateNewPlaylist(randomPlaylist: RandomPlaylist?, song: Song?) {
        // UserPickedPlaylist need to be null for FragmentEditPlaylist to make a new playlist
        viewModelPlaylist.setUserPickedPlaylist(null)
        viewModelPlaylist.clearUserPickedSongs()
        if (song != null) {
            viewModelPlaylist.addUserPickedSongs(song)
        }
        if (randomPlaylist != null) {
            viewModelPlaylist.addUserPickedSongs(*randomPlaylist.songs().toTypedArray())
        }
        requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.fragmentEditPlaylist)
    }

    companion object {
        const val BUNDLE_KEY_ADD_TO_PLAYLIST_PLAYLIST = "ADD_TO_PLAYLIST_PLAYLIST"
        const val BUNDLE_KEY_ADD_TO_PLAYLIST_SONG = "ADD_TO_PLAYLIST_SONG"
    }

}