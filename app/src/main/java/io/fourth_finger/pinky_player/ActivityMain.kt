package io.fourth_finger.pinky_player

import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.app.Activity
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import io.fourth_finger.pinky_player.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

/**
 * The main [Activity].
 */
class ActivityMain : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ActivityMainViewModel by viewModels(
        factoryProducer = { ActivityMainViewModel.Factory }
    )

    private lateinit var mediaBrowser: MediaBrowserCompat
    private lateinit var mediaController: MediaControllerCompat

    /**
     * Sets up the onClickListeners
     */
    private fun setUpOnClickListeners() {
        binding.buttonPlayPause.setOnClickListener {
            viewModel.onPlayPauseClicked(mediaController)
        }
    }

    /**
     *  Makes sure the proper permissions are granted and
     *  then loads music files from the MediaStore.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermissionsAndLoadMusicFiles()
    }

    /**
     *  Makes sure the proper permissions are granted and
     *  then loads music files from the MediaStore.
     */
    private fun requestPermissionsAndLoadMusicFiles() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(READ_MEDIA_AUDIO)
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            requestPermission(READ_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(POST_NOTIFICATIONS)
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
        Snackbar.make(
            binding.root,
            R.string.permission_needed,
            16000
        ).show()
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser = MediaBrowserCompat(
            this,
            ComponentName(this, ServiceMediaBrowser::class.java),
            mediaBrowserConnectionCallback,
            null
        )
        mediaBrowser.connect()
    }

    override fun onStop() {
        super.onStop()
        if (this::mediaBrowser.isInitialized) {
            if (this::mediaController.isInitialized) {
                mediaController.unregisterCallback(mediaControllerCallback)
            }
            mediaBrowser.disconnect()
        }
    }

    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {

        override fun onMetadataChanged(metadata: MediaMetadataCompat) {

        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
            when (state.state) {
                PlaybackStateCompat.STATE_PAUSED -> {
                    binding.buttonPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    binding.controls.visibility = View.VISIBLE
                }

                PlaybackStateCompat.STATE_PLAYING -> {
                    binding.buttonPlayPause.setImageResource(R.drawable.ic_baseline_pause_24)
                    binding.controls.visibility = View.VISIBLE
                }

                PlaybackStateCompat.STATE_STOPPED -> {
                    binding.controls.visibility = View.GONE
                }

                PlaybackStateCompat.STATE_NONE -> {
                    binding.controls.visibility = View.GONE
                }

            }
        }

        override fun onSessionDestroyed() {
            mediaBrowser.disconnect()
            // maybe schedule a reconnection using a new MediaBrowser instance
        }

    }

    private val mediaBrowserConnectionCallback = object : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            super.onConnected()
            mediaBrowser.sessionToken.also { token ->
                mediaController = MediaControllerCompat(
                    this@ActivityMain,
                    token
                )
                MediaControllerCompat.setMediaController(
                    this@ActivityMain,
                    mediaController
                )

                setUpOnClickListeners()
            }

            mediaController.registerCallback(this@ActivityMain.mediaControllerCallback)
        }

        override fun onConnectionSuspended() {
            // The Service has crashed. Disable transport controls until it automatically reconnects
        }

        override fun onConnectionFailed() {
            // The Service has refused our connection
        }

    }

}