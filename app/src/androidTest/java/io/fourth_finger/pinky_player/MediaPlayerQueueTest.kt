package io.fourth_finger.pinky_player

import android.Manifest
import android.media.MediaPlayer
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import kotlin.time.Duration

class MediaPlayerQueueTest {

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Test
    fun clearPrepareAndPlay_withValidSong_preparesAndPlaysIt() =
        runTest(timeout = Duration.parse("60s")) {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val application = ApplicationProvider.getApplicationContext<MainApplication>()
            val musicRepository = application.musicRepository
            val music = musicRepository.loadMusicFiles(context.contentResolver)!!
            val shortestSong = MediaPlayerUtil.get_music_id_of_shortest_song(music)

            val countDownLatch = CountDownLatch(2)
            val mediaPlayerQueue = MediaPlayerQueue(musicRepository)
            mediaPlayerQueue.clearPrepareAndPlay(
                context,
                shortestSong,
                onPrepared = { countDownLatch.countDown() },
                onCompletion = { countDownLatch.countDown() }
            )
            countDownLatch.await()
        }

    @Test
    fun pause_pausesCurrentlyPlayingMusic() = runTest(timeout = Duration.parse("60s")) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val application = ApplicationProvider.getApplicationContext<MainApplication>()
        val musicRepository = application.musicRepository
        val music = musicRepository.loadMusicFiles(context.contentResolver)!!
        val shortestSong = MediaPlayerUtil.get_music_id_of_shortest_song(music)

        val countDownLatch = CountDownLatch(1)
        val mediaPlayerQueue = MediaPlayerQueue(musicRepository)
        var mediaPlayer: MediaPlayer? = null
        mediaPlayerQueue.clearPrepareAndPlay(
            context,
            shortestSong,
            onPrepared = {
                mediaPlayer = it
                countDownLatch.countDown()
            },
        )
        countDownLatch.await()
        mediaPlayerQueue.play()
        assert(mediaPlayer!!.isPlaying)
        mediaPlayerQueue.pause()
        assert(!mediaPlayer!!.isPlaying)
    }

    @Test
    fun play_playsSongToCompletionAfterPause() = runTest(timeout = Duration.parse("60s")) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val application = ApplicationProvider.getApplicationContext<MainApplication>()
        val musicRepository = application.musicRepository
        val music = musicRepository.loadMusicFiles(context.contentResolver)!!
        val shortestSong = MediaPlayerUtil.get_music_id_of_shortest_song(music)

        val countDownLatchOnPrepared = CountDownLatch(1)
        val countDownLatchOnCompletion = CountDownLatch(1)
        val mediaPlayerQueue = MediaPlayerQueue(musicRepository)
        var mediaPlayer: MediaPlayer? = null
        mediaPlayerQueue.clearPrepareAndPlay(
            context,
            shortestSong,
            onPrepared = {
                mediaPlayer = it
                countDownLatchOnPrepared.countDown()
            },
            onCompletion = {
                countDownLatchOnCompletion.countDown()
            }
        )
        countDownLatchOnPrepared.await()
        mediaPlayerQueue.pause()
        assert(!mediaPlayer!!.isPlaying)
        mediaPlayerQueue.play()
        countDownLatchOnCompletion.await()
    }

}