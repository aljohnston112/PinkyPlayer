package io.fourth_finger.pinky_player

import android.Manifest
import android.media.MediaMetadataRetriever
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

    private fun get_music_id_of_shortest_song(music: List<MusicFile>): Long {
        val application = ApplicationProvider.getApplicationContext<MainApplication>()
        val musicRepository = application.musicRepository
        var shortestMusic = music[0].id

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, musicRepository.getUri(music[0].id))
        var durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        var shortestDuration = durationStr!!.toInt()

        for(m in music){
            mmr.setDataSource(context, musicRepository.getUri(m.id))
            durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

            val duration = durationStr!!.toInt()
            if(duration < shortestDuration) {
                shortestDuration = duration
                shortestMusic = m.id
            }
            if (duration < 10000) {
                break
            }
        }
        return shortestMusic
    }

    @Test
    fun play_validSong_playsToCompletion() = runTest(timeout = Duration.parse("60s")) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val countDownLatchOnCompletion = CountDownLatch(1)
        val application = ApplicationProvider.getApplicationContext<MainApplication>()
        val musicRepository = application.musicRepository
        val music = musicRepository.loadMusicFiles(context.contentResolver)!!
        val mediaPlayerHolder = MediaPlayerHolder(musicRepository)


        mediaPlayerHolder.prepareAndPlay(
            context,
            get_music_id_of_shortest_song(music),
            onPrepared = { },
            onCompletion = {
                countDownLatchOnCompletion.countDown()
            }
        )
        countDownLatchOnCompletion.await()
    }

}