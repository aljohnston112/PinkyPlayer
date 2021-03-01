package com.fourthfinger.pinkyplayer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat
import androidx.test.core.app.ApplicationProvider
import com.fourthfinger.pinkyplayer.BitmapUtil.Companion.getSongBitmap
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class BitmapUtilTest: ActivityMainBaseTest() {

    @Test fun testGetSongBitmap(){
        val songArtDimen = 10
        val context: Context = ApplicationProvider.getApplicationContext()
        val bitmapTest = getSongBitmap(context, null, songArtDimen)
        val drawable = ResourcesCompat.getDrawable(context.resources,
                R.drawable.music_note_black_48dp, null
        )
        var bitmap: Bitmap? = null
        if (drawable != null) {
            drawable.setBounds(0, 0, songArtDimen, songArtDimen)
            bitmap = Bitmap.createBitmap(songArtDimen, songArtDimen, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            paint.color = ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null)
            canvas.drawRect(0f, 0f, songArtDimen.toFloat(), songArtDimen.toFloat(), paint)
            drawable.draw(canvas)
        }
        assert(bitmapTest != null)
        assert(bitmap != null)
        assert(bitmap?.sameAs(bitmapTest) ?: false)
    }

}