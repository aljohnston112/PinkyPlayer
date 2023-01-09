package com.fourth_finger.pinky_player

import android.media.session.MediaController
import androidx.lifecycle.ViewModel

class FragmentControlsViewModel : ViewModel() {

    /**
     * Pauses or plays the current song.
     */
    fun playPause(isPlaying: Boolean, controls: MediaController.TransportControls) {
        if(isPlaying){
            controls.pause()
        } else {
            controls.play()
        }
    }

}
