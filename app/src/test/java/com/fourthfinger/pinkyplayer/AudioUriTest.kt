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

    @Test
    fun testCompareTo() {
        val displayName = "a"
        val artist = "b"
        val title = "c"
        val id = 4L
        val audioUri = AudioUri(displayName, artist, title, id)
        val displayName1 = "e"
        val artist1 = "f"
        val title1 = "g"
        val id1 = 8L
        val audioUri1 = AudioUri(displayName1, artist1, title1, id1)
        assert(audioUri < audioUri1)
    }


}