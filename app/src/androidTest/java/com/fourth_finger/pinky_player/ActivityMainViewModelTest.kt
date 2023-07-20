package com.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.fourth_finger.music_repository.MusicRepository
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

@HiltAndroidTest
class ActivityMainViewModelTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule: ActivityScenarioRule<HiltTestActivity> = ActivityScenarioRule(
        HiltTestActivity::class.java
    )

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
    @Test
    fun permissionGranted_MusicLoaded() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val music = musicRepository.loadMusicFiles(context.contentResolver)!!
        assert(music.isNotEmpty())
        val countDownLatch = CountDownLatch(1)
        activityRule.scenario.onActivity {
            it.viewModel.permissionGranted(context.contentResolver)
            countDownLatch.countDown()
        }
        countDownLatch.await()
    }

}