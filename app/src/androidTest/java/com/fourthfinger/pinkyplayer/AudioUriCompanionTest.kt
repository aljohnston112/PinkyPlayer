package com.fourthfinger.pinkyplayer

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.songs.AudioUri
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val displayName = "a"
private const val artist = "b"
private const val title = "c"
private const val id = 4L
private val audioUri = AudioUri(displayName, artist, title, id)

@RunWith(AndroidJUnit4::class)
class AudioUriCompanionTest{

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun delete(){
        AudioUri.deleteAudioUri(context, id)
    }

    @Test
    fun testDoesAudioExistSaveAndDelete(){
        assert(!AudioUri.doesAudioUriExist(context, id))
        assert(AudioUri.saveAudioUri(context, audioUri))
        assert(AudioUri.doesAudioUriExist(context, id))
        assert(AudioUri.deleteAudioUri(context, id))
        assert(!AudioUri.doesAudioUriExist(context, id))
    }

    @Test
    fun testSaveAndGet(){
        assert(AudioUri.saveAudioUri(context, audioUri))
        val get = AudioUri.getAudioUri(context, id)!!
        assert(get.displayName == displayName)
        assert(get.artist == artist)
        assert(get.title == title)
        assert(get.id == id)
        assert(AudioUri.deleteAudioUri(context, id))
    }

}