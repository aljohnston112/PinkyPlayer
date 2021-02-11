package com.fourthfinger.pinkyplayer.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.ToastUtil.Companion.showToast
import com.fourthfinger.pinkyplayer.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class FragmentSettings : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeSettings()
        setUpFab()
    }

    private fun setUpFab() {
        binding.fabFragmentSettings.setText(R.string.save)
        binding.fabFragmentSettings.icon = ResourcesCompat.getDrawable(
                resources, R.drawable.ic_check_black_24dp, context?.theme)
        setFabOnClickListener()
    }

    private fun setFabOnClickListener() {
        binding.fabFragmentSettings.setOnClickListener {
            val nSongs = getNSongs()
            if (nSongs == -1) {
                return@setOnClickListener
            }
            val percentChangeUp = getPercentChangeUp()
            if (percentChangeUp == -1) {
                return@setOnClickListener
            }
            val percentChangeDown = getPercentChangeDown()
            if (percentChangeDown == -1) {
                return@setOnClickListener
            }
            viewModel.settings.value?.let { it1 ->
                Settings(
                        1.0/nSongs,
                        percentChangeUp/100.0,
                        percentChangeDown/100.0,
                        it1.lowerProb)
            }?.let { it2 -> viewModel.save(it2)
                findNavController().popBackStack()
            }
        }
    }

    private fun getNSongs(): Int {
        val editTextNSongs = binding.editTextNSongs
        var nSongs = -1
        try {
            nSongs = editTextNSongs.text.toString().toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        if (nSongs < 1) {
            context?.let { showToast(it, R.string.max_percent_error) }
            nSongs = -1
        }
        return nSongs
    }

    private fun getPercentChangeUp(): Int {
        val editTextPercentChangeUp = binding.editTextPercentChangeUp
        var percentChangeUp = -1
        try {
            percentChangeUp = editTextPercentChangeUp.text.toString().toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        if (percentChangeUp < 1 || percentChangeUp > 100) {
            context?.let { showToast(it, R.string.percent_change_error) }
            percentChangeUp = -1
        }
        return percentChangeUp
    }

    private fun getPercentChangeDown(): Int {
        val editTextPercentChangeDown = binding.editTextPercentChangeDown
        var percentChangeDown = -1
        try {
            percentChangeDown = editTextPercentChangeDown.text.toString().toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        if (percentChangeDown < 1 || percentChangeDown > 100) {
            context?.let { showToast(it, R.string.percent_change_error) }
            percentChangeDown = -1
        }
        return percentChangeDown
    }

    private fun observeSettings() {
        viewModel.settings.observe(viewLifecycleOwner, { newSettings ->
            binding.editTextNSongs.setText((1.0 / newSettings.maxPercent).roundToInt().toString())
            binding.editTextPercentChangeUp.setText((newSettings.percentChangeUp*100.0).roundToInt().toString())
            binding.editTextPercentChangeDown.setText((newSettings.percentChangeDown*100.0).roundToInt().toString())
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}