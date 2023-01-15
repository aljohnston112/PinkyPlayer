package com.fourth_finger.music_repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

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

    @Before
    fun init() {
        hiltRule.inject()
    }

    /**
     * Test the [MusicFile]s that loadMusicFiles loads into the musicFiles [LiveData]
     * of the [MusicRepository]
     * matches the [MusicFile]s returned by getMusicFromMediaStore
     * of the [MusicDataSource].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loadMusicFiles_ContentResolver_LiveDataReturnsCorrectSongs() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().context
        val musicFiles = MusicDataSource().getMusicFromMediaStore(context.contentResolver)

        val music = musicRepository.loadMusicFiles(context.contentResolver)

        assert(music.isNotEmpty())
        assert(music.size == musicFiles.size)
        for ((i, song) in music.withIndex()) {
            assert(song.id == musicFiles[i].id)
            assert(song.displayName == musicFiles[i].displayName)
        }
    }

}
