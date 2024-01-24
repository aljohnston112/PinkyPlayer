package io.fourth_finger.pinky_player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters

@OptIn(UnstableApi::class)
class PinkyPlayer(context: Context) : ForwardingPlayer(
    ExoPlayer.Builder(context)
        .setSkipSilenceEnabled(true)
        .setSeekParameters(SeekParameters.EXACT)
        .build()
        // TODO add audio focus parameter
) {


}