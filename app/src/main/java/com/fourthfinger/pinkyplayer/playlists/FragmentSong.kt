package com.fourthfinger.pinkyplayer.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import com.fourthfinger.pinkyplayer.BitmapUtil
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.StringUtil
import com.fourthfinger.pinkyplayer.databinding.FragmentSongBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentSong() : Fragment() {

    private var _binding: FragmentSongBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mediaViewModel: MediaViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentSongBinding.inflate(layoutInflater)
        setUpLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeMedia()
        observeControls()
        observeDetails()
        setUpClickListeners()
    }

    private fun setUpClickListeners() {
        binding.apply {
            imageButtonRepeat.setOnClickListener {
                mediaViewModel.toggleLooping()
            }
            imageButtonPlayPause.setOnClickListener {
                mediaViewModel.toggleIsPlaying()
            }
            imageButtonShuffle.setOnClickListener {
                mediaViewModel.toggleShuffling()
            }
        }
    }

    private fun setUpLayout() {
        binding.imageViewSongArtFragmentSong.doOnPreDraw {
            val lp = LinearLayout.LayoutParams(it.measuredWidth, it.measuredWidth)
            val m = resources.getDimension(R.dimen.margin).toInt()
            lp.setMargins(m, m, m, m)
            it.layoutParams = lp
        }
        binding.buttonThumbDown.doOnPreDraw {
            val lp = LinearLayout.LayoutParams(it.measuredWidth, it.measuredWidth)
            val m = resources.getDimension(R.dimen.half_margin).toInt()
            lp.setMargins(0, 0, m, 0)
            it.layoutParams = lp
        }
        binding.buttonThumbUp.doOnPreDraw {
            val lp = LinearLayout.LayoutParams(it.measuredWidth, it.measuredWidth)
            val m = resources.getDimension(R.dimen.half_margin).toInt()
            lp.setMargins(m, 0, 0, 0)
            it.layoutParams = lp
        }
        binding.imageButtonShuffle.doOnPreDraw {
            val lp = LinearLayout.LayoutParams(it.measuredWidth, it.measuredWidth)
            val m = resources.getDimension(R.dimen.half_margin).toInt()
            lp.setMargins(0, 0, m, 0)
            it.layoutParams = lp
        }
        binding.imageButtonPrev.doOnPreDraw {
            val lp = LinearLayout.LayoutParams(it.measuredWidth, it.measuredWidth)
            val m = resources.getDimension(R.dimen.half_margin).toInt()
            lp.setMargins(m, 0, m, 0)
            it.layoutParams = lp
        }
        binding.imageButtonNext.doOnPreDraw {
            val lp = LinearLayout.LayoutParams(it.measuredWidth, it.measuredWidth)
            val m = resources.getDimension(R.dimen.half_margin).toInt()
            lp.setMargins(m, 0, m, 0)
            it.layoutParams = lp
        }
        binding.imageButtonPlayPause.doOnPreDraw {
            val lp = LinearLayout.LayoutParams(it.measuredWidth, it.measuredWidth)
            val m = resources.getDimension(R.dimen.half_margin).toInt()
            lp.setMargins(m, 0, m, 0)
            it.layoutParams = lp
        }
        binding.imageButtonRepeat.doOnPreDraw {
            val lp = LinearLayout.LayoutParams(it.measuredWidth, it.measuredWidth)
            val m = resources.getDimension(R.dimen.half_margin).toInt()
            lp.setMargins(m, 0, 0, 0)
            it.layoutParams = lp
        }
    }

    private fun observeDetails() {
        val mediatorLiveDataBitmap = MediatorLiveDataBitmap()
        mediatorLiveDataBitmap.currentSongBitmap(
                mediaViewModel.currentSongBitmap
        ).observe(viewLifecycleOwner) {
            binding.imageViewSongArtFragmentSong.setImageBitmap(it)
        }
        mediaViewModel.currentSongTime.observe(viewLifecycleOwner) {
            binding.editTextCurrentTime.text = it
        }
        mediaViewModel.currentSongEndTime.observe(viewLifecycleOwner) {
            binding.editTextEndTime.text = it
        }
    }

    private fun observeControls() {
        mediaViewModel.looping.observe(viewLifecycleOwner) {
            val buttonLoop = binding.imageButtonRepeat
            when (it) {
                MediaController.Loop.ALL -> {
                    buttonLoop.setImageResource(R.drawable.repeat_black_24dp)
                }
                MediaController.Loop.ONE -> {
                    buttonLoop.setImageResource(R.drawable.repeat_one_black_24dp)
                }
                else -> {
                    buttonLoop.setImageResource(R.drawable.repeat_white_24dp)
                }
            }
        }
        mediaViewModel.shuffling.observe(viewLifecycleOwner) {
            val buttonShuffle = binding.imageButtonShuffle
            if (!it) {
                buttonShuffle.setImageResource(R.drawable.ic_shuffle_white_24dp)
            } else {
                buttonShuffle.setImageResource(R.drawable.ic_shuffle_black_24dp)
            }
        }
        mediaViewModel.isPlaying.observe(viewLifecycleOwner) {
            val buttonPause = binding.imageButtonPlayPause
            if (it) {
                buttonPause.setImageResource(R.drawable.pause_black_24dp)
            } else {
                buttonPause.setImageResource(R.drawable.play_arrow_black_24dp)
            }
        }


    }

    private fun observeMedia() {
        mediaViewModel.currentAudioUri.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.textViewSongName.text = it.title
                binding.editTextCurrentTime.post {
                    lifecycleScope.launch(Dispatchers.IO) {
                        mediaViewModel.setCurrentSongTime(StringUtil.formatMillis(0))
                    }
                }
                binding.editTextEndTime.post {
                    lifecycleScope.launch(Dispatchers.IO) {
                        mediaViewModel.setCurrentSongEndTime(
                                StringUtil.formatMillis(it.getDuration(requireContext()))
                        )
                    }
                }
                binding.imageViewSongArtFragmentSong.post {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val imageViewSongArt = binding.imageViewSongArtFragmentSong
                        var songArtDimen: Int = imageViewSongArt.measuredWidth
                        if (imageViewSongArt.measuredHeight > 2) {
                            songArtDimen = minOf(songArtDimen, imageViewSongArt.measuredHeight)
                        }
                        if (songArtDimen > 0) {
                            BitmapUtil.getSongBitmap(requireContext(), it, songArtDimen)?.let { it1 -> mediaViewModel.setCurrentSongBitmap(it1) }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}