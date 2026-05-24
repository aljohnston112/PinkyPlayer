package io.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.media3.common.Player
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.fourth_finger.event_processor.EventProcessor
import io.fourth_finger.event_processor.MediaBrowserProvider
import io.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

@HiltAndroidTest
class EventProcessorTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val rule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    var activityScenarioRule = activityScenarioRule<ActivityMain>()

    @get:Rule(order = 3)
    val mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            Manifest.permission.READ_MEDIA_AUDIO
        )

    @Inject
    lateinit var coroutineScope: CoroutineScope

    @Inject
    lateinit var eventProcessor: EventProcessor

    @Inject
    lateinit var mediaBrowserProvider: MediaBrowserProvider

    @Inject
    lateinit var musicRepository: MusicRepository

    private val context =
        ApplicationProvider.getApplicationContext<HiltTestApplication>()

    @Before
    fun init() = runTest {
        hiltRule.inject()
    }

    @Test
    fun onPlayPauseClicked_whenMediaControllerIsPlaying_pausesMediaPlayer() =
        runTest {
            val countDownLatchPlay = CountDownLatch(1)
            val countDownLatchPause = CountDownLatch(1)
            val mediaBrowser = mediaBrowserProvider.await()
            mediaBrowser.addListener(
                object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        if (isPlaying) {
                            countDownLatchPlay.countDown()
                        } else {
                            if (countDownLatchPlay.count == 0L) {
                                countDownLatchPause.countDown()
                            }
                        }
                    }
                }
            )

            // Play a song and wait for it to play

            val music = musicRepository.loadMusicFiles(context.contentResolver)
            UiThreadStatement.runOnUiThread {
                eventProcessor.songClicked(context, music[0].id)
            }
            countDownLatchPlay.await()

            // Pause the song and wait for it to pause
            UiThreadStatement.runOnUiThread {
                eventProcessor.onPlayPauseClicked(context)
            }
            countDownLatchPause.await()
        }

    @Test
    fun onPlayPauseClicked_whenMediaControllerIsPaused_playsMediaPlayer() =
        runTest {
            val countDownLatchPlay = CountDownLatch(1)
            val countDownLatchPause = CountDownLatch(1)
            val countDownLatchPlay2 = CountDownLatch(1)
            val mediaBrowser = mediaBrowserProvider.await()
            mediaBrowser.addListener(
                object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        if (isPlaying) {
                            if (countDownLatchPlay.count == 0L) {
                                countDownLatchPlay2.countDown()
                            } else {
                                countDownLatchPlay.countDown()
                            }
                        } else {
                            if (countDownLatchPlay.count == 0L) {
                                countDownLatchPause.countDown()
                            }
                        }
                    }
                }
            )

            // Play a song and wait for it to play
            val music = musicRepository.loadMusicFiles(context.contentResolver)
            UiThreadStatement.runOnUiThread {
                eventProcessor.songClicked(context, music[0].id)
            }
            countDownLatchPlay.await()

            // Pause the song and wait for it to pause
            UiThreadStatement.runOnUiThread {
                eventProcessor.onPlayPauseClicked(context)
            }
            countDownLatchPause.await()

            // Resume the song and wait for it to resume
            UiThreadStatement.runOnUiThread {
                eventProcessor.onPlayPauseClicked(context)
            }
            countDownLatchPlay2.await()
        }

    @Test
    fun songClicked_triggersPlayOfClickedSong() = runTest {
        val countDownLatch = CountDownLatch(1)
        val mediaBrowser = mediaBrowserProvider.await()
        val music = musicRepository.loadMusicFiles(context.contentResolver)
        val musicId = music[0].id
        mediaBrowser.addListener(
            object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)
                    if (events.contains(Player.EVENT_IS_PLAYING_CHANGED) && player.isPlaying) {
                        Assert.assertTrue(player.currentMediaItem?.mediaId == musicId.toString())
                        countDownLatch.countDown()
                    }
                }
            }
        )

        // Start song and wait for it to play
        UiThreadStatement.runOnUiThread {
            eventProcessor.songClicked(context, musicId)
        }
        countDownLatch.await()
    }

}