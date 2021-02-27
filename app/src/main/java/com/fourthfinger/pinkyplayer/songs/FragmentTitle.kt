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
import androidx.navigation.fragment.findNavController
import com.fourthfinger.pinkyplayer.NavUtil.Companion.safeNav
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.ToastUtil.Companion.showToast
import com.fourthfinger.pinkyplayer.databinding.FragmentTitleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentTitle : Fragment() {

    private var _binding: FragmentTitleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var requestPermissionLauncher : ActivityResultLauncher<String>

    private var loaded = false

    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        findNavController().navigate(R.id.fragmentLoading)
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
        val loadingCallback = LoadingCallback.getInstance()
        val mediatorLiveDataLoading = MediatorLiveDataLoading()
                mediatorLiveDataLoading.isLoaded(
                        loadingCallback.songsLoaded,
                        loadingCallback.settingsLoaded,
                        loadingCallback.playlistsLoaded,
        ).observe(viewLifecycleOwner, { _isLoaded ->
            loaded = _isLoaded
            if (_isLoaded != true) {
                checkPermissions()
            }
        })
    }

    private fun checkPermissions() : Boolean {
        val granted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        when {
             granted-> {
                if(!loaded && !loading){
                    findNavController().navigate(R.id.fragmentLoading)
                    loading = true
                }
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                showToast(requireContext(), R.string.permission_read_needed)
                requireActivity().runOnUiThread{
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        return granted
    }

    override fun onStart() {
        super.onStart()
        binding.buttonSettings.setOnClickListener {
            if (checkPermissions()) {
                safeNav(
                        this, R.id.fragmentTitle,
                        FragmentTitleDirections.actionFragmentTitleToFragmentSettings()
                )
            }
        }
        binding.buttonSongs.setOnClickListener {
            if (checkPermissions()) {
                safeNav(
                        this, R.id.fragmentTitle,
                        FragmentTitleDirections.actionFragmentTitleToFragmentSongs()
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}