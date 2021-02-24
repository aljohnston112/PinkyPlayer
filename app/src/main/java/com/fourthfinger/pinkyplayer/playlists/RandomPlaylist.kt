package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import com.fourthfinger.pinkyplayer.songs.AudioUri
import com.fourthfinger.pinkyplayer.songs.Song
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

/**
 * A random playlist.
 *
 * @param name            The name of this RandomPlaylist.
 * @param music           The List of AudioURIs to add to this playlist.
 * @param maxPercent      The max percentage that any AudioUri can have
 * of being returned when fun() is called.
 * @throws IllegalArgumentException if there is not at least one AudioURI in music.
 */
class RandomPlaylist(val name: String, music: List<Song>, maxPercent: Double,
                     val comparable: Boolean) : Serializable {

    // The ProbFun that randomly picks the media to play
    private var probabilityFunction: ProbFun<Song>

    fun songs() = probabilityFunction.getKeys()

    fun getMaxPercent(): Double {
        return probabilityFunction.maxPercent
    }

    fun setMaxPercent(maxPercent : Double){
        probabilityFunction.maxPercent = maxPercent
    }

    fun size(): Int {
        return probabilityFunction.size()
    }

    fun getProbability(song: Song): Double {
        return probabilityFunction.getProbability(song)
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

    fun globalBad(song: Song, percentChangeDown: Double) {
        probabilityFunction.bad(song, percentChangeDown)
    }

    fun globalGood(song: Song, percentChangeUp: Double) {
        probabilityFunction.good(song, percentChangeUp)
    }

    fun resetProbabilities(context: Context) {
        for (song in songs()) {
            AudioUri.getAudioUri(context, song.id)?.resetProbabilities()
        }
        probabilityFunction.resetProbabilities()
    }

    fun lowerProbabilities(context: Context, lowerProb: Double) {
        probabilityFunction.lowerProbs(lowerProb)
    }

    fun swapTwoSongPositions(oldPosition: Int, newPosition: Int) {
        probabilityFunction.swapTwoPositions(oldPosition, newPosition)
    }

    fun switchOneSongsPosition(oldPosition: Int, newPosition: Int) {
        probabilityFunction.switchOnesPosition(oldPosition, newPosition)
    }

    private val playlistArray: MutableList<Long> = ArrayList()

    fun songIds(): MutableList<Long> {
        if(comparable) {
            playlistArray.sort()
        }
        return playlistArray
    }

    operator fun contains(song: Song): Boolean {
        return playlistArray.contains(song.id)
    }

    @Transient
    private var playlistIterator: ListIterator<Long>

    fun goToFront(){
        playlistIterator = playlistArray.listIterator()
    }

    fun goToBack() {
        playlistIterator = playlistArray.listIterator(playlistArray.size)
    }

    fun setIndexTo(songID: Long) {
        val i = playlistArray.indexOf(songID)
        if (i != -1) {
            playlistIterator = playlistArray.listIterator(i)
        }
    }

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

    fun next(context: Context, random: Random, looping: Boolean, shuffling: Boolean): AudioUri? {
        return if (!shuffling) {
            if (looping && !playlistIterator.hasNext()) {
                playlistIterator = playlistArray.listIterator()
            }
            if (playlistIterator.hasNext()) {
                AudioUri.getAudioUri(context, playlistIterator.next())
            } else {
                null
            }
        } else {
            next(context, random)
        }
    }

    private fun next(context: Context, random: Random): AudioUri {
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
        if (looping && !playlistIterator.hasPrevious()) {
            playlistIterator = playlistArray.listIterator(playlistArray.size)
        }
        return if (playlistIterator.hasPrevious()) {
            AudioUri.getAudioUri(context, playlistIterator.previous())
        } else {
            if (shuffling) {
                next(context, random)
            } else {
                null
            }
        }
    }

    fun updateSongs(newSongs: List<Song>){
        for(s in newSongs){
            if(!contains(s)){
                add(s)
            }
        }
        for(s in songs()){
            if(!newSongs.contains(s)){
                remove(s)
            }
        }
    }

    init {
        require(music.isNotEmpty()) { "List music must contain at least one AudioURI" }
        val songs: Set<Song> = TreeSet(music)
        probabilityFunction = if (comparable) {
            ProbFun.ProbFunTreeMap(songs, maxPercent)
        } else {
            ProbFun.ProbFunLinkedMap(songs, maxPercent)
        }
        if (comparable) {
            val sortedMusic = music.toSortedSet()
            for (song in sortedMusic) {
                playlistArray.add(song.id)
            }
        } else {
            for (song in music) {
                playlistArray.add(song.id)
            }
        }
        playlistIterator = playlistArray.listIterator()
    }

    override fun equals(other: Any?): Boolean {
        if(other is RandomPlaylist) {
            for (song in songs()) {
                if (!other.contains(song)){
                    return false
                }
            }
            for(song in other.songs()){
                if(!contains(song)){
                    return false
                }
            }
            return (getMaxPercent() == other.getMaxPercent()) && (comparable == other.comparable)
        }
        return false
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + comparable.hashCode()
        result = 31 * result + playlistArray.hashCode()
        return result
    }

    companion object {
        private const val serialVersionUID = 2323326608918863420L
    }

}