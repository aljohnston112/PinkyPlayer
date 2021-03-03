package com.fourthfinger.pinkyplayer.matchers

import android.view.View
import com.fourthfinger.pinkyplayer.songs.AudioUri
import com.fourthfinger.pinkyplayer.songs.Song
import org.hamcrest.Matcher

class EspressoTestMatcher {

    companion object {

        fun withDrawable(resourceId: Int): Matcher<View> {
            return DrawableMatcher(resourceId)
        }

        fun withSongs(songs: List<Song>): Matcher<View> {
            return RecyclerViewAdapterSongsMatcher(songs)
        }

        fun withSongAtPosition(pos: Int, song: Song): Matcher<View> {
            return RecyclerViewAdapterSongMatcher(pos, song)
        }

        fun withHeightOfAtLeast(height: Int): Matcher<View> {
            return ViewHeightMatcher(height)
        }

        fun withSameBitmapAs(audioUri: AudioUri): Matcher<View> {
            return AudioUriBitmapMatcher(audioUri)
        }

    }

}