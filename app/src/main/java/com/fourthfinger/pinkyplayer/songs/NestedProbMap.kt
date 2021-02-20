package com.fourthfinger.pinkyplayer.songs

import java.io.Serializable
import java.util.*
import kotlin.random.Random

class NestedProbMap : Serializable {

    // The ProbFun used to determine if a song should play during a specific hour
    private val probabilityFunctionHours: ProbMap<Int> = ProbMap()

    fun getProbForHour(hour: Int): Double {
        return probabilityFunctionHours.getProb(hour)
    }

    // The ProbFun used to determine if a song should play during a specific day
    private val probabilityFunctionDays: ProbMap<Int> = ProbMap()

    fun getProbForDay(day: Int): Double {
        return probabilityFunctionDays.getProb(day-1)
    }

    // The ProbFun used to determine if a song should play during a specific month
    private val probabilityFunctionMonths: ProbMap<Int> = ProbMap()

    fun getProbForMonth(month: Int): Double {
        return probabilityFunctionMonths.getProb(month)
    }

    init {
        for (i in 0..23) {
            probabilityFunctionHours.put(i, 1.0)
        }
        for (i in 0..6) {
            probabilityFunctionDays.put(i, 1.0)
        }
        for (i in 0..11) {
            probabilityFunctionMonths.put(i, 1.0)
        }
    }

    fun outcome(random: Random, date: Calendar): Boolean {
        return probabilityFunctionHours.outcome(date[Calendar.HOUR_OF_DAY], random) &&
                probabilityFunctionDays.outcome(date[Calendar.DAY_OF_WEEK] - 1, random) &&
                probabilityFunctionMonths.outcome(date[Calendar.MONTH], random)
    }

    fun good(percent: Double, date: Calendar): Boolean {
        return probabilityFunctionHours.good(date[Calendar.HOUR_OF_DAY], percent) &&
                probabilityFunctionDays.good(date[Calendar.DAY_OF_WEEK] - 1, percent) &&
                probabilityFunctionMonths.good(date[Calendar.MONTH], percent)
    }

    fun bad(percent: Double, date: Calendar): Boolean {
        return probabilityFunctionHours.bad(date[Calendar.HOUR_OF_DAY], percent) &&
                probabilityFunctionDays.bad(date[Calendar.DAY_OF_WEEK] - 1, percent) &&
                probabilityFunctionMonths.bad(date[Calendar.MONTH], percent)
    }

    fun resetProbabilities() {
        probabilityFunctionHours.resetProbabilities()
        probabilityFunctionDays.resetProbabilities()
        probabilityFunctionMonths.resetProbabilities()
    }

}