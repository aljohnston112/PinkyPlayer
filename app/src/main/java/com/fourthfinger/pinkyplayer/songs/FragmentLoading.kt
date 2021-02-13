package com.fourthfinger.pinkyplayer.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.databinding.FragmentLoadingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentLoading : Fragment() {

    private var _binding: FragmentLoadingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val registrationViewModel: SongsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentLoadingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}