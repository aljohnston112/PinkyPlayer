package io.fourth_finger.pinky_player

import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

class StateBuilderHelper {

    private val supportedActions = PlaybackStateCompat.ACTION_PLAY or
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

    private val stateBuilder = PlaybackStateCompat.Builder()

    fun setStartingState(mediaSession: MediaSessionCompat) {
        stateBuilder.setActions(
            supportedActions
        )
        mediaSession.setPlaybackState(stateBuilder.build())
    }

    fun setPlayState(mediaSession: MediaSessionCompat?) {
        stateBuilder.setState(
            PlaybackStateCompat.STATE_PLAYING,
            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
            1F
        )
        mediaSession?.setPlaybackState(stateBuilder.build())
    }

    fun setPauseState(mediaSession: MediaSessionCompat?) {
        stateBuilder.setState(
            PlaybackStateCompat.STATE_PAUSED,
            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
            0F
        )
        mediaSession?.setPlaybackState(stateBuilder.build())
    }

    fun setStopState(mediaSession: MediaSessionCompat?) {
        stateBuilder.setState(
            PlaybackStateCompat.STATE_STOPPED,
            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
            1F
        )
        mediaSession?.setPlaybackState(stateBuilder.build())
    }


}