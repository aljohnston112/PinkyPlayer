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
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.session.MediaBrowser
import dagger.hilt.android.AndroidEntryPoint
import io.fourth_finger.pinky_player.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * The main [Activity].
 */
@AndroidEntryPoint
class ActivityMain : AppCompatActivity() {

    @Inject
    lateinit var mediaBrowserProvider: MediaBrowserProvider

    private lateinit var binding: ActivityMainBinding

    private val viewModel: ActivityMainViewModel by viewModels()

    private val menuProvider = object : MenuProvider {

        override fun onCreateMenu(
            menu: Menu,
            menuInflater: MenuInflater
        ) {
            menuInflater.inflate(
                R.menu.actvity_main_menu,
                menu
            )
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return false
        }

    }

    /**
     * Handles UI updates in response to player updates
     */
    private val playerListener = object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (playbackState == STATE_ENDED) {
                setPlayButton()
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying) {
                setPauseButton()
            } else {
                setPlayButton()
            }
        }

    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.loadMusic(contentResolver)
        } else {
            viewModel.displayPermissionNeeded(binding.root)
        }
    }

    private fun setPlayButton() {
        binding.buttonPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        binding.controls.visibility = View.VISIBLE
    }

    private fun setPauseButton() {
        binding.buttonPlayPause.setImageResource(R.drawable.ic_baseline_pause_24)
        binding.controls.visibility = View.VISIBLE
    }

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

    override fun onStart() {
        super.onStart()
        requestPermissionsAndLoadMusicFiles()
        mediaBrowserProvider.invokeOnConnection(
            Dispatchers.Main.immediate
        ) { mediaBrowser ->
            mediaBrowser.addListener(playerListener)
        }
        setUpOnClickListener()
    }

    override fun onStop() {
        super.onStop()
        mediaBrowserProvider.getOrNull()?.removeListener(playerListener)
    }

    /**
     *  Makes sure the proper permissions are granted and
     *  then loads the music files from the MediaStore.
     */
    private fun requestPermissionsAndLoadMusicFiles() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(READ_MEDIA_AUDIO)
            requestPermission(POST_NOTIFICATIONS)
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

        // TODO handle notification permission

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
     * Sets up the onClickListener for the play/pause button
     */
    private fun setUpOnClickListener() {
        binding.buttonPlayPause.setOnClickListener {
            mediaBrowserProvider.invokeOnConnection(
                Dispatchers.Main.immediate
            ) { mediaBrowser ->
                viewModel.onPlayPauseClicked(mediaBrowser)
            }
        }
        binding.buttonNext.setOnClickListener {
            mediaBrowserProvider.invokeOnConnection(
                Dispatchers.Main.immediate
            ) { mediaBrowser ->
                viewModel.onNextClicked(mediaBrowser)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeMenuProvider(menuProvider)
    }

}