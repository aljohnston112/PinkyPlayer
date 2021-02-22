package com.fourthfinger.pinkyplayer.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.databinding.FragmentLoadingBinding
import com.fourthfinger.pinkyplayer.playlists.PlaylistsViewModel
import com.fourthfinger.pinkyplayer.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentLoading : Fragment() {

    private var _binding: FragmentLoadingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModelSongs: SongsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private val viewModelSettings: SettingsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private val viewModelPlaylists: PlaylistsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentLoadingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelSettings.loadSettings()
        viewModelSongs.loadSongs()
        viewModelPlaylists.loadPlaylists()
        observeLoadingProgress()
    }

    private fun observeLoadingProgress() {
        viewModelSongs.loadingProgress.observe(viewLifecycleOwner, { progress ->
            binding.progressBarLoading.progress = progress
        })
        viewModelSongs.loadingText.observe(viewLifecycleOwner, { text ->
            binding.textViewLoading.text = text
        })
        val loaded = MediatorLiveDataLoading().isLoaded(viewModelSongs.isLoaded, viewModelSettings.isLoaded)
        loaded.observe(viewLifecycleOwner, { isLoaded ->
            if (isLoaded) {
                findNavController().popBackStack()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}