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

    private lateinit var nestedProbMap: NestedProbMap
    private val goodHour = 12
    private val calendarGoodDay = 1
    private var goodDay = 1
    private val percent = 0.5

    private val hourProb = percent.pow(7.0)
    private val dayProb = percent
    private val badMonthProb = percent

    @Test fun testProbs(){
        bad()
        good()
        resetProbabilities()
    }

    private fun bad() {
        nestedProbMap = NestedProbMap()
        val calendarBuilder = Calendar.Builder()
        calendarBuilder.setDate(0, Calendar.JANUARY, calendarGoodDay + 1)
        goodDay = calendarBuilder.build().get(Calendar.DAY_OF_WEEK) - 2
        badAllButGoodHour(calendarBuilder)
        for (hour in hours) {
            if (hour != goodHour) {
                assert(nestedProbMap.getProbForHour(hour) == 0.5)
            } else {
                assert(nestedProbMap.getProbForHour(hour) == 1.0)
            }
        }
        calendarBuilder.setTimeOfDay(goodHour, 0, 0)
        nestedProbMap.bad(percent, calendarBuilder.build())
        for (hour in hours) {
            assert(nestedProbMap.getProbForHour(hour) == 0.5)
        }
        calendarBuilder.setTimeOfDay(goodHour, 0, 0)
        for (day in days) {
            if ((day != goodDay) && (day != (goodDay + 1))) {
                calendarBuilder.setDate(0, Calendar.JANUARY, (day + goodDay) % 7)
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
        calendarBuilder.setDate(0, Calendar.JANUARY, calendarGoodDay)
        calendarBuilder.setTimeOfDay(goodHour, 0, 0)
        nestedProbMap.bad(percent, calendarBuilder.build())
        for (hour in hours) {
            assert(nestedProbMap.getProbForHour(hour) == hourProb)
        }
        for (day in days) {
            assert(nestedProbMap.getProbForDay(day) == dayProb)
        }
        for (month in months) {
            if (month != Calendar.JANUARY) {
                assert(nestedProbMap.getProbForMonth(month) == 1.0)
            } else {
                assert(nestedProbMap.getProbForMonth(month) == badMonthProb)
            }
        }
        val random = Random.Default
        val rolls = 100000
        val map = mutableMapOf(true to 0, false to 0)
        val badMonthTarget = (rolls * (dayProb * hourProb * badMonthProb * TOLERANCE)).roundToInt()
        val goodMonthTarget = (rolls * (dayProb * hourProb * TOLERANCE)).roundToInt()
        calendarBuilder.setDate(0, Calendar.JANUARY, days[0])
        calendarBuilder.setTimeOfDay(hours[0], 0, 0)
        for (roll in 0..rolls) {
            if (nestedProbMap.outcome(random, calendarBuilder.build())) {
                map[true] = map[true]!! + 1
            } else {
                map[false] = map[false]!! + 1
            }
        }
        assert(map[true]!! >= badMonthTarget)
        calendarBuilder.setDate(0, Calendar.FEBRUARY, days[0])
        for (roll in 0..rolls) {
            if (nestedProbMap.outcome(random, calendarBuilder.build())) {
                map[true] = map[true]!! + 1
            } else {
                map[false] = map[false]!! + 1
            }
        }
        assert(map[true]!! >= goodMonthTarget)

    }

    private fun badAllButGoodHour(calendarBuilder: Calendar.Builder) {
        for (hour in hours) {
            if (hour != goodHour) {
                calendarBuilder.setTimeOfDay(hour, 0, 0)
                nestedProbMap.bad(percent, calendarBuilder.build())
            }
        }
    }

    private fun good() {
        val calendarBuilder = Calendar.Builder()
        calendarBuilder.setDate(0, Calendar.JANUARY, calendarGoodDay + 1)
        goodAllButGoodHour(calendarBuilder)
        var goodHourProbs = hourProb + (percent * hourProb)
        for (hour in hours) {
            if (hour != goodHour) {
                assert(nestedProbMap.getProbForHour(hour) == goodHourProbs)
            } else {
                assert(nestedProbMap.getProbForHour(hour) == hourProb)
            }
        }
        calendarBuilder.setTimeOfDay(goodHour, 0, 0)
        nestedProbMap.good(percent, calendarBuilder.build())
        assert(nestedProbMap.getProbForHour(goodHour) == goodHourProbs)
        val goodDayProbs = dayProb + (percent * dayProb)
        assert(nestedProbMap.getProbForDay(goodDay + 2) == goodDayProbs)
        for (day in days) {
            if ((day != goodDay) && (day != (goodDay + 1))) {
                calendarBuilder.setDate(0, Calendar.JANUARY, (day + goodDay) % 7)
                goodAllButGoodHour(calendarBuilder)
                calendarBuilder.setTimeOfDay(goodHour, 0, 0)
                nestedProbMap.good(percent, calendarBuilder.build())
                goodHourProbs += (percent * goodHourProbs)
            }
        }
        for (hour in hours) {
                assert(nestedProbMap.getProbForHour(hour) == goodHourProbs)
        }
        for(day in days){
            if(day != goodDay + 1){
                assert(nestedProbMap.getProbForDay(day) == goodDayProbs)
            } else {
                assert(nestedProbMap.getProbForDay(day) == dayProb)
            }
        }
        calendarBuilder.setDate(0, Calendar.JANUARY, calendarGoodDay)
        goodAllButGoodHour(calendarBuilder)
        calendarBuilder.setTimeOfDay(goodHour, 0, 0)
        nestedProbMap.good(percent, calendarBuilder.build())
        goodHourProbs += (percent * goodHourProbs)
        for (hour in hours) {
            assert(nestedProbMap.getProbForHour(hour) == goodHourProbs)
        }
        for(day in days){
                assert(nestedProbMap.getProbForDay(day) == goodDayProbs)
        }
        val goodMonthProb = badMonthProb + (badMonthProb*percent)
        for(month in months){
            if(month != Calendar.JANUARY) {
                assert(nestedProbMap.getProbForMonth(month) == 1.0)
            } else {
                assert(nestedProbMap.getProbForMonth(month) == goodMonthProb)
            }
        }
    }

    private fun goodAllButGoodHour(calendarBuilder: Calendar.Builder) {
        for (hour in hours) {
            if (hour != goodHour) {
                calendarBuilder.setTimeOfDay(hour, 0, 0)
                nestedProbMap.good(percent, calendarBuilder.build())
            }
        }
    }

    private fun resetProbabilities() {
        nestedProbMap.resetProbabilities()
        for(month in months){
            assert(nestedProbMap.getProbForMonth(month) == 1.0)
        }
        for(day in days){
            assert(nestedProbMap.getProbForDay(day) == 1.0)
        }
        for(hour in hours){
            assert(nestedProbMap.getProbForHour(hour) == 1.0)
        }
    }
}