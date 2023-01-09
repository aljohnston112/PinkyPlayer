package com.fourth_finger.pinky_player

import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.PlaybackState
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels

/**
 * A [Fragment] that represents media controls.
 */
class FragmentControls : Fragment() {

    private val viewModel: FragmentControlsViewModel by viewModels()

    private lateinit var controllerCallback: MediaController.Callback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_controls,
            container,
            false
        )
    }

    /**
     * Initializes the controllerCallback and sets up the onClickListeners.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpOnClickListeners(view)
        val playPauseButton = view.findViewById<ImageButton>(R.id.button_play_pause)

        controllerCallback = object : MediaController.Callback() {
            override fun onMetadataChanged(metadata: MediaMetadata?) {}

            override fun onPlaybackStateChanged(state: PlaybackState?) {
                when (state?.state) {
                    PlaybackState.STATE_PLAYING -> {
                        playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24)
                    }

                    PlaybackState.STATE_PAUSED -> {
                        playPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                }

            }
        }
    }

    /**
     * Sets up the onClickListeners
     */
    private fun setUpOnClickListeners(view: View) {
        val playPauseButton = view.findViewById<ImageButton>(R.id.button_play_pause)
        playPauseButton.setOnClickListener {
            val mediaController = requireActivity().mediaController!!
            val isPlaying = mediaController.playbackState?.state == PlaybackStateCompat.STATE_PLAYING
            viewModel.playPause(isPlaying, mediaController.transportControls)
        }
    }

    /**
     * Called by the [Activity] when it has a MediaController set.
     */
    fun onMediaControllerSet() {
        requireActivity().mediaController.registerCallback(controllerCallback)
    }

    /**
     * Called by the [Activity] when it stops.
     */
    fun onActivityStop() {
        requireActivity().mediaController.unregisterCallback(controllerCallback)

    }

}