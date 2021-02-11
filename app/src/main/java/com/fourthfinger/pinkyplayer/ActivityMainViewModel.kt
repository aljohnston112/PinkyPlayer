package com.fourthfinger.pinkyplayer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.fourthfinger.pinkyplayer.settings.SettingsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityMainViewModel @Inject constructor(
        application: Application,
        savedStateHandle: SavedStateHandle)
    : AndroidViewModel(application) {

}