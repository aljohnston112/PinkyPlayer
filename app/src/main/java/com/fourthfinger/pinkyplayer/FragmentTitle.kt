package com.fourthfinger.pinkyplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fourthfinger.pinkyplayer.databinding.FragmentTitleBinding

class FragmentTitle : Fragment() {

    private var _binding: FragmentTitleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // container: ViewGroup? means container may be null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentTitleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.buttonSettings.setOnClickListener {
            val dest = findNavController().currentDestination
            if(dest != null){
                if(dest.id == R.id.fragmentTitle) {
                    findNavController().navigate(
                            FragmentTitleDirections.actionFragmentTitleToFragmentSettings())
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // For VMs
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}