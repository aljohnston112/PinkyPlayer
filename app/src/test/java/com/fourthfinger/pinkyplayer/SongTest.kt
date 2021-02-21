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

    @Test fun compareTo(){
        val song = Song(1L, "1",)
        val song1 = Song(1L, "2",)
        assert(song < song1)
    }

    @Test fun equalsHashCode(){
        val song = Song(1L, "1",)
        val song1 = Song(1L, "2",)
        assert(song == song1)
        assert(song.hashCode() == song1.hashCode())
        val song2 = Song(1L, "1",)
        val song3 = Song(2L, "2",)
        assert(song2 != song3)
        assert(song2.hashCode() != song3.hashCode())
    }

}