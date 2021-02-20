package com.fourthfinger.pinkyplayer.songs

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.ToastUtil.Companion.showToast
import com.fourthfinger.pinkyplayer.databinding.FragmentTitleBinding
import com.fourthfinger.pinkyplayer.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class FragmentTitle : Fragment() {

    private var _binding: FragmentTitleBinding? = null

    private val viewModelSongs: SongsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private val viewModelSettings: SettingsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var requestPermissionLauncher : ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        findNavController().navigate(R.id.fragmentLoading)
                    } else {
                        showToast(requireContext(), R.string.permission_read_needed)
                        runBlocking {
                            delay(1000)
                        }
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
    }

    // container: ViewGroup? means container may be null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentTitleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loaded = MediatorLiveDataLoading().isLoaded(viewModelSongs.isLoaded, viewModelSettings.isLoaded)
        loaded.observe(viewLifecycleOwner, { isLoaded ->
            if (isLoaded != true) {
                checkPermissions()
            }
        })
    }

    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                findNavController().navigate(R.id.fragmentLoading)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                showToast(requireContext(), R.string.permission_read_needed)
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.buttonSettings.setOnClickListener {
            safeNav(FragmentTitleDirections.actionFragmentTitleToFragmentSettings())
        }
        binding.buttonSongs.setOnClickListener {
            safeNav(FragmentTitleDirections.actionFragmentTitleToFragmentSongs())
        }
    }

    private fun safeNav(action: NavDirections) {
        val dest = findNavController().currentDestination
        if (dest != null) {
            if (dest.id == R.id.fragmentTitle) {
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}