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
     * Test the [MusicFile]s that loadMusicFiles loads into the musicFiles [LiveData]
     * of the [MusicRepository]
     * matches the [MusicFile]s returned by getMusicFromMediaStore
     * of the [MusicDataSource].
     */
    @Test
    fun loadMusicFiles_ContentResolver_LiveDataReturnsCorrectSongs() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val musicFiles = MusicDataSource.getMusicFromMediaStore(context.contentResolver)

        MusicRepository.getInstance().loadMusicFiles(context.contentResolver, ioDispatcher)
        val music = MusicRepository.getInstance().musicFiles.getOrAwaitValue()
        assert(music.size == musicFiles.size)
        for((i, song) in music.withIndex()) {
            assert(song.displayName == musicFiles[i].displayName)
        }

    }


}
