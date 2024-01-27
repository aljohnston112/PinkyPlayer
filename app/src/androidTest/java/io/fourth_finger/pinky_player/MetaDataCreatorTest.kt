package io.fourth_finger.pinky_player

import android.Manifest
import android.content.ContentResolver
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class MetaDataCreatorTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @Inject lateinit var musicRepository: MusicRepository

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun updateMetaData_updatesMetaData() = runTest {
        val application = ApplicationProvider.getApplicationContext<HiltTestApplication>()
        val musicRepository = musicRepository
        val music = musicRepository.loadMusicFiles(application.contentResolver)
        val metaDataCreator = MetaDataCreator(musicRepository)

        for(testMusic in music) {

            val metadata = metaDataCreator.getMetaData(application, testMusic.id)

            val resources = application.resources
            val resourceId = R.drawable.ic_baseline_music_note_24
            val uri = musicRepository.getUri(testMusic.id) ?: Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resourceId))
                .appendPath(resources.getResourceTypeName(resourceId))
                .appendPath(resources.getResourceEntryName(resourceId))
                .build()

            Assert.assertTrue(metadata.title == testMusic.relativePath + testMusic.displayName)
            Assert.assertTrue(metadata.artworkUri == uri)
        }

    }

}