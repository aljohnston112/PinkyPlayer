package com.fourthfinger.pinkyplayer.playlists

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.songs.Song
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

// TODO add a cancel button
@AndroidEntryPoint
class DialogFragmentAddToPlaylist : DialogFragment() {

    interface DialogFragmentAddToPlaylistListener {
        fun onDialogPositiveClick(dialog: String, song: Song)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    private lateinit var callback: DialogFragmentAddToPlaylistListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as DialogFragmentAddToPlaylistListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement DialogFragmentAddToPlaylistListener"))
        }
    }

    private val viewModelPlaylist: PlaylistsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = requireArguments()
        lateinit var titles: Array<String>
        val value = viewModelPlaylist.playlists.value
        titles = if (value != null) {
            getPlaylistTitles(value)
        } else {
            arrayOf()
        }
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.add_to_playlist)
        val selectedPlaylistIndices: MutableList<Int> = mutableListOf()
        setUpChoices(builder, titles, selectedPlaylistIndices)
        setUpButtons(builder, bundle, titles, selectedPlaylistIndices)
        return builder.create()
    }

    private fun setUpChoices(
            builder: AlertDialog.Builder,
            titles: Array<String>,
            selectedPlaylistIndices: MutableList<Int>
    ) {
        builder.setMultiChoiceItems(titles, null)
        { _: DialogInterface?, which: Int, isChecked: Boolean ->
            if (isChecked) {
                selectedPlaylistIndices.add(which)
            } else {
                selectedPlaylistIndices.remove(which)
            }
        }
    }

    // TODO put in MediaData at some point...
    private fun getPlaylistTitles(randomPlaylists: List<RandomPlaylist>): Array<String> {
        val titles: MutableList<String> = ArrayList(randomPlaylists.size)
        for (randomPlaylist in randomPlaylists) {
            titles.add(randomPlaylist.name)
        }
        return Array(titles.size) { titles[it] }
    }

    private fun setUpButtons(
            builder: AlertDialog.Builder,
            bundle: Bundle,
            titles: Array<String>,
            selectedPlaylistIndices: List<Int>) {
        val song = bundle.getSerializable(BUNDLE_KEY_ADD_TO_PLAYLIST_SONG) as Song?
        val randomPlaylist = bundle.getSerializable(BUNDLE_KEY_ADD_TO_PLAYLIST_PLAYLIST) as RandomPlaylist?
        builder.setPositiveButton(R.string.add) { _: DialogInterface?, _: Int ->
            if (song != null) {
                for (index in selectedPlaylistIndices) {
                    callback.onDialogPositiveClick(titles[index], song)
                }
            }
            if (randomPlaylist != null) {
                for (randomPlaylistSong in randomPlaylist.songs()) {
                    for (index in selectedPlaylistIndices) {
                        callback.onDialogPositiveClick(titles[index], randomPlaylistSong)
                    }
                }
            }
        }
        builder.setNeutralButton(R.string.new_playlist) { dialog: DialogInterface?, which: Int ->
            // UserPickedPlaylist need to be null for FragmentEditPlaylist to make a new playlist
            /* TODO
            activityMain.setUserPickedPlaylist(null)
            activityMain.clearUserPickedSongs()
            if (song != null) {
                activityMain.addUserPickedSong(song)
            }
            if (randomPlaylist != null) {
                for (songInPlaylist in randomPlaylist.getSongs()) {
                    activityMain.addUserPickedSong(songInPlaylist)
                }
            }
            activityMain.navigateTo(R.id.fragmentEditPlaylist)
             */
        }
    }

    companion object {
        const val BUNDLE_KEY_ADD_TO_PLAYLIST_PLAYLIST = "ADD_TO_PLAYLIST_PLAYLIST"
        const val BUNDLE_KEY_ADD_TO_PLAYLIST_SONG = "ADD_TO_PLAYLIST_SONG"
    }

}