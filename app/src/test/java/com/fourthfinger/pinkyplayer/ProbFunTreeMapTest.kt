package com.fourthfinger.pinkyplayer

import com.fourthfinger.pinkyplayer.playlists.ProbFun.ProbFunTreeMap
import com.fourthfinger.pinkyplayer.playlists.ProbFun.ProbFunLinkedMap
import org.junit.Assert.fail
import org.junit.Test
import kotlin.random.Random

private const val roundingError = 0.001
private const val TOLERANCE = 0.95
private const val rolls = 100000

// TODO scale up to 10000 goods or bads
class ProbFunTreeMapMapTest {

    @Test
    fun testClearProbs() {
        val choices = setOf(0, 1)
        val maxPercent = 0.5
        val pf: ProbFunTreeMap<Int> = ProbFunTreeMap(choices, maxPercent)
        pf.add(2, 0.5)
        pf.resetProbabilities()
        for (key in pf.getKeys()) {
            assert((pf.getProbability(key) < ((1.0 / 3.0) + roundingError)) ||
                    (pf.getProbability(key) > ((1.0 / 3.0) - roundingError)))
        }
    }

    @Test
    fun testConstructor() {
        val choices = emptySet<Int>()
        val maxPercent = 0.5
        try {
            ProbFunTreeMap(choices, maxPercent)
            fail()
        } catch (e: IllegalArgumentException) { }
        val choices1 = setOf(0, 1)
        try {
            ProbFunTreeMap(choices1, 0.0)
            fail()
        } catch (e: IllegalArgumentException) {
        }
        try {
            ProbFunTreeMap(choices1, 1.0)
            fail()
        } catch (e: IllegalArgumentException) {
        }
        val pf = ProbFunTreeMap(choices1, maxPercent)
        pf.getKeys().contains(0)
        pf.getKeys().contains(1)
        pf.maxPercent = maxPercent
    }

    @Test
    fun testNext() {
        val random = Random.Default
        val choices = setOf(0, 1)
        val maxPercent = 0.5
        val pf = ProbFunTreeMap(choices, maxPercent)
        val map = mutableMapOf((0 to 0), (1 to 0))
        for (i in 0..rolls) {
            if (pf.next(random) == 0) map[0] = (map[0]!! + 1) else map[1] = (map[1]!! + 1)
        }
        assert(map[0]!! >= (TOLERANCE * rolls / 2.0).toInt())
        assert(map[1]!! >= (TOLERANCE * rolls / 2.0).toInt())

    }

    @Test
    fun testBad() {
        val random = Random.Default
        val choices = setOf(0, 1)
        val maxPercent = 0.5
        val percentBad = 0.5
        val pf = ProbFunTreeMap(choices, maxPercent)
        pf.bad(1, percentBad)
        val map = mutableMapOf((0 to 0), (1 to 0))
        for (i in 0..rolls) {
            if (pf.next(random) == 0) map[0] = (map[0]!! + 1) else map[1] = (map[1]!! + 1)
        }
        val percent0 = ((1.0 / choices.size) * percentBad) + (1.0 / choices.size)
        val percent1 = 1.0 - percent0
        assert(map[0]!! >= (TOLERANCE * rolls * percent0).toInt())
        assert(map[1]!! >= (TOLERANCE * rolls * percent1).toInt())
    }

    @Test
    fun testBadByDebug() {
        val choices = setOf(0, 1, 2, 3, 4, 5, 6)
        val maxPercent = 0.5
        val percentBad = 0.5
        val pf = ProbFunTreeMap(choices, maxPercent)
        var percent1 = (1.0 / choices.size)
        for(i in 0..100000) {
            if(pf.bad(1, percentBad) != -1.0) {
                percent1 -= (percent1 * percentBad)
            }
        }
        print("Done")
    }

    @Test
    fun testGoodByDebug() {
        val choices = setOf(0, 1, 2, 3, 4, 5, 6)
        val maxPercent = 1.0-(0.0000000000000005*7.0)
        val percentGood = 0.5
        val pf = ProbFunTreeMap(choices, maxPercent)
        var percent1 = (1.0 / choices.size)
        for(i in 0..100000) {
            if(pf.good(1, percentGood) != -1.0) {
                if (percent1 > 0.5) {
                    percent1 += (1.0 - percent1) * percentGood
                } else {
                    percent1 += percent1 * percentGood
                }

                percent1 += (percent1 * percentGood)
            }
        }
        print("Done")
    }

    @Test
    fun testGood() {
        val random = Random.Default
        val choices = setOf(0, 1)
        val maxPercent = 0.9
        val percentGood = 0.5
        val pf = ProbFunTreeMap(choices, maxPercent)
        pf.good(0, percentGood)
        val map = mutableMapOf((0 to 0), (1 to 0))
        for (i in 0..rolls) {
            if (pf.next(random) == 0) map[0] = (map[0]!! + 1) else map[1] = (map[1]!! + 1)
        }
        val percent0 = ((1.0 / choices.size) * percentGood) + (1.0 / choices.size)
        val percent1 = 1.0 - percent0
        assert(map[0]!! >= (TOLERANCE * rolls * percent0).toInt())
        assert(map[1]!! >= (TOLERANCE * rolls * percent1).toInt())
        pf.good(0, percentGood)
        map[0] = 0
        map[1] = 0
        for (i in 0..rolls) {
            if (pf.next(random) == 0) map[0] = (map[0]!! + 1) else map[1] = (map[1]!! + 1)
        }
        val percent0a = ((1.0 - percent0) * percentGood) + percent0
        val percent1a = 1.0 - percent0a
        assert(map[0]!! >= (TOLERANCE * rolls * percent0a).toInt())
        assert(map[1]!! >= (TOLERANCE * rolls * percent1a).toInt())
    }

    @Test
    fun testClone() {
        val choices = setOf(0, 1)
        val maxPercent = 0.5
        val pf = ProbFunTreeMap(choices, maxPercent)
        val pf0 = pf.clone()
        pf0.getKeys().contains(0)
        pf0.getKeys().contains(1)
        pf0.maxPercent = maxPercent
        pf.bad(1, 0.5)
        val pf1 = pf.clone()
        pf1.getKeys().contains(0)
        pf1.getKeys().contains(1)
        pf1.maxPercent = maxPercent
        assert((pf.getProbability(0) < ((3.0 / 4.0) + roundingError)) ||
                (pf.getProbability(0) > ((3.0 / 4.0) - roundingError)))
        assert((pf.getProbability(1) < ((1.0 / 4.0) + roundingError)) ||
                (pf.getProbability(1) > ((1.0 / 4.0) - roundingError)))
    }

    @Test
    fun testSize() {
        val choices = setOf(0, 1)
        val maxPercent = 0.5
        val pf = ProbFunTreeMap(choices, maxPercent)
        assert(pf.size() == 2)
    }

    @Test
    fun testRemove() {
        val choices = setOf(0, 1, 2)
        val maxPercent = 0.5
        val pf = ProbFunTreeMap(choices, maxPercent)
        pf.remove(1)
        assert(!pf.contains(1))
        assert((pf.getProbability(0) < (0.5 + roundingError)) ||
                (pf.getProbability(0) > (0.5 - roundingError)))
        assert((pf.getProbability(2) < (0.5 + roundingError)) ||
                (pf.getProbability(2) > (0.5 - roundingError)))
    }

    @Test
    fun testAdd() {
        val choices = setOf(0, 1)
        val maxPercent = 0.5
        val pf = ProbFunTreeMap(choices, maxPercent)
        pf.add(2)
        assert(pf.contains(2))
        assert((pf.getProbability(0) < ((1 / 3) + roundingError)) ||
                (pf.getProbability(0) > ((1 / 3) - roundingError)))
        assert((pf.getProbability(1) < ((1 / 3) + roundingError)) ||
                (pf.getProbability(1) > ((1 / 3) - roundingError)))
        assert((pf.getProbability(2) < ((1 / 3) + roundingError)) ||
                (pf.getProbability(2) > ((1 / 3) - roundingError)))
    }

    @Test
    fun testAddWithPercent() {
        val choices = setOf(0, 1)
        val maxPercent = 0.5
        val percent = 0.5
        val pf = ProbFunTreeMap(choices, maxPercent)
        pf.add(2, percent)
        assert(pf.contains(2))
        assert((pf.getProbability(0) < ((1 / 4) + roundingError)) ||
                (pf.getProbability(0) > ((1 / 4) - roundingError)))
        assert((pf.getProbability(1) < ((1 / 4) + roundingError)) ||
                (pf.getProbability(1) > ((1 / 4) - roundingError)))
        assert((pf.getProbability(2) < ((1 / 2) + roundingError)) ||
                (pf.getProbability(2) > ((1 / 2) - roundingError)))
    }

    @Test
    fun testSwap() {
        val choices = setOf(0, 1, 2)
        val maxPercent = 0.5
        val pf = ProbFunLinkedMap(choices, maxPercent)
        var keys = pf.iterator()
        assert(keys.next().key == 0)
        assert(keys.next().key == 1)
        assert(keys.next().key == 2)
        pf.swapTwoPositions(2, 1)
        keys = pf.iterator()
        assert(keys.next().key == 0)
        assert(keys.next().key == 2)
        assert(keys.next().key == 1)
        pf.swapTwoPositions(0, 2)
        keys = pf.iterator()
        assert(keys.next().key == 1)
        assert(keys.next().key == 2)
        assert(keys.next().key == 0)
    }

    @Test
    fun testSwitch() {
        val choices = setOf(0, 1, 2)
        val maxPercent = 0.5
        val pf = ProbFunLinkedMap(choices, maxPercent)
        var keys = pf.getKeys()
        assert(keys[0] == 0)
        assert(keys[1] == 1)
        assert(keys[2] == 2)
        pf.switchOnesPosition(2, 1)
        keys = pf.getKeys()
        assert(keys[0] == 0)
        assert(keys[1] == 2)
        assert(keys[2] == 1)
        pf.switchOnesPosition(0, 2)
        keys = pf.getKeys()
        assert(keys[0] == 2)
        assert(keys[1] == 1)
        assert(keys[2] == 0)
        pf.switchOnesPosition(2, 0)
        keys = pf.getKeys()
        assert(keys[0] == 0)
        assert(keys[1] == 2)
        assert(keys[2] == 1)
    }

    @Test
    fun testLowerProbs(){
        val random = Random.Default
        val choices = setOf(0, 1)
        val maxPercent = 0.9
        val percentGood = 0.5
        val pf = ProbFunTreeMap(choices, maxPercent)
        pf.good(0, percentGood)
        val map = mutableMapOf((0 to 0), (1 to 0))
        pf.good(0, percentGood)
        pf.lowerProbs(0.75)
        for (i in 0..rolls) {
            if (pf.next(random) == 0) map[0] = (map[0]!! + 1) else map[1] = (map[1]!! + 1)
        }
        assert(map[0]!! >= (TOLERANCE * rolls * 0.75).toInt())
        assert(map[1]!! >= (TOLERANCE * rolls * 0.25).toInt())
    }

}