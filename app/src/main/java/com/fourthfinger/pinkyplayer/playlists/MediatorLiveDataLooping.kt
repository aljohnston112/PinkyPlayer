package com.fourthfinger.pinkyplayer.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class MediatorLiveDataLooping {

    private var loopingValue: Boolean? = null
    private var loopingOneValue: Boolean? = null

    fun isLooping(
            looping: LiveData<Boolean>, loopingOne: LiveData<Boolean>
    ): LiveData<Boolean> = MediatorLiveData<Boolean>().also { mediator ->
        this.loopingValue = looping.value
        this.loopingOneValue = loopingOne.value
        if(loopingValue == null || loopingOneValue == null) {
            mediator.value = false
        } else {
            mediator.value = (!loopingValue!! && !loopingOneValue!!)
        }
        mediator.addSource(looping) {
            this.loopingValue = it
            if(loopingValue == null || loopingOneValue == null) {
                mediator.value = false
            } else {
                mediator.value = (!loopingValue!! && !loopingOneValue!!)
            }
        }
        mediator.addSource(loopingOne) {
            this.loopingOneValue = it
            if(loopingValue == null || loopingOneValue == null) {
                mediator.value = false
            } else {
                mediator.value = (!loopingValue!! && !loopingOneValue!!)
            }
        }
    }

}