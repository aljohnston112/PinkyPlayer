package com.fourthfinger.pinkyplayer

import com.fourthfinger.pinkyplayer.playlists.RandomPlaylist
import com.fourthfinger.pinkyplayer.songs.Song
import org.junit.Test

class RandomPlaylistRoundingErrorTest {

    private val song = Song(0L, "a")
    private val song1 = Song(1L, "b")
    private val song2 = Song(2L, "c")
    private val song3 = Song(3L, "d")
    private val song4 = Song(4L, "e")
    private val song5 = Song(5L, "f")
    private val name = "a"
    private val maxPercent = 0.5

    @Test fun debugForRoundingError(){
        val music = listOf(song, song1, song2, song3, song4, song5,)
        val comparable = true
        val rp = RandomPlaylist(name, music, maxPercent, comparable)
        while(true){
            rp.globalBad(song5, 0.5)
        }
    }

}