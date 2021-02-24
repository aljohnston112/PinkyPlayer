package com.fourthfinger.pinkyplayer.settings

import android.app.Application
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val FILE_SAVE = "settings"
private const val SAVE_FILE_VERIFICATION_NUMBER = 8479145830949658990L

@HiltViewModel
class SettingsViewModel @Inject constructor(
        app: Application,
        savedStateHandle: SavedStateHandle,
        private val settingsRepo: SettingsRepo
) : AndroidViewModel(app) {

    val settings = settingsRepo.settings

    private val _isLoaded: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isLoaded get() = _isLoaded as LiveData<Boolean>

    init {
        _isLoaded.postValue(false)
    }

    fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoaded.postValue(false)
            settingsRepo.load(getApplication(), FILE_SAVE, SAVE_FILE_VERIFICATION_NUMBER)
            _isLoaded.postValue(true)
        }
    }

    fun save(settings: Settings) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepo.save(
                    settings,
                    getApplication<Application>().applicationContext,
                    FILE_SAVE,
                    SAVE_FILE_VERIFICATION_NUMBER,
            )
        }
    }

}