package com.fourthfinger.pinkyplayer

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.fourthfinger.pinkyplayer.playlists.RecyclerViewAdapterSongs
import com.fourthfinger.pinkyplayer.songs.Song
import org.hamcrest.BaseMatcher
import org.hamcrest.Description

class RecyclerViewAdapterSongsMatcher(private val songs: List<Song>): BaseMatcher<View>() {

        override fun describeTo(description: Description?) {
            description?.appendText("has list of songs: ")
            description?.appendText(songs.toString())
        }

        override fun matches(item: Any?): Boolean {
            if (item is RecyclerView) {
                if (item.adapter is RecyclerViewAdapterSongs) {
                    val recyclerViewAdapterSongs: RecyclerViewAdapterSongs = item.adapter as RecyclerViewAdapterSongs
                    var same = true
                    for ((i, song) in recyclerViewAdapterSongs.songs.listIterator().withIndex()) {
                        if (this.songs[i] != (song)) {
                            same = false
                            throw IllegalStateException("$i")
                        }
                    }
                    return same && recyclerViewAdapterSongs.songs.size == this.songs.size
                }
            }
            return false
        }

}