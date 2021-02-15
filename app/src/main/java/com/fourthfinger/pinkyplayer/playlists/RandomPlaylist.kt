package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import com.fourthfinger.pinkyplayer.songs.AudioUri
import com.fourthfinger.pinkyplayer.songs.Song
import java.io.Serializable
import kotlin.random.Random

class RandomPlaylist(name: String, music: List<Song>, maxPercent: Double,
                     comparable: Boolean) : Serializable {

    private val playlistArray: MutableList<Long> = ArrayList()

    @Transient
    private var playlistIterator: ListIterator<Long>?
    var name: String

    // The ProbFun that randomly picks the media to play
    private var probabilityFunction: ProbFun<Song>

    val songs: List<Any>
        get() = probabilityFunction.getKeys()

    val songIDs: List<Long>
        get() = playlistArray

    fun add(song: Song) {
        probabilityFunction.add(song)
        playlistArray.add(song.id)
    }

    fun add(song: Song, probability: Double) {
        probabilityFunction.add(song, probability)
        playlistArray.add(song.id)
    }

    fun remove(song: Song) {
        probabilityFunction.remove(song)
        playlistArray.remove(song.id)
    }

    operator fun contains(song: Song): Boolean {
        return probabilityFunction.contains(song)
    }

    operator fun contains(songID: Long): Boolean {
        return playlistArray.contains(songID)
    }

    fun maxPercent(): Double {
        return probabilityFunction.maxPercent
    }

    fun good(context: Context, song: Song, percent: Double) {
        if (AudioUri.getAudioUri(context, song.id)?.good(percent) == true) {
            probabilityFunction.good(song, percent)
        }
    }

    fun bad(context: Context, song: Song, percent: Double) {
        if (AudioUri.getAudioUri(context, song.id)?.bad(percent) == true) {
            probabilityFunction.bad(song, percent)
        }
    }

    fun getProbability(song: Song): Double {
        return probabilityFunction.getProbability(song)
    }

    fun resetProbabilities(context: Context) {
        for (song in probabilityFunction.getKeys()) {
            AudioUri.getAudioUri(context, song.id)?.resetProbabilities()
        }
        probabilityFunction.resetProbabilities()
    }

    fun lowerProbabilities(context: Context, lowerProb: Double) {
        probabilityFunction.lowerProbs(lowerProb)
    }

    fun size(): Int {
        return probabilityFunction.size()
    }

    fun swapTwoSongPositions(oldPosition: Int, newPosition: Int) {
        probabilityFunction.swapTwoPositions(oldPosition, newPosition)
    }

    fun switchOneSongsPosition(oldPosition: Int, newPosition: Int) {
        probabilityFunction.switchOnesPosition(oldPosition, newPosition)
    }

    fun next(context: Context, random: Random, looping: Boolean, shuffling: Boolean): AudioUri? {
        if (playlistIterator == null) {
            playlistIterator = playlistArray.listIterator()
        }
        return if (!shuffling) {
            if (looping && !playlistIterator!!.hasNext()) {
                playlistIterator = playlistArray.listIterator()
            }
            if (playlistIterator!!.hasNext()) {
                AudioUri.getAudioUri(context, playlistIterator!!.next())
            } else {
                null
            }
        } else {
            next(context, random)
        }
    }

    fun next(context: Context, random: Random): AudioUri {
        var song: Song
        var audioUri: AudioUri? = null
        var next = false
        while (!next) {
            song = probabilityFunction.next(random)
            audioUri = AudioUri.getAudioUri(context, song.id)
            next = audioUri?.shouldPlay(random) == true
        }
        return audioUri!!
    }

    fun previous(context: Context, random: Random, looping: Boolean, shuffling: Boolean): AudioUri? {
        if (playlistIterator == null) {
            playlistIterator = playlistArray.listIterator()
        }
        if (looping && !playlistIterator!!.hasPrevious()) {
            playlistIterator = playlistArray.listIterator(playlistArray.size - 1)
        }
        return if (playlistIterator!!.hasPrevious()) {
            AudioUri.getAudioUri(context, playlistIterator!!.previous())
        } else {
            if (shuffling) {
                next(context, random)
            } else {
                null
            }
        }
    }


/*
public void goToFront() {
    playlistIterator = null;
    playlistIterator = playlistArray.listIterator();
}

public void goToBack() {
    playlistIterator = null;
    playlistIterator = playlistArray.listIterator(playlistArray.size());
}

 */

    fun setIndexTo(songID: Long) {
        val i = playlistArray.indexOf(songID)
        if(i != -1) {
            playlistIterator = playlistArray.listIterator(i + 1)
        }
    }

    fun globalBad(song: Song, percentChangeDown: Double) {
        probabilityFunction.bad(song, percentChangeDown)
    }

    fun globalGood(song: Song, percentChangeUp: Double) {
        probabilityFunction.good(song, percentChangeUp)
    }

    companion object {
        private const val serialVersionUID = 2323326608918863420L
    }

    /**
     * Creates a random playlist.
     *
     * @param name            The name of this RandomPlaylist.
     * @param music           The List of AudioURIs to add to this playlist.
     * @param maxPercent      The max percentage that any AudioUri can have
     * of being returned when fun() is called.
     * @throws IllegalArgumentException if there is not at least one AudioURI in music.
     * @throws IllegalArgumentException if folder is not a directory.
     */
    init {
        require(music.isNotEmpty()) { "List music must contain at least one AudioURI" }
        val files: Set<Song> = LinkedHashSet<Song>(music)
        probabilityFunction = if (comparable) {
            ProbFun.ProbFunTreeMap(files, maxPercent)
        } else {
            ProbFun.ProbFunLinkedMap(files, maxPercent)
        }
        this.name = name
        for (song in music) {
            playlistArray.add(song.id)
        }
        playlistIterator = playlistArray.listIterator()
    }
}