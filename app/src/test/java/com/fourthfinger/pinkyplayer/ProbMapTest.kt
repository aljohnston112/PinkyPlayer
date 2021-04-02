package com.fourthfinger.pinkyplayer

import com.fourthfinger.pinkyplayer.songs.ProbMap
import org.junit.Test
import kotlin.math.roundToInt
import kotlin.random.Random

private const val TOLERANCE = 0.99

class ProbMapTest {

    @Test
    fun testPutGetAndOutcome() {
        val rolls = 100000
        val random = Random.Default
        val map = mutableMapOf(true to 0, false to 0)
        val probMap = ProbMap<Int>()
        probMap.put(0, 0.5)
        assert(probMap.getProb(0) == 0.5)
        for(i in 0..rolls) {
            if(probMap.outcome(0, random)){
                map[true] = map[true]!!+1
            } else {
                map[false] = map[false]!!+1
            }
        }
        assert(map[true]!! >= ((rolls/2)* TOLERANCE).roundToInt())
    }

    @Test
    fun testGood() {
        val prob = 0.5
        val percent = 0.5
        val probMap = ProbMap<Int>()
        probMap.put(0, prob)
        probMap.good(0, percent)
        val newProb = prob+(prob*percent)
        assert(probMap.getProb(0) == newProb)
        probMap.good(0, percent)
        assert(probMap.getProb(0) == 1.0)
    }

    @Test
    fun testBad() {
        val prob = 0.5
        val percent = 0.5
        val probMap = ProbMap<Int>()
        probMap.put(0, prob)
        probMap.bad(0, percent)
        val newProb = prob-(prob*percent)
        assert(probMap.getProb(0) == newProb)
    }

    @Test
    fun testResetProbabilities() {
        val probMap = ProbMap<Int>()
        probMap.put(0, 0.5)
        probMap.resetProbabilities()
        assert(probMap.getProb(0) == 1.0)
    }

}