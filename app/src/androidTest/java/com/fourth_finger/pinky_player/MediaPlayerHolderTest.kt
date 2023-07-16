package com.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.fourth_finger.music_repository.MusicRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

/**
 * Tests [MediaPlayerHolder].
 */
@HiltAndroidTest
class MediaPlayerHolderTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Inject
    lateinit var musicRepository: MusicRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun play_validSong_playsToCompletion() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val countDownLatchOnCompletion = CountDownLatch(1)

        val music = musicRepository.loadMusicFiles(context.contentResolver)
        val mediaPlayerHolder = MediaPlayerHolder(musicRepository)
        mediaPlayerHolder.start(
            context,
            music[0].id,
            onPrepared = { },
        ) { countDownLatchOnCompletion.countDown() }
        countDownLatchOnCompletion.await()
    }

}