package com.fourth_finger.music_repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tests the [MusicRepository].
 */
@HiltAndroidTest
class MusicRepositoryTest {

    @get:Rule()
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Inject
    lateinit var musicRepository: MusicRepository


    @BindValue @JvmField
    val threadSafeMemoryCache = ThreadSafeMemoryCache<List<MusicFile>>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    /**
     * Tests that the [MusicFile]s returned by [MusicRepository.loadMusicFiles] match those
     * loaded by the [MusicDataSource].
     * This test will fail if there are no music files on the test device.
     */
    @Test
    fun loadMusicFiles_ContentResolver_ReturnsCorrectSongs() = runTest {
        val contentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
        val actualMusicFiles = MusicDataSource().getMusicFromMediaStore(contentResolver)!!
        val musicFiles = musicRepository.loadMusicFiles(contentResolver)!!

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

        assert(!threadSafeMemoryCache.hasData())
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
