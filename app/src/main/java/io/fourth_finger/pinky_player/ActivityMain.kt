package io.fourth_finger.pinky_player

import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.session.MediaBrowser
import dagger.hilt.android.AndroidEntryPoint
import io.fourth_finger.pinky_player.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * The main [Activity].
 */
@AndroidEntryPoint
class ActivityMain : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var mediaBrowserProvider: MediaBrowserProvider

    private val viewModel: ActivityMainViewModel by viewModels()

    /**
     * Handles UI updates in response to player updates
     */
    private val listener = object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (playbackState == STATE_ENDED) {
                binding.buttonPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                binding.controls.visibility = View.VISIBLE
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying) {
                binding.buttonPlayPause.setImageResource(R.drawable.ic_baseline_pause_24)
                binding.controls.visibility = View.VISIBLE
            }
        }

    }

    private val menuProvider = object : MenuProvider {

        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.actvity_main_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return false
        }

    }

    /**
     * Sets up the onClickListener for the play/pause button
     */
    private fun setUpOnClickListener() {
        binding.buttonPlayPause.setOnClickListener {
            mediaBrowserProvider.invokeOnConnection(Dispatchers.Main.immediate) { mediaBrowser ->
                viewModel.onPlayPauseClicked(mediaBrowser)
                if (mediaBrowser.isPlaying) {
                    binding.buttonPlayPause.setImageResource(R.drawable.ic_baseline_pause_24)
                    binding.controls.visibility = View.VISIBLE
                } else {
                    binding.buttonPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    binding.controls.visibility = View.VISIBLE
                }
            }
        }
        binding.buttonNext.setOnClickListener {
            mediaBrowserProvider.invokeOnConnection(Dispatchers.Main.immediate) { mediaBrowser ->
                mediaBrowser.seekToNextMediaItem()
            }
        }
    }


    /**
     *  Makes sure the proper permissions are granted and
     *  then loads the music files from the MediaStore.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolbar()
    }

    private fun setUpToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        addMenuProvider(
            menuProvider,
            this,
            Lifecycle.State.RESUMED
        )
    }

    /**
     *  Makes sure the proper permissions are granted and
     *  then loads the music files from the MediaStore.
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

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.loadMusic(contentResolver)
            } else {
                viewModel.displayPermissionNeeded(binding.root)
            }
        }

        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.loadMusic(contentResolver)
            }

            shouldShowRequestPermissionRationale(permission) -> {
                viewModel.displayPermissionNeeded(binding.root)
            }

            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }

    }

    /**
     * Gets the application's [MediaBrowser] and adds a listener to it.
     */
    override fun onStart() {
        super.onStart()
        requestPermissionsAndLoadMusicFiles()
        mediaBrowserProvider.invokeOnConnection(Dispatchers.Main.immediate) { mediaBrowser ->
            mediaBrowser.addListener(listener)
        }
        setUpOnClickListener()
    }

    override fun onStop() {
        super.onStop()
        mediaBrowserProvider.getOrNull()?.removeListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeMenuProvider(menuProvider)
    }

}