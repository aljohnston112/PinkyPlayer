package com.fourthfinger.pinkyplayer.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.databinding.FragmentEditPlaylistBinding

class FragmentEditPlaylist : Fragment() {

    private var _binding: FragmentEditPlaylistBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModelPlaylists: PlaylistsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPlaylistBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
    }

    // TODO how is userPickedSongs going to get the songs in the userPickedPlaylist?
    private fun observe() {
        viewModelPlaylists.userPickedPlaylist.observe(viewLifecycleOwner){
            if (it != null) {
                binding.editTextPlaylistName.setText(it.name)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}