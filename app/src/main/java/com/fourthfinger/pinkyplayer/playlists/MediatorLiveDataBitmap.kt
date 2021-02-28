package com.fourthfinger.pinkyplayer.playlists

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class MediatorLiveDataBitmap {

    private var old: Bitmap? = null

    private var bitmapValue: Bitmap? = null

    fun currentSongBitmap(
            currentSongBitmap: LiveData<Bitmap>
    ): LiveData<Bitmap> = MediatorLiveData<Bitmap>().also { mediator ->
        this.bitmapValue = currentSongBitmap.value
            mediator.value = bitmapValue
        mediator.addSource(currentSongBitmap) {
            old?.recycle()
            old = this.bitmapValue
            this.bitmapValue = it
            mediator.value = bitmapValue
        }
    }


}