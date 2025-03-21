package io.fourth_finger.pinky_player

import android.content.ContentResolver
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The [androidx.lifecycle.ViewModel] for [io.fourth_finger.pinky_player.ActivityMain].
 *
 */
@HiltViewModel
class ActivityMainViewModel @Inject constructor(
    private val applicationScope: CoroutineScope,
    private val musicRepository: MusicRepository
) : ViewModel() {


    private val _havePermission = MutableLiveData(false)
    val havePermission: LiveData<Boolean> = _havePermission

    /**
     * Lets the user know that permission is needed to access the music files.
     */
    fun displayPermissionNeeded(activity: FragmentActivity) {
        DialogPermission().show(
            activity.supportFragmentManager,
            activity.resources.getString(R.string.permission_needed)
        )
    }

    /**
     * Loads the music files from the MediaStore.
     * Must only be called when there is permission to load the music files.
     *
     * @param contentResolver The ContentResolver to query for music files.
     * @return The job that loads the music files.
     */
    fun loadMusic(contentResolver: ContentResolver): Job {
        _havePermission.postValue(true)
        return applicationScope.launch {
            musicRepository.loadMusicFiles(contentResolver)
        }
    }

}