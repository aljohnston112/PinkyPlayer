package com.fourthfinger.pinkyplayer.matchers

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fourthfinger.pinkyplayer.playlists.RecyclerViewAdapterSongs
import com.fourthfinger.pinkyplayer.songs.Song
import org.hamcrest.BaseMatcher
import org.hamcrest.Description

class RecyclerViewAdapterSongMatcher(private val pos: Int, private val song: Song) : BaseMatcher<View>() {

    override fun describeTo(description: Description?) {
        description?.appendText("has song: ")
        description?.appendText(song.toString())
    }

    override fun matches(item: Any): Boolean {
        if (item is RecyclerView && item.adapter is RecyclerViewAdapterSongs) {
                val vh = item.findViewHolderForAdapterPosition(pos) as RecyclerViewAdapterSongs.ViewHolder
                return vh.song == song
        }
        return false
    }

}