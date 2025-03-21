package io.fourth_finger.pinky_player

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.fourth_finger.pinky_player.FragmentTitleDirections
import io.fourth_finger.pinky_player.databinding.FragmentTitleBinding
import io.fourth_finger.pinky_player.ActivityMainViewModel


/**
 * The title [Fragment] of the app.
 */
@AndroidEntryPoint
class FragmentTitle : Fragment() {

    private var _binding: FragmentTitleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ActivityMainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTitleBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up navigation buttons
        viewModel.havePermission.observe(viewLifecycleOwner) {

            // Must guarantee permissions are granted before launching [FragmentMusicList]
            if (it) {
                binding.buttonSongs.setOnClickListener{
                    findNavController().navigate(
                        FragmentTitleDirections.actionFragmentTitleToFragmentMusicList()
                    )
                }
                binding.buttonPlaylists.setOnClickListener {
                    findNavController().navigate(
                        FragmentTitleDirections.actionFragmentTitleToPlaylists()
                    )
                }
            } else {
                binding.buttonSongs.setOnClickListener{
                    viewModel.displayPermissionNeeded(requireActivity())
                }
            }
        }
        binding.buttonSettings.setOnClickListener {
            findNavController().navigate(
                FragmentTitleDirections.actionFragmentTitleToFragmentSettings()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.buttonSongs.setOnClickListener(null)
        _binding = null
    }

}