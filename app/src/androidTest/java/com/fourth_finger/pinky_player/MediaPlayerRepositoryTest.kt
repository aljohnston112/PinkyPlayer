package com.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.fourth_finger.music_repository.MusicRepository
import org.junit.Assert.*
import org.junit.Rule

import org.junit.Test
import java.util.concurrent.CountDownLatch

/**
 * Tests [MediaPlayerRepository].
 */
class MediaPlayerRepositoryTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Test
    fun play_validSong_playsToCompletion() {
        val musicRepository = MusicRepository.getInstance()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        musicRepository.loadMusicFiles(context.contentResolver)

        val countDownLatchOnPrepared = CountDownLatch(1)
        val countDownLatchOnCompletion = CountDownLatch(1)

        // LiveData update must be on main thread
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val music = musicRepository.musicFiles.getOrAwaitValue()
            MediaPlayerRepository.getInstance().play(
                context,
                music[0].id,
                onPrepared = {countDownLatchOnPrepared.countDown()},
                onCompletion = {countDownLatchOnCompletion.countDown()},
            )
        }

        countDownLatchOnPrepared.await()
        countDownLatchOnCompletion.await()
    }
}