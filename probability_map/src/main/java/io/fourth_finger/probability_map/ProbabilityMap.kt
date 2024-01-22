package io.fourth_finger.probability_map

import java.math.BigInteger
import java.security.SecureRandom

class ProbabilityMap<T>(elements: List<T>) {

    private val elementProbabilities: MutableMap<T, BigInteger> = mutableMapOf()
    private var totalSum = BigInteger.valueOf(0L)
    private val random = SecureRandom()

    init {
        // The size of elements is used so reduceProbability does not need to multiply all elements
        val n = elements.size.toLong()
        for (element in elements) {
            elementProbabilities[element] = BigInteger.valueOf(n)
            totalSum = totalSum.plus(BigInteger.valueOf(n))
        }
    }

    fun sample(): T {
        val randomNumber = BigInteger(
            totalSum.bitLength(), random
        ).mod(
            totalSum
        )

        var cumulativeSum = BigInteger.ZERO
        var pickedElement: T? = null
        var found = false
        var i = 0
        val keySet = elementProbabilities.keys.toList()
        while (!found && i < keySet.size) {
            val element = keySet[i]
            val probability = elementProbabilities[element]!!
            cumulativeSum += probability
            if (randomNumber < cumulativeSum) {
                pickedElement = element
                found = true
            }
            i++
        }
        return pickedElement!!
    }

    fun reduceProbability(element: T, divisor: Long) {
        require(divisor > 0) { "Divisor must be greater than 0." }

        val bigDivisor = BigInteger.valueOf(divisor)
        val currentProbability = elementProbabilities[element]!!
        if (currentProbability == BigInteger.ONE) {
            for (key in elementProbabilities.keys) {
                if (key != element) {
                    val oldProbability = elementProbabilities[key]!!
                    val newProbability = oldProbability.multiply(bigDivisor)
                    elementProbabilities[key] = newProbability
                    totalSum = totalSum.subtract(oldProbability).add(newProbability)
                }
            }
        } else {
            val newProbability = (currentProbability / BigInteger.valueOf(divisor))
                .coerceAtLeast(BigInteger.ONE)
            totalSum = totalSum.subtract(currentProbability).add(newProbability)
            elementProbabilities[element] = newProbability
        }
    }


    fun addElement(element: T) {
        require(!elementProbabilities.containsKey(element)) {
            "Element already exists in the ProbabilityMap."
        }
        val probability = BigInteger.valueOf(
            (elementProbabilities.size + 1).toLong()
        )
        elementProbabilities[element] = probability
        totalSum = totalSum.add(probability)
    }

    fun removeElement(element: T) {
        require(elementProbabilities.containsKey(element)) {
            "Element does not exist in the ProbabilityMap."
        }
        val removedProbability = elementProbabilities.remove(element)!!
        totalSum = totalSum.subtract(removedProbability)
    }

}