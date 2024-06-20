package io.fourth_finger.pinky_player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fourth_finger.settings_repository.Settings
import io.fourth_finger.settings_repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,

): ViewModel() {

    val probabilityDown = settingsRepository.probabilityDown

    val respectAudioFocus = settingsRepository.respectAudioFocus

    fun probabilityDownChanged(
        context: Context,
        newValue: Int
    ) {
        viewModelScope.launch {
            settingsRepository.saveSettings(
                context,
                Settings(
                    newValue,
                    respectAudioFocus.first()
                )
            )
        }
    }

    fun respectAudioFocusChanged(
        context: Context,
        respectAudioFocus: Boolean
    ) {
        viewModelScope.launch {
            settingsRepository.saveSettings(
                context,
                Settings(
                    probabilityDown.first(),
                    respectAudioFocus
                )
            )
        }
    }

}