package com.fourthfinger.pinkyplayer.matchers

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.core.view.drawToBitmap
import com.fourthfinger.pinkyplayer.BitmapUtil
import com.fourthfinger.pinkyplayer.songs.AudioUri
import org.hamcrest.BaseMatcher
import org.hamcrest.Description

class AudioUriBitmapMatcher(private val audioUri: AudioUri): BaseMatcher<View>() {

    // TODO recycle bitmaps

    override fun describeTo(description: Description?) {
        description?.appendText("has same bitmap as the audioUri with the song: ")
        description?.appendText(audioUri.title)
    }

    override fun matches(item: Any?): Boolean {
        if(item is ImageView){
            val bitmap = item.drawToBitmap()
            val h = bitmap.height
            val w = bitmap.width
            val bm = BitmapUtil.getSongBitmap(item.context, audioUri, h)
            return if(bm != null && !bm.sameAs(bitmap)){
                val bm1 = BitmapUtil.getSongBitmap(item.context, audioUri, w)
                bm1 != null && bm1.sameAs(bitmap)
            } else {
                true
            }
        }
        return false
    }


}