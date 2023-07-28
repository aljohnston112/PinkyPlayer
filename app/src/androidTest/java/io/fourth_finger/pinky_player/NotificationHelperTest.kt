package io.fourth_finger.pinky_player

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.ContextThemeWrapper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.EXTRA_LARGE_ICON
import androidx.core.app.NotificationCompat.EXTRA_SUB_TEXT
import androidx.core.app.NotificationCompat.EXTRA_TEXT
import androidx.core.app.NotificationCompat.EXTRA_TITLE
import androidx.core.graphics.drawable.toBitmap
import androidx.media.session.MediaButtonReceiver
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test

class NotificationHelperTest {

    // There are problems with checking icons.
    // The small icons do not appear to propagate; they are null
    // The large icon drawable does not match for some reason ???

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    private val testTitle = "musicFile.relativePath + musicFile.displayName"
    private val testSubtitle = "musicFile.relativePath + musicFile.subtitle"
    private val testDescription = "musicFile.relativePath + musicFile.description"
    private val testLargeIcon: Bitmap = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888)

    private val mediaSession = MediaSessionCompat(context, "TAG").apply {
        isActive = true
        val stateBuilder = PlaybackStateCompat.Builder()
        stateBuilder.setState(
            PlaybackStateCompat.STATE_PLAYING,
            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
            1F
        )
        val metaDataBuilder = MediaMetadataCompat.Builder()
        metaDataBuilder.putString(
            MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,
            testTitle
        )
        metaDataBuilder.putString(
            MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,
            testSubtitle
        )
        metaDataBuilder.putString(
            MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,
            testDescription
        )
        metaDataBuilder.putBitmap(
            MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON,
            testLargeIcon
        )
        setPlaybackState(stateBuilder.build())
        setMetadata(metaDataBuilder.build())
    }

    @Test
    fun createNotificationChannel_createsNotificationChannel() {
        NotificationHelper.createNotificationChannel(
            ContextThemeWrapper(context, R.style.AppTheme)
        )
        val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        assert(service.notificationChannels.isNotEmpty())
    }

    @Test
    fun updateNotification_updatesNotificationChannel() {
        val contextWithTheme = ContextThemeWrapper(context, R.style.AppTheme)
        NotificationHelper.createNotificationChannel(
            contextWithTheme
        )
        val testNotification =
            NotificationHelper.createPlayNotification(contextWithTheme, mediaSession)
        val notificationId = 9677454
        NotificationHelper.updateNotification(contextWithTheme, notificationId, testNotification)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = manager.activeNotifications[0]
        assert(notification.id == notificationId)
    }

    @Test
    fun createPauseNotification_createsNotificationWithCorrectMetadata() {
        val channelId =
            "NotificationHelperTest.createPauseNotification_createsNotificationWithCorrectMetadata"
        val notification = NotificationHelper.createPauseNotification(
            ContextThemeWrapper(context, R.style.AppTheme),
            channelId,
            mediaSession
        )
        val title = notification.extras.getString(EXTRA_TITLE)
        val subTitle = notification.extras.getString(EXTRA_TEXT)
        val description = notification.extras.getString(EXTRA_SUB_TEXT)
        val largeIcon =
            (notification.extras.get(EXTRA_LARGE_ICON) as Icon).loadDrawable(context)!!.toBitmap()
        // val smallIcon = (notification.extras.get(EXTRA_SMALL_ICON) as Icon).loadDrawable(context)!!

        assert(testTitle == title)
        assert(testSubtitle == subTitle)
        assert(testDescription == description)
        assert(largeIcon.sameAs(testLargeIcon))
        // assert(smallIcon == context.getDrawable(R.drawable.ic_launcher_foreground))
    }

    @Test
    fun createPlayNotification_createsNotificationWithCorrectMetadata() {
        val notification = NotificationHelper.createPlayNotification(
            ContextThemeWrapper(context, R.style.AppTheme),
            mediaSession
        )
        val title = notification.extras.getString(EXTRA_TITLE)
        val subTitle = notification.extras.getString(EXTRA_TEXT)
        val description = notification.extras.getString(EXTRA_SUB_TEXT)
        val largeIcon =
            (notification.extras.get(EXTRA_LARGE_ICON) as Icon).loadDrawable(context)!!.toBitmap()
        //val smallIcon = (notification.extras.get(EXTRA_SMALL_ICON) as Icon).loadDrawable(context)!!

        assert(testTitle == title)
        assert(testSubtitle == subTitle)
        assert(testDescription == description)
        assert(largeIcon.sameAs(testLargeIcon))
        // assert(smallIcon == context.getDrawable(R.drawable.ic_launcher_foreground))
    }

    @Test
    fun createEmptyNotification_createsNotificationWithCorrectMetadata() {
        val channelId =
            "NotificationHelperTest.createEmptyNotification_createsNotificationWithCorrectMetadata"
        val notification = NotificationHelper.createEmptyNotification(
            ContextThemeWrapper(context, R.style.AppTheme),
            channelId,
        )
        val title = notification.extras.getString(EXTRA_TITLE)
        val subTitle = notification.extras.getString(EXTRA_TEXT)
        val description = notification.extras.getString(EXTRA_SUB_TEXT)
        // val largeIcon = (notification.extras.get(EXTRA_LARGE_ICON) as Icon).loadDrawable(context)
        // val smallIcon = (notification.extras.get(EXTRA_SMALL_ICON) as Icon).loadDrawable(context)!!

        assert(context.getString(R.string.app_name) == title)
        assert(context.getString(R.string.app_despcription) == subTitle)
        assert(context.getString(R.string.playToStart) == description)
        // assert(largeIcon == ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)!!)
        // assert(smallIcon == context.getDrawable(R.drawable.ic_launcher_foreground))
    }

    @Test
    fun createPauseNotification_createsNotificationWithCorrectSetup() {
        val channelId =
            "NotificationHelperTest.createPauseNotification_createsNotificationWithCorrectSetup"
        val notification = NotificationHelper.createPauseNotification(
            ContextThemeWrapper(context, R.style.AppTheme),
            channelId,
            mediaSession
        )

        val colorPrimary = ThemeHelper.getAttr(
            ContextThemeWrapper(context, R.style.AppTheme),
            com.google.android.material.R.attr.colorPrimary
        )

        assert(notification.visibility == NotificationCompat.VISIBILITY_PUBLIC)
        assert(notification.color == colorPrimary)
        assert(notification.contentIntent == mediaSession.controller.sessionActivity)
        assert(
            notification.deleteIntent ==
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_STOP
                    )
        )
    }

    @Test
    fun createPlayNotification_createsNotificationWithCorrectSetup() {
        val notification = NotificationHelper.createPlayNotification(
            ContextThemeWrapper(context, R.style.AppTheme),
            mediaSession
        )

        val colorPrimary = ThemeHelper.getAttr(
            ContextThemeWrapper(context, R.style.AppTheme),
            com.google.android.material.R.attr.colorPrimary
        )

        assert(notification.visibility == NotificationCompat.VISIBILITY_PUBLIC)
        assert(notification.color == colorPrimary)
        assert(notification.contentIntent == mediaSession.controller.sessionActivity)
        assert(
            notification.deleteIntent ==
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        ContextThemeWrapper(context, R.style.AppTheme),
                        PlaybackStateCompat.ACTION_STOP
                    )
        )
    }

}