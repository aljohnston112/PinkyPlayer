package com.fourthfinger.pinkyplayer.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.ViewModel
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.databinding.RecyclerViewSongListBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FragmentSongs : Fragment() {

    private var _binding: RecyclerViewSongListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

   //  private val viewModel: SongsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    val registrationViewModel: SongsViewModel by viewModels<SongsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = RecyclerViewSongListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}