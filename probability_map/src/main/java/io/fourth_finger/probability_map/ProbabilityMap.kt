package io.fourth_finger.probability_map

import java.math.BigInteger
import java.security.SecureRandom

/**
 * A probability distribution.
 * Each element is mapped to a probability and the sum of all probabilities is one.
 *
 * @param elements The elements to add to this distribution.
 *                 Each element will be assigned the same probability.
 */
class ProbabilityMap<T>(elements: List<T>) {

    private val elementProbabilities: MutableMap<T, BigInteger> = mutableMapOf()
    private var totalSum = BigInteger.valueOf(0L)
    private var maxProbability = BigInteger.valueOf(1L)
    private val random = SecureRandom()

    init {
        val n = 1L
        for (element in elements) {
            elementProbabilities[element] = BigInteger.valueOf(n)
            totalSum = totalSum.plus(BigInteger.valueOf(n))
        }
    }

    /**
     * Samples an element from the probability distribution.
     */
    fun sample(): T {
        var randomNumber = BigInteger(
            totalSum.bitLength(), random
        )
        while(randomNumber >= totalSum){
            randomNumber = BigInteger(
                totalSum.bitLength(), random
            )
        }

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

    /**
     * Reduces the probability of an element.
     *
     * @param element The element to reduce the probability of.
     * @param scale A number to scale the probabilities of the other elements by.
     */
    fun reduceProbability(element: T, scale: Long) {
        require(scale > 0) { "Scale must be greater than 0." }

        val bigDivisor = BigInteger.valueOf(scale)
        for (key in elementProbabilities.keys) {
            if (key != element) {
                val oldProbability = elementProbabilities[key]!!
                val newProbability = oldProbability.multiply(bigDivisor)
                elementProbabilities[key] = newProbability
                totalSum = totalSum.subtract(oldProbability).add(newProbability)
            }
        }
        maxProbability *= bigDivisor

    }

    /**
     * Adds an element to this probability distribution.
     * The probability will be set to the max probability of all elements in this distribution.
     *
     * @param element The element to add.
     */
    fun addElement(element: T) {
        require(!elementProbabilities.containsKey(element)) {
            "Element already exists in the ProbabilityMap."
        }
        elementProbabilities[element] = maxProbability
        totalSum = totalSum.add(maxProbability)
    }

    /**
     * Removes an element from the probability distribution.
     * The remaining elements will equally distribute the
     * remaining probability so they add up to one.
     *
     * @param element Removes an element from this probability distribution.
     */
    fun removeElement(element: T) {
        require(elementProbabilities.containsKey(element)) {
            "Element does not exist in the ProbabilityMap."
        }
        val removedProbability = elementProbabilities.remove(element)!!
        totalSum = totalSum.subtract(removedProbability)
    }

}