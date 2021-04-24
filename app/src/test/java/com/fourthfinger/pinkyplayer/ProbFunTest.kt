package com.fourthfinger.pinkyplayer

import com.fourthfinger.pinkyplayer.songs.Song
import org.junit.Test
import kotlin.math.roundToInt
import kotlin.random.Random

class ProbFunTest {

    @Test
    fun getMaxPercent() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        assert(pf.maxPercent() == maxPercent)
    }

    @Test
    fun setMaxPercent() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        val maxPercent1 = 0.85
        pf.setMaxPercent(maxPercent1)
        assert(pf.maxPercent() == maxPercent1)
    }

    @Test
    fun add() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        val song2  = Song(2L, "C")
        pf.add(song2)
        assert(pf.contains(song2))
    }

    @Test
    fun testAdd() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        val song2  = Song(2L, "C")
        val p = 0.5
        pf.add(song2, p)
        assert(pf.contains(song2) && pf.getProbability(song2) == p)
    }

    @Test
    fun remove() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val song2  = Song(2L, "C")
        val songs = setOf(song0, song1, song2)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        pf.remove(song1)
        assert(!pf.contains(song1))
    }

    @Test
    fun prune() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        val song2  = Song(2L, "C")
        val song3  = Song(3L, "D")
        val p = 0.01
        pf.add(song2, p)
        pf.add(song3, p)
        pf.prune()
        assert(!pf.contains(song2) || !pf.contains(song3))
    }

    @Test
    fun testPrune() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        val song2  = Song(2L, "C")
        val song3  = Song(3L, "D")
        val p = 0.01
        pf.add(song2, p)
        pf.add(song3, p)
        pf.prune(0.02)
        assert(!pf.contains(song2) && !pf.contains(song3))
    }

    @Test
    fun swapTwoPositions() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunLinkedMap(songs, maxPercent)
        val song2  = Song(2L, "C")
        val song3  = Song(3L, "D")
        val p = 0.01
        pf.add(song2, p)
        pf.add(song3, p)
        pf.swapTwoPositions(0, 3)
        val keys = pf.getKeys().toList()
        assert(keys[0] == song3 && keys[3] == song0)
    }

    @Test
    fun switchOnesPosition() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunLinkedMap(songs, maxPercent)
        val song2  = Song(2L, "C")
        val song3  = Song(3L, "D")
        val p = 0.01
        pf.add(song2, p)
        pf.add(song3, p)
        pf.switchOnesPosition(0, 3)
        val keys = pf.getKeys().toList()
        assert(keys[0] == song1 && keys[3] == song0)
    }

    @Test
    fun contains() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        assert(pf.contains(song0) && pf.contains(song1))
    }

    @Test
    fun getKeys() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        val keys = pf.getKeys()
        assert(keys.contains(song0) && keys.contains(song1))
    }

    @Test
    fun size() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        assert(pf.size() == 2)
    }

    @Test
    fun good() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        pf.good(song1, 0.5)
        assert(pf.getProbability(song1) == 0.5+(0.5*0.5))
    }

    @Test
    fun bad() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        pf.bad(song1, 0.5)
        assert(pf.getProbability(song1) == 0.5-(0.5*0.5))
    }

    @Test
    fun lowerProbs() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        val song2  = Song(2L, "C")
        val song3  = Song(3L, "D")
        val p = 0.01
        pf.add(song2, p)
        pf.add(song3, p)
        val low = 0.3
        pf.lowerProbs(low)
        assert(pf.getProbability(song1) == low)
    }

    @Test
    fun resetProbabilities() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        pf.bad(song0, 0.99)
        pf.resetProbabilities()
        assert(pf.getProbability(song0) == 0.5 && pf.getProbability(song1) == 0.5)
    }

    @Test
    fun getProbability() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        val song2  = Song(2L, "C")
        val p = 0.01
        pf.add(song2, p)
        assert(pf.getProbability(song2) == p)
    }

    @Test
    operator fun next() {
        val tolerance = 0.99
        val rolls = 100000
        val random = Random.Default
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        val map = mutableMapOf(song0 to 0, song1 to 0)
        for(i in 0..rolls) {
            val s = pf.next(random)
            if(s == song0){
                map[song0] = map[song0]?.plus(1)!!
            } else {
                map[song1] = map[song1]?.plus(1)!!
            }
        }
        assert(map[song0]!! >= ((rolls/2)* tolerance).roundToInt())
    }

    @Test
    fun testHashCode() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        val pf2 = ProbFun.ProbFunTreeMap(songs, maxPercent)
        assert(pf.hashCode() == pf.hashCode() && pf.hashCode() != pf2.hashCode())
    }

    @Test
    fun testEquals() {
        val song0 = Song(0L, "A")
        val song1  = Song(1L, "B")
        val songs = setOf(song0, song1)
        val maxPercent = 0.75
        val pf = ProbFun.ProbFunTreeMap(songs, maxPercent)
        val pf2 = ProbFun.ProbFunTreeMap(songs, maxPercent)
        assert(pf == pf && pf != pf2)
    }

}