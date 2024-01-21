package io.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_READY
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import kotlin.time.Duration

/**
 * Tests [PlayerHolder].
 */
class PlayerHolderTest {

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun prepareAndPlay_validSong_playsToCompletion() = runTest(timeout = Duration.parse("60s")) {
        val countDownLatchOnCompletion = CountDownLatch(1)
        val application = ApplicationProvider.getApplicationContext<ApplicationMain>()
        val musicRepository = application.musicRepository
        val music = musicRepository.loadMusicFiles(application.contentResolver)!!
        val playerHolder = PlayerHolder(application, musicRepository)

        playerHolder.getPlayer().addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if (playbackState == STATE_ENDED) {
                        countDownLatchOnCompletion.countDown()
                    }
                }
            }
        )

        UiThreadStatement.runOnUiThread {
            playerHolder.clearPrepareAndPlay(
                application,
                MediaPlayerUtil.getMusicIdOfShortestSong(music)
            )
        }

        countDownLatchOnCompletion.await()
    }

    @Test
    fun play_validSong_playsToCompletionAfterPause() = runTest(timeout = Duration.parse("60s")) {
        val countDownLatchOnCompletion = CountDownLatch(1)
        val countDownLatchOnPrepared = CountDownLatch(1)
        val application = ApplicationProvider.getApplicationContext<ApplicationMain>()
        val musicRepository = application.musicRepository
        val music = musicRepository.loadMusicFiles(application.contentResolver)!!
        val playerHolder = PlayerHolder(application, musicRepository)

        playerHolder.getPlayer().addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if (playbackState == STATE_ENDED && countDownLatchOnPrepared.count == 0L) {
                        countDownLatchOnCompletion.countDown()
                    } else if(playbackState == STATE_READY){
                        countDownLatchOnPrepared.countDown()
                    }
                }
            }
        )

        UiThreadStatement.runOnUiThread {
            playerHolder.clearPrepareAndPlay(
                application,
                MediaPlayerUtil.getMusicIdOfShortestSong(music)
            )
        }
        countDownLatchOnPrepared.await()
        UiThreadStatement.runOnUiThread {
            playerHolder.pause()
        }
        Assert.assertTrue(countDownLatchOnCompletion.count == 1L)
        UiThreadStatement.runOnUiThread {
            playerHolder.play()
        }
        countDownLatchOnCompletion.await()
    }

    @Test
    fun pause_validSong_pausesMediaPlayer() = runTest(timeout = Duration.parse("60s")) {
        val application = ApplicationProvider.getApplicationContext<ApplicationMain>()
        val musicRepository = application.musicRepository
        val music = musicRepository.loadMusicFiles(application.contentResolver)!!
        val playerHolder = PlayerHolder(application, musicRepository)
        val countDownLatchPlay = CountDownLatch(1)
        val countDownLatchPause = CountDownLatch(1)

        playerHolder.getPlayer().addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if(isPlaying){
                        countDownLatchPlay.countDown()
                    } else if(countDownLatchPlay.count == 0L){
                        countDownLatchPause.countDown()
                    }
                }
            }
        )

        UiThreadStatement.runOnUiThread {
            playerHolder.clearPrepareAndPlay(
                application,
                MediaPlayerUtil.getMusicIdOfShortestSong(music)
            )
        }
        countDownLatchPlay.await()
        UiThreadStatement.runOnUiThread {
            playerHolder.pause()
        }
        countDownLatchPause.await()

    }

}