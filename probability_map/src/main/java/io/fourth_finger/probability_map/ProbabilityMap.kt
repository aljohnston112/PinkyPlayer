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
        if(elements.isEmpty()){
            throw IllegalArgumentException("There must be at least one element in the map")
        }
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
     * Scales the probability of an element.
     *
     * @param element The element to scale the probability of.
     * @param numerator The numerator of the probability to scale the element by.
     * @param denominator The denominator of the probability to scale the element by.
     */
    fun scaleProbability(
        element: T,
        numerator: Int,
        denominator: Int
    ) {
        require(numerator > 0) { "numerator must be greater than 0." }
        require(denominator > 0) { "denominator must be greater than 0." }

        val elementMultiplier = BigInteger.valueOf(numerator.toLong() * (getElements().size - 1))
        val nonElementMultiplier = BigInteger.valueOf((denominator.toLong() * (getElements().size)) - numerator)
        for (key in elementProbabilities.keys) {
            val oldProbability = elementProbabilities[key]!!

            if (key != element) {
                val newProbability = oldProbability.multiply(nonElementMultiplier)
                elementProbabilities[key] = newProbability
                totalSum = totalSum.subtract(oldProbability).add(newProbability)
            } else {
                val newProbability = oldProbability.multiply(elementMultiplier)
                elementProbabilities[key] = newProbability
                totalSum = totalSum.subtract(oldProbability).add(newProbability)
            }
        }
        maxProbability *= elementMultiplier

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

    fun contains(t: T): Boolean {
        return elementProbabilities.containsKey(t)
    }

    fun getElements(): Set<T> {
        return elementProbabilities.keys
    }

}