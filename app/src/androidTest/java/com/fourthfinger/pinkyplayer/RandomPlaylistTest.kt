package com.fourthfinger.pinkyplayer

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.playlists.RandomPlaylist
import com.fourthfinger.pinkyplayer.songs.AudioUri
import com.fourthfinger.pinkyplayer.songs.Song
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class RandomPlaylistTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val song = Song(1L, "b")
    private val song1 = Song(0L, "a")
    private val song2 = Song(2L, "c")
    private val name = "a"
    private val maxPercent = 0.5
    private val audioUri = AudioUri("", "", song.title, song.id)
    private val audioUri1 = AudioUri("", "", song1.title, song1.id)
    private val audioUri2 = AudioUri("", "", song2.title, song2.id)

    @Before
    fun deleteAudioUris() {
        AudioUri.deleteAudioUri(context, song.id)
        AudioUri.deleteAudioUri(context, song1.id)
        AudioUri.deleteAudioUri(context, song2.id)
    }


    @Test
    fun testConstructors(){
        val music = listOf(song, song1, song2)
        val music1 = listOf(song1, song, song2)
        val comparable = true
        val comparable1 = false
        val rp = RandomPlaylist(name, music, maxPercent, comparable)
        val rp1 = RandomPlaylist(name, music1, maxPercent, comparable1)
        testConstructor(rp, music)
        testConstructor(rp1, music1)
        val rp2 = RandomPlaylist(name, music, maxPercent, comparable)
        val rp3 = RandomPlaylist(name, music1, maxPercent, comparable1)
        testNext(rp2)
        testNext(rp3)
        val rp4 = RandomPlaylist(name, music, maxPercent, comparable)
        val rp5 = RandomPlaylist(name, music1, maxPercent, comparable1)
        testPrevious(rp4)
        testPrevious(rp5)
    }

    private fun testConstructor(rp: RandomPlaylist, music: List<Song>) {
        assert(rp.name == name)
        val songs = rp.songs()
        for (s in songs) {
            assert(music.contains(s))
        }
        for (s in music) {
            assert(songs.contains(s))
        }
        assert(rp.maxPercent() == maxPercent)
        assert(songs[0] == song1)
        assert(songs[1] == song)
        assert(songs[2] == song2)
        assert(rp.size() == music.size)
        val songIDs = rp.songIds()
        assert(songIDs[0] == song1.id)
        assert(songIDs[1] == song.id)
        assert(songIDs[2] == song2.id)
        for (m in music) {
            assert(rp.contains(m))
        }
    }

    private fun testNext(rp : RandomPlaylist) {
        assert(!AudioUri.doesAudioUriExist(context, song.id))
        assert(AudioUri.saveAudioUri(context, audioUri))
        assert(AudioUri.doesAudioUriExist(context, song.id))
        assert(!AudioUri.doesAudioUriExist(context, song1.id))
        assert(AudioUri.saveAudioUri(context, audioUri1))
        assert(AudioUri.doesAudioUriExist(context, song1.id))
        assert(!AudioUri.doesAudioUriExist(context, song2.id))
        assert(AudioUri.saveAudioUri(context, audioUri2))
        assert(AudioUri.doesAudioUriExist(context, song2.id))
        val random = Random.Default
        var looping = true
        var shuffling = false
        val q = mutableListOf<AudioUri?>()
        val q2 = mutableListOf<AudioUri>()
        val q3 = mutableListOf<AudioUri>()
        for (i in 0..8) {
            val a = rp.next(context, random, looping, shuffling)
            q.add(a)
            q2.add(a!!)
        }
        for (i in 0..2) {
            assert(q[0 + (3 * i)]!!.id == song1.id)
            assert(q[1 + (3 * i)]!!.id == song.id)
            assert(q[2 + (3 * i)]!!.id == song2.id)
        }
        q.clear()
        rp.goToFront()
        looping = false
        for (i in 0..8) {
            val a = rp.next(context, random, looping, shuffling)
            q.add(a)
            if (i < 3) {
                q2.add(a!!)
            }
        }
        assert(q[0]!!.id == song1.id)
        assert(q[1]!!.id == song.id)
        assert(q[2]!!.id == song2.id)
        for (i in 1..2) {
            assert(q[0 + (3 * i)] == null)
            assert(q[1 + (3 * i)] == null)
            assert(q[2 + (3 * i)] == null)
        }
        q.clear()
        shuffling = true
        for (i in 0..8) {
            val a = rp.next(context, random, looping, shuffling)
            q.add(a)
            q2.add(a!!)
            q3.add(a)
        }
        var same = true
        for (i in 0..8) {
            if (q2[i].id != q3[i].id) {
                same = false
                break
            }
        }
        assert(!same)
        rp.setIndexTo(1)
        shuffling = false
        assert(rp.next(context, random, looping, shuffling)!!.id == song.id)
        assert(AudioUri.deleteAudioUri(context, song2.id))
        assert(!AudioUri.doesAudioUriExist(context, song2.id))
        assert(AudioUri.deleteAudioUri(context, song1.id))
        assert(!AudioUri.doesAudioUriExist(context, song1.id))
        assert(AudioUri.deleteAudioUri(context, song.id))
        assert(!AudioUri.doesAudioUriExist(context, song.id))
    }

    fun testPrevious(rp : RandomPlaylist){
        assert(!AudioUri.doesAudioUriExist(context, song.id))
        assert(AudioUri.saveAudioUri(context, audioUri))
        assert(AudioUri.doesAudioUriExist(context, song.id))
        assert(!AudioUri.doesAudioUriExist(context, song1.id))
        assert(AudioUri.saveAudioUri(context, audioUri1))
        assert(AudioUri.doesAudioUriExist(context, song1.id))
        assert(!AudioUri.doesAudioUriExist(context, song2.id))
        assert(AudioUri.saveAudioUri(context, audioUri2))
        assert(AudioUri.doesAudioUriExist(context, song2.id))
        val random = Random.Default
        var looping = true
        var shuffling = false
        val q = mutableListOf<AudioUri?>()
        val q2 = mutableListOf<AudioUri>()
        val q3 = mutableListOf<AudioUri>()
        for (i in 0..8) {
            val a = rp.previous(context, random, looping, shuffling)
            q.add(a)
            q2.add(a!!)
        }
        for (i in 0..2) {
            assert(q[0 + (3 * i)]!!.id == song2.id)
            assert(q[1 + (3 * i)]!!.id == song.id)
            assert(q[2 + (3 * i)]!!.id == song1.id)
        }
        q.clear()
        rp.goToBack()
        looping = false
        for (i in 0..8) {
            val a = rp.previous(context, random, looping, shuffling)
            q.add(a)
            if (i < 3) {
                q2.add(a!!)
            }
        }
        assert(q[0]!!.id == song2.id)
        assert(q[1]!!.id == song.id)
        assert(q[2]!!.id == song1.id)
        for (i in 1..2) {
            assert(q[0 + (3 * i)] == null)
            assert(q[1 + (3 * i)] == null)
            assert(q[2 + (3 * i)] == null)
        }
        q.clear()
        shuffling = true
        for (i in 0..8) {
            val a = rp.previous(context, random, looping, shuffling)
            q.add(a)
            q2.add(a!!)
            q3.add(a)
        }
        var same = true
        for (i in 0..8) {
            if (q2[i].id != q3[i].id) {
                same = false
                break
            }
        }
        assert(!same)
        rp.setIndexTo(1)
        shuffling = false
        assert(rp.previous(context, random, looping, shuffling)!!.id == song1.id)
        assert(AudioUri.deleteAudioUri(context, song2.id))
        assert(!AudioUri.doesAudioUriExist(context, song2.id))
        assert(AudioUri.deleteAudioUri(context, song1.id))
        assert(!AudioUri.doesAudioUriExist(context, song1.id))
        assert(AudioUri.deleteAudioUri(context, song.id))
        assert(!AudioUri.doesAudioUriExist(context, song.id))
    }


}