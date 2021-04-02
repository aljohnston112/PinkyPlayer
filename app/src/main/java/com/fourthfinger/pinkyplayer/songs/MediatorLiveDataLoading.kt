package com.fourthfinger.pinkyplayer.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class MediatorLiveDataLoading {

    private var songsLoadedValue: Boolean? = null
    private var settingsLoadedValue: Boolean? = null
    private var playlistsLoadedValue: Boolean? = null

    fun isLoaded(
            songsLoaded: LiveData<Boolean>,
            settingsLoaded: LiveData<Boolean>,
            playlistsLoaded: LiveData<Boolean>
    ): LiveData<Boolean> = MediatorLiveData<Boolean>().also { mediator ->
        this.songsLoadedValue = songsLoaded.value
        this.settingsLoadedValue = settingsLoaded.value
        this.playlistsLoadedValue = playlistsLoaded.value
        if(songsLoadedValue == null || settingsLoadedValue == null || playlistsLoadedValue == null) {
            mediator.value = false
        } else {
            mediator.value = (songsLoadedValue!! && settingsLoadedValue!! && playlistsLoadedValue!!)
        }
        mediator.addSource(songsLoaded) {
            this.songsLoadedValue = it
            if(songsLoadedValue == null || settingsLoadedValue == null || playlistsLoadedValue == null) {
                mediator.value = false
            } else {
                mediator.value = (songsLoadedValue!! && settingsLoadedValue!! && playlistsLoadedValue!!)
            }
        }
        mediator.addSource(settingsLoaded) {
            this.settingsLoadedValue = it
            if(songsLoadedValue == null || settingsLoadedValue == null || playlistsLoadedValue == null) {
                mediator.value = false
            } else {
                mediator.value = (songsLoadedValue!! && settingsLoadedValue!! && playlistsLoadedValue!!)
            }
        }
        mediator.addSource(playlistsLoaded) {
            this.playlistsLoadedValue = it
            if(songsLoadedValue == null || settingsLoadedValue == null || playlistsLoadedValue == null) {
                mediator.value = false
            } else {
                mediator.value = (songsLoadedValue!! && settingsLoadedValue!! && playlistsLoadedValue!!)
            }
        }
    }

}