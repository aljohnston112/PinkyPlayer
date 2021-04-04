package com.fourthfinger.pinkyplayer

import com.fourthfinger.pinkyplayer.songs.AudioUri
import org.junit.Test


class AudioUriTest {

    @Test
    fun testAudioUri(){
        val displayName = "a"
        val artist = "b"
        val title = "c"
        val id = 4L
        val audioUri = AudioUri(displayName, artist, title, id)
        assert(audioUri.displayName == displayName)
        assert(audioUri.artist == artist)
        assert(audioUri.title == title)
        assert(audioUri.id == id)
    }

}