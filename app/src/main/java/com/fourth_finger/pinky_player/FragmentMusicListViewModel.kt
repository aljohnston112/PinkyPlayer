package com.fourth_finger.pinky_player

import android.media.session.MediaController
import androidx.lifecycle.*
import com.fourth_finger.music_repository.MusicFile
import com.fourth_finger.music_repository.MusicRepository

/**
 * The [ViewModel] for [FragmentMusicList].
 */
class FragmentMusicListViewModel : ViewModel() {

    private val musicRepository = MusicRepository.getInstance()

    val musicFiles: LiveData<Collection<MusicFile>> = musicRepository.musicFiles

    /**
     * Starts playing the song that has been clicked.
     */
    fun songClicked(
        id: Long,
        transportControls: MediaController.TransportControls
    ) {
        transportControls.playFromMediaId(id.toString(), null)
    }

}