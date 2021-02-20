package com.fourthfinger.pinkyplayer.settings

import android.app.Application
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val FILE_SAVE = "settings"
private const val FILE_SAVE2 = "settings2"
private const val FILE_SAVE3 = "settings3"
private val SAVE_FILES = listOf(FILE_SAVE, FILE_SAVE2, FILE_SAVE3)
private const val SAVE_FILE_VERIFICATION_NUMBER = 8479145830949658990L


@HiltViewModel
class SettingsViewModel @Inject constructor(
        app: Application,
        savedStateHandle: SavedStateHandle,
        private val settingsRepo: SettingsRepo
) : AndroidViewModel(app) {

    private val _settings: MutableLiveData<Settings> by lazy {
        MutableLiveData<Settings>()
    }

    val settings = _settings as LiveData<Settings>

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
            _settings.postValue(
                    settingsRepo.load(
                            getApplication(),
                            SAVE_FILES,
                            SAVE_FILE_VERIFICATION_NUMBER,
                    ))
            _isLoaded.postValue(true)
        }
    }

    fun save(settings: Settings) {
        this._settings.postValue(settings)
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepo.save(
                    settings,
                    getApplication<Application>().applicationContext,
                    SAVE_FILES,
                    SAVE_FILE_VERIFICATION_NUMBER,
            )
        }
    }

}