package io.fourth_finger.music_repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

/**
 * Tests the [MusicRepository].
 */
class MusicRepositoryTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    /**
     * Tests that the [MusicFile]s returned by [MusicRepository.loadMusicFiles] match those
     * loaded by the [MusicDataSource].
     * This test will fail if there are no music files on the test device.
     */
    @Test
    fun loadMusicFiles_ContentResolver_ReturnsCorrectSongs() = runTest {
        val contentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
        val musicFiles = MusicRepository().loadMusicFiles(contentResolver)!!
        val actualMusicFiles = MusicDataSource().getMusicFromMediaStore(contentResolver)!!

        // Assert there are music files
        assert(musicFiles.isNotEmpty())

        // Assert the music repository loads all of them
        assert(musicFiles.size == actualMusicFiles.size)
        for (musicFile in actualMusicFiles) {
            assert(musicFile in musicFiles)
        }

    }

    /**
     * Tests that the [MusicRepository] caches the results of [MusicRepository.loadMusicFiles] and
     * they can be retrieved via [MusicRepository.getCachedMusicFiles].
     */
    @Test
    fun loadMusicFiles_ContentResolver_CachesCorrectSongs() = runTest {
        val contentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
        val actualMusicFiles = MusicDataSource().getMusicFromMediaStore(contentResolver)!!

        val musicRepository = MusicRepository()
        assert(musicRepository.getCachedMusicFiles() == null)
        musicRepository.loadMusicFiles(contentResolver)!!
        val musicFiles = musicRepository.getCachedMusicFiles()!!

        // Assert there are music files
        assert(musicFiles.isNotEmpty())

        // Assert the music repository cached all of them
        assert(musicFiles.size == actualMusicFiles.size)
        for (musicFile in actualMusicFiles) {
            assert(musicFile in musicFiles)
        }
    }

}
