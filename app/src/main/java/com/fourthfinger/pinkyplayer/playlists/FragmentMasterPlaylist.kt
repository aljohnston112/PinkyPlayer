package com.fourthfinger.pinkyplayer.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fourthfinger.pinkyplayer.ActivityMain
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.databinding.RecyclerViewSongListBinding
import com.fourthfinger.pinkyplayer.settings.SettingsViewModel
import com.fourthfinger.pinkyplayer.songs.Song
import com.fourthfinger.pinkyplayer.songs.SongsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class FragmentMasterPlaylist : Fragment(), ListenerCallbackSongs {

    private var _binding: RecyclerViewSongListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModelSongs: SongsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private val playlistsViewModel: PlaylistsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private val settingsViewModel: SettingsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private lateinit var recyclerViewAdapterSongs : RecyclerViewAdapterSongs

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = RecyclerViewSongListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpRecyclerView()
        observeSongs()
    }

    private fun setUpRecyclerView() {
        val recyclerViewSongs = binding.recyclerViewSongList
        recyclerViewAdapterSongs = RecyclerViewAdapterSongs(this)
        recyclerViewSongs.layoutManager = LinearLayoutManager(recyclerViewSongs.context)
        recyclerViewSongs.adapter = recyclerViewAdapterSongs
    }

    private fun observeSongs() {
        viewModelSongs.songs.observe(viewLifecycleOwner, { songs ->
            val sortedSongs =  songs.toMutableList()
            sortedSongs.sort()
            playlistsViewModel.updateSongs(sortedSongs, settingsViewModel.settings.value!!.maxPercent)
            recyclerViewAdapterSongs.updateList(sortedSongs)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickViewHolder(song: Song?) {
        TODO("Not yet implemented")
    }

    override fun onMenuItemClickAddToPlaylist(song: Song?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onMenuItemClickAddToQueue(song: Song?): Boolean {
        TODO("Not yet implemented")
    }

}