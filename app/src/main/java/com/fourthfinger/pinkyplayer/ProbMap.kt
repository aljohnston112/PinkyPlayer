package com.fourthfinger.pinkyplayer

import kotlin.random.Random

class ProbMap<T> {

    // A map of T to the probability of it being returned if selected
    private val probMap = mutableMapOf<T, Double>()

    fun put(t: T, prob: Double) {
        probMap[t] = prob
    }

    fun outcome(t: T, random: Random): Boolean {
        probMap[t]?.let {
            val randomChoice = random.nextDouble()
            return randomChoice <= it
        }
        return false
    }

    fun good(t: T, percent: Double): Boolean {
        require((percent <= 1.0 || percent >= 0.0)) {
            "percent passed to good() is not between 0.0 and 1.0 (inclusive)"
        }
        val prob = probMap[t] ?: return false
        var globalGood = true
        for (d in probMap.values) {
            if (prob >= d) {
                globalGood = false
                break
            }
        }
        val add = prob * percent
        probMap[t] = (prob + add).coerceAtMost(1.0)
        return globalGood
    }

    fun bad(t: T, percent: Double): Boolean {
        require((percent <= 1.0 || percent >= 0.0)) {
            "percent passed to bad() is not between 0.0 and 1.0 (inclusive)"
        }
        val prob = probMap[t] ?: return false
        var globalBad = true
        for (d in probMap.values) {
            if (prob <= d) {
                globalBad = false
                break
            }
        }
        val sub = prob * percent
        probMap[t] = prob - sub
        return globalBad
    }

    fun resetProbabilities() {
        for (t in probMap.keys) {
            probMap[t] = 1.0
        }
    }

}