package com.fourth_finger.pinky_player

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

/**
 * The main [Activity].
 */
class ActivityMain : AppCompatActivity() {

    private val viewModel: ActivityMainViewModel by viewModels()

    /**
     *  Makes sure the proper permissions are granted and
     *  then loads music files from the MediaStore.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissionAndLoadMusicFiles()
    }

    /**
     *  Makes sure the proper permissions are granted and
     *  then loads music files from the MediaStore.
     */
    private fun requestPermissionAndLoadMusicFiles() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(READ_MEDIA_AUDIO)
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            requestPermission(READ_EXTERNAL_STORAGE)
        }
    }

    /**
     * Requests a permission.
     *
     * @param permission The [Manifest.permission] to request.
     */
    private fun requestPermission(permission: String) {

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    viewModel.permissionGranted(contentResolver)
                } else {
                    displayPermissionNeeded()
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                this,
                READ_MEDIA_AUDIO,
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.permissionGranted(contentResolver)
            }
            shouldShowRequestPermissionRationale(permission) -> {
                displayPermissionNeeded()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }

    }

    /**
     * Lets the user know that permission is needed to access the music files.
     */
    private fun displayPermissionNeeded() {
        val thisView = findViewById<View>(R.id.activity_main)
        Snackbar.make(
            thisView,
            R.string.permission_needed,
            16000
        ).show()
    }

}