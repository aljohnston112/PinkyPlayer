package com.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.fourth_finger.music_repository.MusicRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject


@HiltAndroidTest
class FragmentMusicListViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Inject
    lateinit var musicRepository: MusicRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    /**
     * Tests that the UIState of [FragmentMusicListViewModel]
     * contains all the music loaded from the device.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getUiState_AfterFetchMusicFiles_ReturnsAllLoadedMusic() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val musicFiles = musicRepository.loadMusicFiles(context.contentResolver)
        val viewModel = FragmentMusicListViewModel(musicRepository)
        viewModel.fetchMusicFiles(context.contentResolver)
        val music = viewModel.musicFiles.getOrAwaitValue()
        assert(music.size == musicFiles.size)
        // Do these really need to be in the same order?
        for ((i, song) in music.withIndex()) {
            assert(song.displayName == musicFiles[i].displayName)
        }
    }

}