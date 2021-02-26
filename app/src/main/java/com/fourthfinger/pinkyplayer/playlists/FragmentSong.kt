package com.fourthfinger.pinkyplayer.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.databinding.FragmentSongBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentSong(): Fragment(){

    private var _binding: FragmentSongBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mediaViewModel: MediaViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentSongBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeMedia()
    }

    private fun observeMedia() {
        mediaViewModel.currentAudioUri.observe(viewLifecycleOwner) {
            binding.textViewSongName.text = it.title
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}