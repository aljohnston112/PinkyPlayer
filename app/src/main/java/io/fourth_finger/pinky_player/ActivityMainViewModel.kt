package io.fourth_finger.pinky_player

import android.content.ContentResolver
import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import com.google.android.material.snackbar.Snackbar
import io.fourth_finger.music_repository.MusicFile
import io.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * The [ViewModel] for [ActivityMain].
 *
 * @param musicRepository
 * @param mediaItemCreator For creating [MediaItem]s.
 */
class ActivityMainViewModel(
    private val musicRepository: MusicRepository,
    private val mediaItemCreator: MediaItemCreator
) : ViewModel() {

    val musicFiles: LiveData<List<MusicFile>> = musicRepository.musicFiles

    private val _havePermission = MutableLiveData(false)
    val havePermission: LiveData<Boolean> = _havePermission

    /**
     * Lets the user know that permission is needed to access the music files.
     */
    fun displayPermissionNeeded(view: View) {
        Snackbar.make(
            view,
            R.string.permission_needed,
            16000
        ).show()
    }

    /**
     * Loads the music files from the MediaStore.
     * Must only be called when there is permission to load the music files.
     *
     * @param contentResolver The ContentResolver to query for music files.
     * @return The job that loads the music files.
     */
    fun loadMusic(
        contentResolver: ContentResolver,
    ): Job {
        _havePermission.postValue(true)
        return loadMusicFiles(contentResolver)
    }

    /**
     * Loads music files into [musicFiles].
     * If [loadMusic] has not been called then there will be no music.
     *
     * @return The job that loads the music files.
     */
    private fun loadMusicFiles(contentResolver: ContentResolver): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            musicRepository.loadMusicFiles(contentResolver)
        }
    }

    /**
     * Starts playing a music file.
     *
     * @param id The id of the [MusicFile] corresponding to the music file to play.
     * @param controller The [MediaController] connected to the [ServiceMediaLibrary].
     */
    fun songClicked(
        context: Context,
        id: Long,
        controller: MediaController
    ) {
        controller.setMediaItem(
            mediaItemCreator.getMediaItem(context, id)
        )
        controller.play()
    }

    /**
     * Pauses or plays the current song.
     *
     * @param controller The [MediaController] connected to the [ServiceMediaLibrary].
     *
     */
    fun onPlayPauseClicked(
        controller: MediaController
    ) {
        if (controller.isPlaying) {
            controller.pause()
        } else {
            controller.play()
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as ApplicationMain
                return ActivityMainViewModel(
                    application.musicRepository,
                    application.mediaItemCreator
                ) as T
            }

        }
    }

}