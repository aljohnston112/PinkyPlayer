package com.fourth_finger.pinky_player

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The ViewModel for ActivityMain
 */
class ActivityMainViewModel: ViewModel() {

//    private val _uiState = MutableStateFlow(ActivityMainState())
//    val uiState: StateFlow<ActivityMainState> = _uiState

    /**
     * Call when there is permission to search for music files.
     *
     * @param contentResolver the ContentResolver for loading music files.
     */
    fun permissionGranted(contentResolver: ContentResolver) {
        DataSourceMedia.getMusicFromMediaStore(contentResolver)
    }

}