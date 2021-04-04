package com.fourthfinger.pinkyplayer.songs

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AudioUriCompanionTest{

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test fun testHashCodeEqualsCompareTo(){
        val displayName = "a"
        val artist = "b"
        val title = "c"
        val id = 4L
        val audioUri = AudioUri(displayName, artist, title, id)
        val displayName2 = "e"
        val artist2 = "f"
        val title2 = "g"
        val id2 = 8L
        val audioUri2 = AudioUri(displayName2, artist2, title2, id2)
        assert(audioUri != audioUri2)
        assert(audioUri.hashCode() != audioUri2.hashCode())
        val audioUri3 = AudioUri(displayName, artist, title, id)
        assert(audioUri == audioUri3)
        assert(audioUri.hashCode() == audioUri3.hashCode())
        assert(audioUri<audioUri2)
    }

    @Test
    fun testDoesAudioExistSaveAndDelete(){
        val displayName = "a"
        val artist = "b"
        val title = "c"
        val id = 4L
        AudioUri.deleteAudioUri(context, id)
        val audioUri = AudioUri(displayName, artist, title, id)
        assert(!AudioUri.audioUriExists(context, id))
        assert(AudioUri.saveAudioUri(context, audioUri))
        assert(AudioUri.audioUriExists(context, id))
        assert(AudioUri.deleteAudioUri(context, id))
        assert(!AudioUri.audioUriExists(context, id))
    }

    @Test
    fun testSaveAndGet(){
        val displayName = "a"
        val artist = "b"
        val title = "c"
        val id = 4L
        AudioUri.deleteAudioUri(context, id)
        val audioUri = AudioUri(displayName, artist, title, id)
        assert(AudioUri.saveAudioUri(context, audioUri))
        val get = AudioUri.getAudioUri(context, id)!!
        assert(get.displayName == displayName)
        assert(get.artist == artist)
        assert(get.title == title)
        assert(get.id == id)
        assert(AudioUri.deleteAudioUri(context, id))
    }

}