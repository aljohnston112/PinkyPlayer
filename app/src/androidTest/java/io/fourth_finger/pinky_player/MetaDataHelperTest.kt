package io.fourth_finger.pinky_player

import android.Manifest
import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import kotlin.time.Duration

class MetaDataHelperTest {

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Test
    fun updateMetaData_updatesMetaData() = runTest(timeout = Duration.parse("60s")) {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val mediaSession = MediaSessionCompat(context, "TAG")
        val application = ApplicationProvider.getApplicationContext<MainApplication>()
        val musicRepository = application.musicRepository
        val metaDataHelper = MetaDataHelper(musicRepository)
        val music = musicRepository.loadMusicFiles(context.contentResolver)!!
        val testMusic = music[0]

        val mediaController = MediaControllerCompat(
            context,
            mediaSession.sessionToken
        )

        val countDownLatch = CountDownLatch(1)

        runOnUiThread {
            mediaController.registerCallback(
                object : MediaControllerCompat.Callback() {
                    override fun onMetadataChanged(metadata: MediaMetadataCompat) {
                        val title = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                        assert(title == testMusic.relativePath + testMusic.displayName)
                        countDownLatch.countDown()
                    }
                }
            )
        }

        metaDataHelper.updateMetaData(context, testMusic.id.toString(), mediaSession)
        countDownLatch.await()
    }

}