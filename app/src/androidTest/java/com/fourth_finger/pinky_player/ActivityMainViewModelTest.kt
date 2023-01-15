package com.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.fourth_finger.music_repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlinx.coroutines.yield
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltAndroidTest
class ActivityMainViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Inject
    lateinit var musicRepository: MusicRepository

    /**
     * Tests that [ActivityMainViewModel] triggers the update
     * to the musicFiles of the [MusicRepository] when it is
     * notified that permission has been granted.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun permissionGranted_MusicLoaded() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val viewModel = ActivityMainViewModel(musicRepository)
        viewModel.permissionGranted(context.contentResolver)

        val music = musicRepository.loadMusicFiles(context.contentResolver)
        assert(music.isNotEmpty())
    }

}