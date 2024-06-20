package io.fourth_finger.pinky_player

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FragmentSettings : PreferenceFragmentCompat() {

    private val viewModel: FragmentSettingsViewModel by viewModels()

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(
            R.xml.preferences,
            rootKey
        )
        findPreference<SeekBarPreference>(
            resources.getString(R.string.probability_down_key)
        )?.setOnPreferenceChangeListener { _, newValue ->
            viewModel.probabilityDownChanged(
                requireContext(),
                newValue as Int
            )
            true
        }

        findPreference<SwitchPreference>(
            resources.getString(R.string.respect_audio_focus_key)
        )?.setOnPreferenceChangeListener { _, newValue ->
            viewModel.respectAudioFocusChanged(
                requireContext(),
                newValue as Boolean
            )
            true
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            findPreference<SeekBarPreference>(
                resources.getString(R.string.probability_down_key)
            )?.value = viewModel.probabilityDown.first()
            findPreference<SwitchPreference>(
                resources.getString(R.string.respect_audio_focus_key)
            )?.isChecked = viewModel.respectAudioFocus.first()
        }
    }

}