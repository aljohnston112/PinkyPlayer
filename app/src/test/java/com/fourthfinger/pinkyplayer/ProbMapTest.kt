package com.fourthfinger.pinkyplayer

import com.fourthfinger.pinkyplayer.songs.ProbMap
import junit.framework.TestCase
import org.junit.Test
import kotlin.math.roundToInt
import kotlin.random.Random

private const val TOLERANCE = 0.99

class ProbMapTest : TestCase() {

    @Test
    fun testPutAndOutcome() {
        val rolls = 100000
        val random = Random.Default
        val map = mutableMapOf(true to 0, false to 0)
        val probMap = ProbMap<Int>()
        probMap.put(0, 0.5)
        for(i in 0..rolls) {
            if(probMap.outcome(0, random)){
                map[true] = map[true]!!+1
            } else {
                map[false] = map[false]!!+1
            }
        }
        assert(map[true]!! >= ((rolls/2)* TOLERANCE).roundToInt())
    }

    fun testGood() {
        val prob = 0.5
        val percent = 0.5
        val rolls = 100000
        val random = Random.Default
        val map = mutableMapOf(true to 0, false to 0)
        val probMap = ProbMap<Int>()
        probMap.put(0, prob)
        probMap.good(0, percent)
        for(i in 0..rolls) {
            if(probMap.outcome(0, random)){
                map[true] = map[true]!!+1
            } else {
                map[false] = map[false]!!+1
            }
        }
        assert(map[true]!! >= ((rolls*(prob+(prob*percent)))* TOLERANCE).roundToInt())
    }

    fun testBad() {
        val prob = 0.5
        val percent = 0.5
        val rolls = 100000
        val random = Random.Default
        val map = mutableMapOf(true to 0, false to 0)
        val probMap = ProbMap<Int>()
        probMap.put(0, prob)
        probMap.bad(0, percent)
        for(i in 0..rolls) {
            if(probMap.outcome(0, random)){
                map[true] = map[true]!!+1
            } else {
                map[false] = map[false]!!+1
            }
        }
        assert(map[true]!! >= ((rolls*(prob-(prob*percent)))* TOLERANCE).roundToInt())
    }

    fun testResetProbabilities() {
        val rolls = 100000
        val random = Random.Default
        val map = mutableMapOf(true to 0, false to 0)
        val probMap = ProbMap<Int>()
        probMap.put(0, 0.5)
        probMap.resetProbabilities()
        for(i in 0..rolls) {
            if(probMap.outcome(0, random)){
                map[true] = map[true]!!+1
            } else {
                map[false] = map[false]!!+1
            }
        }
        assert(map[true]!! == rolls+1)
    }
}