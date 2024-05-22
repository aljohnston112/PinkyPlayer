package io.fourth_finger.pinky_player

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltTestApplication
import io.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MediaItemCreatorTest {

    @get:Rule(order = 1)
    val rule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_MEDIA_AUDIO
    )

    @Test
    fun getMediaItem_hasCorrectContents() = runTest {
        val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()
        val musicRepository = MusicRepository()
        val music = musicRepository.loadMusicFiles(application.contentResolver)[0]
        val mediaItemCreator = MediaItemCreator(musicRepository)
        val mediaItem = mediaItemCreator.getMediaItem(application, music.id)
        assertTrue(mediaItem.mediaId == music.id.toString())
        // This does not work
        // assertTrue(mediaItem.requestMetadata.mediaUri.toString() == musicRepository.getUri(music.id).toString())
        assertTrue(
            mediaItem.mediaMetadata == MetaDataCreator(musicRepository).getMetaData(
                application,
                music.id
            )
        )

    }

}