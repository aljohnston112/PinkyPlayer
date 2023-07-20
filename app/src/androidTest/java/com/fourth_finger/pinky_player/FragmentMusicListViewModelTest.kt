package com.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
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
import java.util.concurrent.CountDownLatch
import javax.inject.Inject


@HiltAndroidTest
class FragmentMusicListViewModelTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule: ActivityScenarioRule<HiltTestActivity> = ActivityScenarioRule(
        HiltTestActivity::class.java
    )

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
     * Tests that the UIState of [ActivityMainViewModel]
     * contains all the music loaded from the device.
     */
    @Test
    fun getUiState_AfterFetchMusicFiles_ReturnsAllLoadedMusic() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val musicFiles = musicRepository.loadMusicFiles(context.contentResolver)!!
        val countDownLatch = CountDownLatch(1)

        activityRule.scenario.onActivity {
            val viewModel = it.viewModel
            val job = viewModel.fetchMusicFiles(context.contentResolver)
            job.invokeOnCompletion {
                val music = viewModel.musicFiles.getOrAwaitValue()
                assert(music.size == musicFiles.size)
                for (song in music) {
                    assert(song in musicFiles)
                }
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
    }

}