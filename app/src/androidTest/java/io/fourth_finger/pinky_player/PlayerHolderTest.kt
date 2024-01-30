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
import io.fourth_finger.music_repository.MusicFile
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.probability_map.ProbabilityMap
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import kotlin.properties.Delegates
import kotlin.time.Duration

/**
 * Tests [PlayerHolder].
 */
@HiltAndroidTest
class PlayerHolderTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @get:Rule(order = 2)
    val rule = InstantTaskExecutorRule()

    @Inject
    lateinit var musicRepository: MusicRepository

    private val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()

    private lateinit var music: List<MusicFile>
    private lateinit var mediaItemCreator: MediaItemCreator
    private lateinit var playerHolder: PlayerHolder
    private var shortestSong by Delegates.notNull<Long>()


    @Before
    fun init() = runTest(timeout = Duration.parse("60s")) {
        hiltRule.inject()
        mediaItemCreator = MediaItemCreator(musicRepository)
        UiThreadStatement.runOnUiThread {
            playerHolder = PlayerHolder(application, mediaItemCreator)
        }
        music = musicRepository.loadMusicFiles(application.contentResolver)
        shortestSong = MediaFileUtil.getMusicIdOfShortDurationSong(musicRepository)
        playerHolder.setProbabilityMap(ProbabilityMap(listOf(music[0])))
    }

    @Test
    fun clearAndPlay_validSong_playsToCompletion() =
        runTest(timeout = Duration.parse("60s")) {

            // Set up the player holder
            val countDownLatchOnPrepared = CountDownLatch(1)
            val countDownLatchOnCompletion = CountDownLatch(1)
            playerHolder.getPlayer().addListener(
                object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        if (isPlaying) {
                            Assert.assertTrue(countDownLatchOnCompletion.count == 1L)
                            countDownLatchOnPrepared.countDown()
                        }
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        super.onMediaItemTransition(mediaItem, reason)
                        if (reason == MEDIA_ITEM_TRANSITION_REASON_AUTO && countDownLatchOnPrepared.count == 0L) {
                            countDownLatchOnCompletion.countDown()
                        }
                    }
                }
            )

            // Play the song and wait for it to complete
            UiThreadStatement.runOnUiThread {
                playerHolder.clearAndPlay(
                    application,
                    shortestSong
                )
            }
            countDownLatchOnCompletion.await()
        }

    @Test
    fun clearAndPlayCalledTwice_validSong_playsSecondSongToCompletion() =
        runTest(timeout = Duration.parse("60s")) {

            // Set up the player holder
            val countDownLatchOnPrepared = CountDownLatch(1)
            val countDownLatchOnCompletion = CountDownLatch(1)
            val countDownLatchOnPrepared2 = CountDownLatch(1)
            val countDownLatchOnCompletion2 = CountDownLatch(1)
            playerHolder.getPlayer().addListener(
                object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        if (isPlaying) {
                            if (countDownLatchOnPrepared.count == 1L) {
                                Assert.assertTrue(countDownLatchOnCompletion.count == 1L)
                                Assert.assertTrue(countDownLatchOnPrepared2.count == 1L)
                                Assert.assertTrue(countDownLatchOnCompletion2.count == 1L)
                                countDownLatchOnPrepared.countDown()
                            } else if (countDownLatchOnPrepared2.count == 1L) {
                                Assert.assertTrue(countDownLatchOnCompletion2.count == 1L)
                                countDownLatchOnPrepared2.countDown()
                            }
                        }
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        super.onMediaItemTransition(mediaItem, reason)
                        if (reason == MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                            if (countDownLatchOnPrepared.count == 0L && countDownLatchOnPrepared2.count == 1L) {
                                Assert.assertTrue(countDownLatchOnPrepared2.count == 1L)
                                Assert.assertTrue(countDownLatchOnCompletion2.count == 1L)
                                countDownLatchOnCompletion.countDown()
                            } else if (countDownLatchOnPrepared2.count == 0L) {
                                countDownLatchOnCompletion2.countDown()
                            }
                        }
                    }
                }
            )

            // Play a song and wait for it to start
            val song2 = MediaFileUtil.getMusicIdOfShortDurationSong(
                musicRepository,
                listOf(shortestSong)
            )
            UiThreadStatement.runOnUiThread {
                playerHolder.clearAndPlay(
                    application,
                    shortestSong
                )
            }
            countDownLatchOnCompletion.await()

            // Start a second song and wait for it to complete
            UiThreadStatement.runOnUiThread {
                playerHolder.clearAndPlay(
                    application,
                    song2
                )
            }
            countDownLatchOnCompletion2.await()
        }

    @Test
    fun play_validSong_playsToCompletionAfterPause() = runTest(timeout = Duration.parse("60s")) {

        // Set up the player holder
        val countDownLatchOnCompletion = CountDownLatch(1)
        val countDownLatchOnPrepared = CountDownLatch(1)
        playerHolder.getPlayer().addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        Assert.assertTrue(countDownLatchOnCompletion.count == 1L)
                        countDownLatchOnPrepared.countDown()
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    if (reason == MEDIA_ITEM_TRANSITION_REASON_AUTO && countDownLatchOnPrepared.count == 0L) {
                        countDownLatchOnCompletion.countDown()
                    }
                }
            }
        )

        // Play the song and wait for it to start
        UiThreadStatement.runOnUiThread {
            playerHolder.clearAndPlay(
                application,
                shortestSong
            )
        }
        countDownLatchOnPrepared.await()

        // Pause the song
        UiThreadStatement.runOnUiThread {
            playerHolder.pause()
        }
        Assert.assertTrue(countDownLatchOnCompletion.count == 1L)

        // Resume the song and wait for it to complete
        UiThreadStatement.runOnUiThread {
            playerHolder.play()
        }
        countDownLatchOnCompletion.await()
    }

    @Test
    fun pause_validSong_pauses() = runTest(timeout = Duration.parse("60s")) {

        // Set up the player holder
        val countDownLatchPlay = CountDownLatch(1)
        val countDownLatchPause = CountDownLatch(1)
        playerHolder.getPlayer().addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        Assert.assertTrue(countDownLatchPause.count == 1L)
                        countDownLatchPlay.countDown()
                    } else if (countDownLatchPlay.count == 0L) {
                        countDownLatchPause.countDown()
                    }
                }
            }
        )

        // Play a song and wait for it to start
        UiThreadStatement.runOnUiThread {
            playerHolder.clearAndPlay(
                application,
                shortestSong
            )
        }
        countDownLatchPlay.await()

        // Pause the song and wait for it to pause
        UiThreadStatement.runOnUiThread {
            playerHolder.pause()
        }
        countDownLatchPause.await()

    }

}