package io.fourth_finger.pinky_player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import io.fourth_finger.music_repository.MusicFile
import io.fourth_finger.probability_map.ProbabilityMap

@OptIn(UnstableApi::class)
class PinkyPlayer(
    private var context: Context?,
    private val mediaItemCreator: MediaItemCreator
) : ForwardingPlayer(
    ExoPlayer.Builder(context!!)
        .setSkipSilenceEnabled(true)
        .setSeekParameters(SeekParameters.EXACT)
        .build()
) {
    // TODO add audio focus parameter


    private lateinit var _playlist: ProbabilityMap<MusicFile>

    fun setPlaylist(playlist: ProbabilityMap<MusicFile>) {
        _playlist = playlist
    }

    override fun seekToNextMediaItem() {
        super.seekToNextMediaItem()
        if(::_playlist.isInitialized) {
            addMediaItem(
                mediaItemCreator.getMediaItem(
                    context!!,
                    _playlist.sample().id
                )
            )
            seekTo(
                mediaItemCount - 1,
                C.TIME_UNSET
            )
        }
    }

    override fun release() {
        super.release()
        context = null
    }


}