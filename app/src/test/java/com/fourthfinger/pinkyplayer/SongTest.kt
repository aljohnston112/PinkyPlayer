package com.fourthfinger.pinkyplayer

import com.fourthfinger.pinkyplayer.settings.Settings
import com.fourthfinger.pinkyplayer.songs.Song
import org.junit.Test

class SongTest {

    @Test
    fun validateConstructor() {
        val song = Song(1L, "1",)
        assert(song.id == 1L)
        assert(song.title == "1")
        assert(!song.selected)
    }

}