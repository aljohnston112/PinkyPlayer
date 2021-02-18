package com.fourthfinger.pinkyplayer.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.databinding.FragmentTitleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentTitle : Fragment() {

    private var _binding: FragmentTitleBinding? = null

    private val viewModelSongs: SongsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // container: ViewGroup? means container may be null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentTitleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelSongs.isLoaded.observe(viewLifecycleOwner,  {loaded ->
            if(loaded != true){
                findNavController().navigate(R.id.fragmentLoading)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        binding.buttonSettings.setOnClickListener {
            safeNav(FragmentTitleDirections.actionFragmentTitleToFragmentSettings())
        }
        binding.buttonSongs.setOnClickListener {
            safeNav(FragmentTitleDirections.actionFragmentTitleToFragmentSongs())
        }
    }

    private fun safeNav(action: NavDirections) {
        val dest = findNavController().currentDestination
        if(dest != null){
            if(dest.id == R.id.fragmentTitle) {
                findNavController().navigate(action)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // For VMs
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}