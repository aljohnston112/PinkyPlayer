package com.fourthfinger.pinkyplayer.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class MediatorLiveDataLoading {

    var songLoadedValue: Boolean? = null
    var settingsLoadedValue: Boolean? = null

    fun isLoaded(
            songsLoaded: LiveData<Boolean>, settingsLoaded: LiveData<Boolean>
    ): LiveData<Boolean> = MediatorLiveData<Boolean>().also { mediator ->
        songLoadedValue = songsLoaded.value
        settingsLoadedValue = settingsLoaded.value
        val songsLoadedValue = this.songLoadedValue
        val settingsLoadedValue = this.settingsLoadedValue
        if(songsLoadedValue == null || settingsLoadedValue == null) {
            mediator.value = false
        } else {
            mediator.value = (songsLoadedValue && settingsLoadedValue)
        }
        mediator.addSource(songsLoaded) {
            this.songLoadedValue = it
            val songLoadedValue = this.songLoadedValue
            val settingLoadedValue = this.settingsLoadedValue
            if(settingLoadedValue == null){
                mediator.value = false
            } else {
                if(songLoadedValue == null){
                    mediator.value = false
                } else {
                    mediator.value = (songLoadedValue) && (settingLoadedValue)
                }
            }
        }
        mediator.addSource(settingsLoaded) {
            this.settingsLoadedValue = it
            val songLoadedValue = this.songLoadedValue
            val settingLoadedValue = this.settingsLoadedValue
            if(songLoadedValue == null){
                mediator.value = false
            } else {
                if(settingLoadedValue == null){
                    mediator.value = false
                } else {
                    mediator.value = (songLoadedValue && settingLoadedValue)
                }
            }
        }
    }

}