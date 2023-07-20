package com.fourth_finger.pinky_player

import android.Manifest
import android.media.MediaMetadataRetriever
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.fourth_finger.music_repository.MusicFile
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

    private fun get_music_id_of_shorstes_song(music: List<MusicFile>): Long {
        var shortestMusic = music[0].id;

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, musicRepository.getUri(music[0].id))
        var durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        var shortestDuration = durationStr!!.toInt()

        for(m in music){
            mmr.setDataSource(context, musicRepository.getUri(music[0].id))
            durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

            val duration = durationStr!!.toInt()
            if(duration < shortestDuration) {
                shortestDuration = duration
                shortestMusic = m.id
            }
        }
        return shortestMusic
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun play_validSong_playsToCompletion() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val countDownLatchOnCompletion = CountDownLatch(1)

        val music = musicRepository.loadMusicFiles(context.contentResolver)!!
        val mediaPlayerHolder = MediaPlayerHolder(musicRepository)


        mediaPlayerHolder.prepareAndPlay(
            context,
            get_music_id_of_shorstes_song(music),
            onPrepared = { },
            onCompletion = {
                countDownLatchOnCompletion.countDown()
            }
        )
        countDownLatchOnCompletion.await()
    }

}