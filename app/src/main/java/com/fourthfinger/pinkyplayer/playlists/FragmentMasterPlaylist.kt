package com.fourthfinger.pinkyplayer.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fourthfinger.pinkyplayer.NavUtil
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.databinding.RecyclerViewSongListBinding
import com.fourthfinger.pinkyplayer.songs.Song
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentMasterPlaylist : Fragment(), ListenerCallbackSongs {

    private var _binding: RecyclerViewSongListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModelPlaylist: PlaylistsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private val mediaViewModel: MediaViewModel by hiltNavGraphViewModels(R.id.nav_graph)

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
        viewModelPlaylist.masterPlaylist.observe(viewLifecycleOwner) { playlist ->
            /*
                val sortedSongs = playlist.songs().toMutableList()
                sortedSongs.sort()

             */
            recyclerViewAdapterSongs.updateList(playlist.songs())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickViewHolder(song: Song) {
        mediaViewModel.setCurrentSong(requireContext(), song)
        NavUtil.safeNav(
                this, R.id.fragmentMasterPlaylist,
                FragmentMasterPlaylistDirections.actionFragmentMasterPlaylistToFragmentSong()
        )
    }

    override fun onMenuItemClickAddToPlaylist(song: Song): Boolean {
        TODO("Not yet implemented")
    }

    override fun onMenuItemClickAddToQueue(song: Song): Boolean {
        TODO("Not yet implemented")
    }

}