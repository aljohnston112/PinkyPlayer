package io.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.MEDIA_ITEM_TRANSITION_REASON_AUTO
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.probability_map.ProbabilityMap
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import kotlin.time.Duration

/**
 * Tests [PlayerHolder].
 */
@HiltAndroidTest
class PlayerHolderTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var musicRepository: MusicRepository

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

    @Test
    fun prepareAndPlay_validSong_playsToCompletion() = runTest(timeout = Duration.parse("60s")) {
        val countDownLatchOnCompletion = CountDownLatch(1)
        val countDownLatchOnPrepared = CountDownLatch(1)
        val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()
        val music = musicRepository.loadMusicFiles(application.contentResolver)

        val mediaItemCreator = MediaItemCreator(musicRepository)

        val playerHolder = PlayerHolder(application, mediaItemCreator)
        playerHolder.setPlaylist(ProbabilityMap(listOf(music[0])))

        playerHolder.getPlayer().addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if(isPlaying){
                        Assert.assertTrue(countDownLatchOnCompletion.count == 1L)
                        countDownLatchOnPrepared.countDown()
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    if (reason == MEDIA_ITEM_TRANSITION_REASON_AUTO  && countDownLatchOnPrepared.count == 0L) {
                        countDownLatchOnCompletion.countDown()
                    }
                }
            }
        )

        val song = MediaFileUtil.getMusicIdOfShortestSong(musicRepository)
        UiThreadStatement.runOnUiThread {
            playerHolder.clearPrepareAndPlay(
                application,
                song
            )
        }

        countDownLatchOnCompletion.await()
    }

    @Test
    fun play_validSong_playsToCompletionAfterPause() = runTest(timeout = Duration.parse("60s")) {
        val countDownLatchOnCompletion = CountDownLatch(1)
        val countDownLatchOnPrepared = CountDownLatch(1)
        val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()
        val music = musicRepository.loadMusicFiles(application.contentResolver)
        val mediaItemCreator = MediaItemCreator(musicRepository)

        val playerHolder = PlayerHolder(application, mediaItemCreator)
        playerHolder.setPlaylist(ProbabilityMap(listOf(music[0])))

        playerHolder.getPlayer().addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if(isPlaying){
                        Assert.assertTrue(countDownLatchOnCompletion.count == 1L)
                        countDownLatchOnPrepared.countDown()
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    if (reason == MEDIA_ITEM_TRANSITION_REASON_AUTO  && countDownLatchOnPrepared.count == 0L) {
                        countDownLatchOnCompletion.countDown()
                    }
                }
            }
        )

        val song = MediaFileUtil.getMusicIdOfShortestSong(musicRepository)
        UiThreadStatement.runOnUiThread {
            playerHolder.clearPrepareAndPlay(
                application,
                song
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
        val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()
        val musicRepository = musicRepository
        val music = musicRepository.loadMusicFiles(application.contentResolver)
        val mediaItemCreator = MediaItemCreator(musicRepository)
        val playerHolder = PlayerHolder(application, mediaItemCreator)
        playerHolder.setPlaylist(ProbabilityMap(listOf(music[0])))

        val countDownLatchPlay = CountDownLatch(1)
        val countDownLatchPause = CountDownLatch(1)

        playerHolder.getPlayer().addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if(isPlaying){
                        Assert.assertTrue(countDownLatchPause.count == 1L)
                        countDownLatchPlay.countDown()
                    } else if(countDownLatchPlay.count == 0L){
                        countDownLatchPause.countDown()
                    }
                }
            }
        )

        val song = MediaFileUtil.getMusicIdOfShortestSong(musicRepository)
        UiThreadStatement.runOnUiThread {
            playerHolder.clearPrepareAndPlay(
                application,
                song
            )
        }
        countDownLatchPlay.await()
        UiThreadStatement.runOnUiThread {
            playerHolder.pause()
        }
        countDownLatchPause.await()

    }

}