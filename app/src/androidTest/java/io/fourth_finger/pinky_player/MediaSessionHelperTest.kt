package io.fourth_finger.pinky_player

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import io.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

class MediaSessionHelperTest {

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private val musicRepository = MusicRepository()

    @Test
    fun setUpMediaSession_ReturnsCorrectlyConfiguredMediaSession() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val mediaSessionHelper = MediaSessionHelper(musicRepository)
        val countDownLatch = CountDownLatch(1)

        val mediaSessionCallback = object : MediaSessionCompat.Callback() {

            override fun onPlay() {
                super.onPlay()
                countDownLatch.countDown()
            }

        }


        runOnUiThread {
            val token = mediaSessionHelper.setUpMediaSession(context, mediaSessionCallback)!!
            val mediaController = MediaControllerCompat(context, token)

            assert((mediaController.flags and MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS.toLong()) == MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS.toLong())

            val supportedActions = PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
                    PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PREPARE_FROM_URI or
                    PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH or
                    PlaybackStateCompat.ACTION_SEEK_TO or
                    PlaybackStateCompat.ACTION_SET_REPEAT_MODE or
                    PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE or
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                    PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM or
                    PlaybackStateCompat.ACTION_STOP
            assert(mediaController.playbackState.actions == supportedActions)

            mediaController.transportControls.play()

        }
        countDownLatch.await()
    }

    @Test
    fun onPlay_updatesPlaybackStateAndNotification() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val mediaSessionHelper = MediaSessionHelper(musicRepository)
        val musicFiles = musicRepository.loadMusicFiles(context.contentResolver)!!

        val countDownLatch = CountDownLatch(2)

        val mediaSessionCallback = object : MediaSessionCompat.Callback() {}

        val mediaControllerCallback = object : MediaControllerCompat.Callback() {

            override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                assert(state.state == PlaybackStateCompat.STATE_PLAYING)
                countDownLatch.countDown()
            }

        }


        runOnUiThread {
            val token = mediaSessionHelper.setUpMediaSession(context, mediaSessionCallback)!!

            val notificationId1 = 8434436
            val notificationId2 = 349085
            val notificationId3 = 529405

            mediaSessionHelper.onPlayFromMediaId(context, musicFiles[0].id.toString(), notificationId1)
            mediaSessionHelper.onPause(context, notificationId2)

            val mediaController = MediaControllerCompat(context, token)
            mediaController.registerCallback(mediaControllerCallback)

            mediaSessionHelper.onPlay(context, notificationId3)
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = manager.activeNotifications[0]
            assert(notification.id == notificationId3)
            countDownLatch.countDown()
        }

        countDownLatch.await()

    }

    @Test
    fun getStartNotification_returnsNotificationWithCorrectData() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val mediaSessionHelper = MediaSessionHelper(musicRepository)
        val mediaSessionCallback = object : MediaSessionCompat.Callback() {}
        val musicFiles = musicRepository.loadMusicFiles(context.contentResolver)!!
        val musicFile = musicFiles[0]

        val countDownLatch = CountDownLatch(1)
        runOnUiThread {
            mediaSessionHelper.setUpMediaSession(context, mediaSessionCallback)!!

            val notificationId = 8434436

            mediaSessionHelper.onPlayFromMediaId(
                context,
                musicFile.id.toString(),
                notificationId
            )

            val notification = mediaSessionHelper.getStartNotification(context)!!
            assert(notification.extras.getString(NotificationCompat.EXTRA_TITLE) == musicFile.relativePath + musicFile.displayName)
            countDownLatch.countDown()
        }
        countDownLatch.await()
    }

    @Test
    fun onPause_updatesPlaybackStateAndNotification() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val mediaSessionHelper = MediaSessionHelper(musicRepository)
        val musicFiles = musicRepository.loadMusicFiles(context.contentResolver)!!

        val countDownLatch = CountDownLatch(2)

        val mediaSessionCallback = object : MediaSessionCompat.Callback() {}

        val mediaControllerCallback = object : MediaControllerCompat.Callback() {

            override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                assert(state.state == PlaybackStateCompat.STATE_PAUSED)
                countDownLatch.countDown()
            }

        }


        runOnUiThread {
            val token = mediaSessionHelper.setUpMediaSession(context, mediaSessionCallback)!!

            val notificationId1 = 8434436
            val notificationId2 = 349085

            mediaSessionHelper.onPlayFromMediaId(context, musicFiles[0].id.toString(), notificationId1)

            val mediaController = MediaControllerCompat(context, token)
            mediaController.registerCallback(mediaControllerCallback)

            mediaSessionHelper.onPause(context, notificationId2)

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = manager.activeNotifications[0]
            assert(notification.id == notificationId2)
            countDownLatch.countDown()
        }

        countDownLatch.await()

    }


}