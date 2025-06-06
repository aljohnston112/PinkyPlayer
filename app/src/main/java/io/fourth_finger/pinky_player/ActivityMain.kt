package io.fourth_finger.pinky_player

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View.VISIBLE
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.fourth_finger.pinky_player.databinding.ActivityMainBinding
import io.fourth_finger.event_processor.EventProcessor
import javax.inject.Inject

/**
 * The main [Activity].
 */
@AndroidEntryPoint
class ActivityMain : AppCompatActivity() {

    @Inject
    lateinit var eventProcessor: EventProcessor

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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.loadMusic(contentResolver)
        } else {
            viewModel.displayPermissionNeeded(this)
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        addMenuProvider(
            menuProvider,
            this,
            Lifecycle.State.RESUMED
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolbar()
    }

    private fun setPlayPauseButton(drawable: Int) {
        binding.buttonPlayPause.setBackgroundResource(drawable)
    }

    /**
     * Requests a permission.
     *
     * @param permission The [Manifest.permission] to request.
     */
    private fun requestPermission(permission: String) {

        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.loadMusic(contentResolver)
            }

            shouldShowRequestPermissionRationale(permission) -> {
                viewModel.displayPermissionNeeded(this)
            }

            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }

    }

    /**
     *  Makes sure the proper permissions are granted and
     *  then loads the music files from the MediaStore.
     */
    private fun requestPermissionsAndLoadMusicFiles() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(READ_MEDIA_AUDIO)
        } else {
            requestPermission(READ_EXTERNAL_STORAGE)
        }
    }

    /**
     * Sets up the onClickListeners for the media playback buttons
     */
    private fun setUpOnClickListeners() {
        binding.buttonPlayPause.setOnClickListener {
            eventProcessor.onPlayPauseClicked(this@ActivityMain)
        }
        binding.buttonNext.setOnClickListener {
            eventProcessor.onNextClicked()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.havePermission.observe(this) { havePermission ->
            if (havePermission) {
                binding.controls.visibility = VISIBLE
            }
        }
        eventProcessor.playbackStarted.observe(this){ playbackStarted ->
            if (playbackStarted){
                binding.buttonNext.visibility = VISIBLE
            }
        }
        eventProcessor.playing.observe(this) { isPlaying ->
            if (isPlaying) {
                setPlayPauseButton(R.drawable.ic_baseline_pause_24)
            } else {
                setPlayPauseButton(R.drawable.ic_baseline_play_arrow_24)
            }
        }
        requestPermissionsAndLoadMusicFiles()
        setUpOnClickListeners()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeMenuProvider(menuProvider)
    }

}