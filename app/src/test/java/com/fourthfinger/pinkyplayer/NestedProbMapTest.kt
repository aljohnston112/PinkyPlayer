package com.fourthfinger.pinkyplayer

import com.fourthfinger.pinkyplayer.songs.NestedProbMap
import junit.framework.TestCase
import org.junit.Test
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random

private const val TOLERANCE = 0.75

private val hours = List(24) { it }
private val days = List(7) { it + 1 }
private val months = List(12) { it }

class NestedProbMapTest : TestCase() {

    private val nestedProbMap = NestedProbMap()
    private val goodHour = 12
    private val goodDay = 1
    private val percent = 0.5

    @Test
    fun testBad() {
        val calendarBuilder = Calendar.Builder()
        calendarBuilder.setDate(0, Calendar.JANUARY, goodDay + 1)
        badAllButGoodHour(calendarBuilder)
        var rolls = 100000
        val random = Random.Default
        val map = mutableMapOf(true to 0, false to 0)
        for (hour in hours) {
            map[true] = 0
            map[false] = 0
            calendarBuilder.setTimeOfDay(hour, 0, 0)
            for (roll in 0..rolls) {
                if (nestedProbMap.outcome(random, calendarBuilder.build())) {
                    map[true] = map[true]!! + 1
                } else {
                    map[false] = map[false]!! + 1
                }
            }
            if (hour != goodHour) {
                assert(map[true]!! >= ((rolls * percent) * TOLERANCE).roundToInt())
            } else {
                assert(map[true]!! == rolls + 1)
            }
        }
        calendarBuilder.setTimeOfDay(goodHour, 0, 0)
        nestedProbMap.bad(percent, calendarBuilder.build())
        for (hour in hours) {
            map[true] = 0
            map[false] = 0
            calendarBuilder.setTimeOfDay(hour, 0, 0)
            for (roll in 0..rolls) {
                if (nestedProbMap.outcome(random, calendarBuilder.build())) {
                    map[true] = map[true]!! + 1
                } else {
                    map[false] = map[false]!! + 1
                }
            }
            assert(map[true]!! >= (rolls * (percent * percent) * TOLERANCE).roundToInt())
        }
        calendarBuilder.setTimeOfDay(goodHour, 0, 0)
        for (day in days) {
            if ((day != goodDay) && (day != (goodDay + 1))) {
                calendarBuilder.setDate(0, Calendar.JANUARY, day)
                badAllButGoodHour(calendarBuilder)
                calendarBuilder.setTimeOfDay(goodHour, 0, 0)
                nestedProbMap.bad(percent, calendarBuilder.build())
            }
        }
        for (hour in hours) {
            if (hour != goodHour) {
                calendarBuilder.setTimeOfDay(hour, 0, 0)
                nestedProbMap.bad(percent, calendarBuilder.build())
            }
        }
        calendarBuilder.setDate(0, Calendar.JANUARY, goodDay)
        calendarBuilder.setTimeOfDay(goodHour, 0, 0)
        nestedProbMap.bad(percent, calendarBuilder.build())
        val goodHourProb = percent.pow(7.0)
        val goodDayProb = percent
        val badMonthProb = percent
        rolls = 100000
        val badMonthTarget = (rolls * (goodDayProb * goodHourProb * badMonthProb * TOLERANCE)).roundToInt()
        var lowestRolls = Int.MAX_VALUE
        for (day in days) {
            calendarBuilder.setDate(0, Calendar.JANUARY, day)
            for (hour in hours) {
                map[true] = 0
                map[false] = 0
                calendarBuilder.setTimeOfDay(hour, 0, 0)
                for (roll in 0..rolls) {
                    if (nestedProbMap.outcome(random, calendarBuilder.build())) {
                        map[true] = map[true]!! + 1
                    } else {
                        map[false] = map[false]!! + 1
                    }
                }
                if (map[true]!! < lowestRolls) {
                    lowestRolls = map[true]!!
                    println("${map[true]!!} rolls when expecting $badMonthTarget")
                }
                assert(map[true]!! >= badMonthTarget)
            }
        }
    }

    private fun badAllButGoodHour(calendarBuilder: Calendar.Builder) {
        for (hour in hours) {
            if (hour != goodHour) {
                calendarBuilder.setTimeOfDay(hour, 0, 0)
                nestedProbMap.bad(percent, calendarBuilder.build())
            }
        }
    }

    @Test
    fun testGood() {

    }

    fun testResetProbabilities() {}
}