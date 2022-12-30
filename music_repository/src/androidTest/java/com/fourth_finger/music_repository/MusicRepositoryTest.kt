package com.fourth_finger.music_repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test

/**
 * Tests the [MusicRepository].
 */
class MusicRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    /**
     * Test the [MusicFile]s that getMusicFromMediaStore returns
     * represent audio files that are in the MediaStore's audio store and
     * are considered music.
     *
     * It is assumed there is one music file on the device,
     * and it display name is "01 .mp3"
     */
    @Test
    fun loadMusicFiles_ContentResolver_LiveDataReturnsCorrectSongs() {

        // Test the returned MusicFile objects represent the correct MediaStore rows
        val context = InstrumentationRegistry.getInstrumentation().context
        MusicRepository.getInstance().loadMusicFiles(context.contentResolver)
        MusicRepository.getInstance().musicFiles.observeForever {
            assert(it.size == 1)
            assert(it[0].displayName == "01 .mp3")
        }

    }


}
