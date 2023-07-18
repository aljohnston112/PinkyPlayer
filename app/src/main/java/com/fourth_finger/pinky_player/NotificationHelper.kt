package com.fourth_finger.pinky_player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.media.session.MediaButtonReceiver
import javax.inject.Inject

/**
 * The class creates a [NotificationChannel] for a given channel ID and
 * can post updates to it.
 *
 * There are static methods that help with creating [Notification]s.
 */
class NotificationHelper @Inject constructor() {

    private lateinit var notificationChannel: NotificationChannel

    /**
     * Creates a notification channel.
     *
     * @param channelId The id of the notification channel.
     */
    fun createNotificationChannel(context: Context, channelId: String) {
        val channelName = "MainMediaBrowserServiceChannelName"
        notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.lightColor = ContextCompat.getColor(
            context,
            R.color.md_theme_light_primary
        )
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

    companion object {

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
            val controller = mediaSession.controller
            val description = controller?.metadata?.description
            val builder = NotificationCompat.Builder(context, channelId).apply {

                setContentTitle(description?.title)
                setContentText(description?.subtitle)
                setSubText(description?.description)
                setLargeIcon(description?.iconBitmap)

                setContentIntent(controller?.sessionActivity)

                setDeleteIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_STOP
                    )
                )

                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                setSmallIcon(R.drawable.ic_launcher_foreground)

                val colorPrimary = ThemeHelper.getAttr(
                    context,
                    androidx.appcompat.R.attr.colorPrimary
                )
                if(colorPrimary != null) {
                    color = colorPrimary
                }

                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_baseline_pause_24,
                        context.getString(R.string.play_pause),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context,
                            PlaybackStateCompat.ACTION_PLAY_PAUSE
                        )
                    )
                )

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
            }
            return builder.build()
        }

        /**
         * Creates a [Notification] showing the music is playing.
         *
         * @param context
         * @param channelId The notification channel to create the [Notification] for.
         * @param mediaSession The [MediaSessionCompat] containing the meta data for the [Notification].
         * @return The [Notification] showing the music is playing.
         */
        fun createPlayNotification(
            context: Context,
            channelId: String,
            mediaSession: MediaSessionCompat
        ): Notification {
            val controller = mediaSession.controller
            val description = controller?.metadata?.description
            val builder = NotificationCompat.Builder(context, channelId).apply {

                setContentTitle(description?.title)
                setContentText(description?.subtitle)
                setSubText(description?.description)
                setLargeIcon(description?.iconBitmap)

                setContentIntent(controller?.sessionActivity)

                setDeleteIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_STOP
                    )
                )

                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                setSmallIcon(R.drawable.ic_launcher_foreground)

                val colorPrimary = ThemeHelper.getAttr(
                    context,
                    androidx.appcompat.R.attr.colorPrimary
                )
                if(colorPrimary != null) {
                    color = colorPrimary
                }

                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_baseline_play_arrow_24,
                        context.getString(R.string.play_pause),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context,
                            PlaybackStateCompat.ACTION_PLAY
                        )
                    )
                )

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
            }
            return builder.build()
        }

        /**
         * Creates a [Notification] showing that no music has been started.
         *
         * @param context
         * @param channelId The notification channel to create the [Notification] for.
         * @param mediaSession The [MediaSessionCompat] containing the meta data for the [Notification].
         * @return The [Notification] showing that no music has been started.
         */
        fun createEmptyNotification(
            context: Context,
            channelId: String,
            mediaSession: MediaSessionCompat
        ): Notification {
            val controller = mediaSession.controller
            val builder = NotificationCompat.Builder(context, channelId).apply {

                setContentIntent(controller?.sessionActivity)

                setDeleteIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_STOP
                    )
                )

                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                setSmallIcon(R.drawable.ic_launcher_foreground)

                val colorPrimary = ThemeHelper.getAttr(
                    context,
                    androidx.appcompat.R.attr.colorPrimary
                )
                if(colorPrimary != null) {
                    color = colorPrimary
                }

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
            }
            return builder.build()
        }

    }

}