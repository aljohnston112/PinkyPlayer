package com.fourthfinger.pinkyplayer.playlists

import com.fourthfinger.pinkyplayer.LiveDataTestUtil
import org.junit.Test

class MediaControllerTest {

    @Test
    fun liveData() {
        val mediaController = MediaController()

        LiveDataTestUtil.checkLiveDataUpdate(mediaController.isPlaying, false)
        mediaController.toggleIsPlaying()
        LiveDataTestUtil.checkLiveDataUpdate(mediaController.isPlaying, true)
        mediaController.toggleIsPlaying()
        LiveDataTestUtil.checkLiveDataUpdate(mediaController.isPlaying, false)

        LiveDataTestUtil.checkLiveDataUpdate(mediaController.looping, MediaController.Loop.NONE)
        mediaController.toggleLooping()
        LiveDataTestUtil.checkLiveDataUpdate(mediaController.looping, MediaController.Loop.ALL)
        mediaController.toggleLooping()
        LiveDataTestUtil.checkLiveDataUpdate(mediaController.looping, MediaController.Loop.ONE)
        mediaController.toggleLooping()
        LiveDataTestUtil.checkLiveDataUpdate(mediaController.looping, MediaController.Loop.NONE)

        LiveDataTestUtil.checkLiveDataUpdate(mediaController.shuffling, true)
        mediaController.toggleShuffling()
        LiveDataTestUtil.checkLiveDataUpdate(mediaController.shuffling, false)
        mediaController.toggleShuffling()
        LiveDataTestUtil.checkLiveDataUpdate(mediaController.shuffling, true)


    }

}