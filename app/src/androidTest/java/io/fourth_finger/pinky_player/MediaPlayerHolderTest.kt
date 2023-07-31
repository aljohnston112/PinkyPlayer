package io.fourth_finger.pinky_player

import android.Manifest
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import io.fourth_finger.music_repository.MusicFile
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import kotlin.time.Duration

/**
 * Tests [MediaPlayerHolder].
 */
class MediaPlayerHolderTest {

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun prepareAndPlay_validSong_playsToCompletion() = runTest(timeout = Duration.parse("60s")) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val countDownLatchOnCompletion = CountDownLatch(1)
        val application = ApplicationProvider.getApplicationContext<MainApplication>()
        val musicRepository = application.musicRepository
        val music = musicRepository.loadMusicFiles(context.contentResolver)!!
        val mediaPlayerHolder = MediaPlayerHolder(musicRepository)


        mediaPlayerHolder.prepareAndPlay(context,
            MediaPlayerUtil.get_music_id_of_shortest_song(music),
            onPrepared = { },
            onCompletion = {
                countDownLatchOnCompletion.countDown()
            })
        countDownLatchOnCompletion.await()
    }

    @Test
    fun play_validSong_playsToCompletionAfterPause() = runTest(timeout = Duration.parse("60s")) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val countDownLatchOnCompletion = CountDownLatch(1)
        val countDownLatchOnPrepared = CountDownLatch(1)
        val application = ApplicationProvider.getApplicationContext<MainApplication>()
        val musicRepository = application.musicRepository
        val music = musicRepository.loadMusicFiles(context.contentResolver)!!
        val mediaPlayerHolder = MediaPlayerHolder(musicRepository)

        var mediaPlayer: MediaPlayer? = null
        mediaPlayerHolder.prepareAndPlay(
            context,
            MediaPlayerUtil.get_music_id_of_shortest_song(music),
            onPrepared = {
                mediaPlayer = it
                countDownLatchOnPrepared.countDown()
            },
            onCompletion = {
                countDownLatchOnCompletion.countDown()
            }
        )

        countDownLatchOnPrepared.await()
        mediaPlayerHolder.pause()
        assert(mediaPlayer!!.isPlaying)
        mediaPlayerHolder.play()
        countDownLatchOnCompletion.await()
    }

    @Test
    fun pause_validSong_pausesMediaPlayer() = runTest(timeout = Duration.parse("60s")) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val application = ApplicationProvider.getApplicationContext<MainApplication>()
        val musicRepository = application.musicRepository
        val music = musicRepository.loadMusicFiles(context.contentResolver)!!
        val mediaPlayerHolder = MediaPlayerHolder(musicRepository)
        val countDownLatch = CountDownLatch(1)

        var mediaPlayer: MediaPlayer? = null
        mediaPlayerHolder.prepareAndPlay(
            context,
            MediaPlayerUtil.get_music_id_of_shortest_song(music),
            onPrepared = {
                mediaPlayer = it
                countDownLatch.countDown()
            },
        )
        countDownLatch.await()
        mediaPlayerHolder.play()
        assert(mediaPlayer!!.isPlaying)
        mediaPlayerHolder.pause()
        assert(!mediaPlayer!!.isPlaying)
    }

}