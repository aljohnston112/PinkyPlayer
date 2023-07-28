package io.fourth_finger.pinky_player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media.session.MediaButtonReceiver

/**
 * The class creates a [NotificationChannel] for a given channel ID and
 * can post updates to it.
 *
 * There are static methods that help with creating [Notification]s.
 */
class NotificationHelper {

    companion object {

        private const val notificationChannelId = "MediaSessionHelperChannelId"

        /**
         * Creates a notification channel.
         */
        fun createNotificationChannel(context: Context) {
            val channelName = "NotificationHelperChannelName"
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.lightColor = ContextCompat.getColor(
                context,
                R.color.md_theme_light_primary
            )
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val service =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(notificationChannel)
        }

        /**
         * Updates a notification.
         *
         * @param context
         * @param notificationId The id of the notification to update.
         * @param notification The new [Notification].
         */
        fun updateNotification(context: Context, notificationId: Int, notification: Notification) {
            try {
                NotificationManagerCompat.from(context).apply {
                    notify(notificationId, notification)
                }
            } catch (e: SecurityException) {
                //TODO make a Toast or something
                throw e
            }
        }

        /**
         * Creates a [Notification] showing the music is paused.
         *
         * @param context
         * @param channelId The notification channel to create the [Notification] for.
         * @param mediaSession The [MediaSessionCompat] containing the meta data for the [Notification].
         * @return The [Notification] showing the music is paused.
         */
        fun createPauseNotification(
            context: Context,
            channelId: String,
            mediaSession: MediaSessionCompat
        ): Notification {
            val builder = NotificationCompat.Builder(context, channelId)
            builder.apply {
                setUpNotificationBuilder(this, mediaSession, context)
                // TODO Integration test this action
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_baseline_pause_24,
                        context.getString(R.string.pause),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context,
                            PlaybackStateCompat.ACTION_PAUSE
                        )
                    )
                )
            }
            return builder.build()
        }

        /**
         * Creates a [Notification] showing the music is playing.
         *
         * @param context
         * @param mediaSession The [MediaSessionCompat] containing the meta data for the [Notification].
         * @return The [Notification] showing the music is playing.
         */
        fun createPlayNotification(
            context: Context,
            mediaSession: MediaSessionCompat
        ): Notification {
            val builder = NotificationCompat.Builder(context, notificationChannelId)
            builder.apply {
                setUpNotificationBuilder(this, mediaSession, context)
                // TODO Integration test this action
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_baseline_play_arrow_24,
                        context.getString(R.string.play),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context,
                            PlaybackStateCompat.ACTION_PLAY
                        )
                    )
                )
            }
            return builder.build()
        }

        /**
         * Creates a [Notification] showing that no music has been started.
         *
         * @param context
         * @param channelId The notification channel to create the [Notification] for.
         * @return The [Notification] showing that no music has been started.
         */
        fun createEmptyNotification(
            context: Context,
            channelId: String
        ): Notification {
            val builder = NotificationCompat.Builder(context, channelId).apply {
                setContentTitle(context.getString(R.string.app_name))
                setContentText(context.getString(R.string.app_despcription))
                setSubText(context.getString(R.string.playToStart))

                val height = context.resources.getDimensionPixelSize(
                    com.google.android.material.R.dimen.compat_notification_large_icon_max_height
                )
                val width = context.resources.getDimensionPixelSize(
                    com.google.android.material.R.dimen.compat_notification_large_icon_max_width
                )
                val drawable =
                    ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)!!
                val bitmap = drawable.toBitmap(width = width, height = height, config = null)
                setLargeIcon(bitmap)

                setSmallIcon(R.drawable.ic_launcher_foreground)
            }
            return builder.build()
        }

        private fun setUpNotificationBuilder(
            builder: NotificationCompat.Builder,
            mediaSession: MediaSessionCompat,
            context: Context
        ) {
            builder.apply {
                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                // TODO How to get primary color from correct theme (light vs. dark) dynamically?
                val colorPrimary = ThemeHelper.getAttr(
                    context,
                    androidx.appcompat.R.attr.colorPrimary
                )
                if (colorPrimary != null) {
                    color = colorPrimary
                }

                setUpNotificationBuilderMetadata(this, mediaSession.controller)
                setContentIntent(mediaSession.controller.sessionActivity)

                // TODO Integration test the stuff in this style
                setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.sessionToken)
                        .setShowActionsInCompactView(0)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                context,
                                PlaybackStateCompat.ACTION_STOP
                            )
                        )
                )

                setDeleteIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_STOP
                    )
                )
            }
        }

        private fun setUpNotificationBuilderMetadata(
            builder: NotificationCompat.Builder,
            controller: MediaControllerCompat?,
        ) {
            val description = controller?.metadata?.description
            if (description != null) {
                builder.apply {
                    setContentTitle(description.title)
                    setContentText(description.subtitle)
                    setSubText(description.description)
                    setLargeIcon(description.iconBitmap)
                    builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                }
            }
        }

        fun updateToPauseNotification(
            context: Context,
            mediaSession: MediaSessionCompat?,
            notificationId: Int
        ) {
            mediaSession?.let { mediaSessionCompat ->
                updateNotification(
                    context,
                    notificationId,
                    createPauseNotification(
                        context,
                        notificationChannelId,
                        mediaSessionCompat
                    )
                )
            }
        }

        fun updateToPlayNotification(
            context: Context,
            mediaSession: MediaSessionCompat?,
            notificationId: Int
        ) {
            mediaSession?.let { mediaSessionCompat ->
                updateNotification(
                    context,
                    notificationId,
                    createPlayNotification(
                        context,
                        mediaSessionCompat
                    )
                )
            }
        }

        fun updateToEmptyNotification(
            context: Context,
            notificationId: Int
        ) {
            updateNotification(
                context,
                notificationId,
                createEmptyNotification(
                    context,
                    notificationChannelId
                )
            )
        }

    }

}