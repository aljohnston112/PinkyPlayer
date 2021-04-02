package com.fourthfinger.pinkyplayer

import android.content.Context
import android.graphics.*
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.core.content.res.ResourcesCompat
import com.fourthfinger.pinkyplayer.songs.AudioUri
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class BitmapUtil {

    companion object {

        fun getSongBitmap(context: Context, audioUri: AudioUri?, songArtDimen: Int): Bitmap? {
            var bitmap = audioUri?.uri?.let { getThumbnail(it, songArtDimen, songArtDimen, context) }
            if (bitmap == null) {
                val drawable = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.music_note_black_48dp,
                        null
                )
                if (drawable != null) {
                    drawable.setBounds(0, 0, songArtDimen, songArtDimen)
                    bitmap = Bitmap.createBitmap(songArtDimen, songArtDimen, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    val paint = Paint()
                    paint.color = ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null)
                    canvas.drawRect(0f, 0f, songArtDimen.toFloat(), songArtDimen.toFloat(), paint)
                    drawable.draw(canvas)
                }
            }
            return bitmap
        }

        private fun getThumbnail(uri: Uri, width: Int, height: Int, context: Context): Bitmap? {
            var bitmap: Bitmap? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    bitmap = context.contentResolver.loadThumbnail(
                            uri, Size(width, height), null
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                val mmr = MediaMetadataRetriever()
                try {
                    mmr.setDataSource(context.contentResolver.openFileDescriptor(
                            uri, "r")?.fileDescriptor
                    )
                    var inputStream: InputStream? = null
                    if (mmr.embeddedPicture != null) {
                        inputStream = ByteArrayInputStream(mmr.embeddedPicture)
                    }
                    mmr.release()
                    bitmap = BitmapFactory.decodeStream(inputStream)
                    if (bitmap != null) {
                        return getResizedBitmap(bitmap, width, height)
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
            return bitmap
        }

        private fun getResizedBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
            val width: Int = bitmap.width
            val height: Int = bitmap.height
            val scaleWidth = newWidth.toFloat() / width.toFloat()
            val scaleHeight = newHeight.toFloat() / height.toFloat()
            val matrix = Matrix()
            matrix.postScale(scaleWidth, scaleHeight)
            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        }

    }

}