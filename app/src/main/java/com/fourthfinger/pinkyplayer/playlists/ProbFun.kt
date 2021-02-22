package com.fourthfinger.pinkyplayer.playlists

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.random.Random
import kotlin.reflect.KProperty

const val MIN_VALUE = 0.0000000000000005

// TODO fix max percent like the constructor
sealed class ProbFun<T>(
        choices: Set<T>, var maxPercent: Double, comparable: Boolean
) : Serializable, Cloneable {

    class ProbFunLinkedMap<T>(
            choices: Set<T>, maxPercent: Double
    ) : ProbFun<T>(choices, maxPercent, false) {

        constructor(probFun: ProbFunLinkedMap<T>) : this(
                probFun.probabilityMap.keys, probFun.maxPercent
        ) {
            for ((key, value) in probFun.probabilityMap) {
                probabilityMap[key] = value
            }
            roundingError = probFun.roundingError
        }

        public override fun clone(): ProbFunLinkedMap<T> {
            return ProbFunLinkedMap(this)
        }

    }

    class ProbFunTreeMap<T : Comparable<T>>(
            choices: Set<T>, maxPercent: Double
    ) : ProbFun<T>(choices, maxPercent, true) {

        constructor(probFun: ProbFunTreeMap<T>) : this(
                probFun.probabilityMap.keys, probFun.maxPercent
        ) {
            for ((key, value) in probFun.probabilityMap) {
                probabilityMap[key] = value
            }
            roundingError = probFun.roundingError
        }

        public override fun clone(): ProbFunTreeMap<T> {
            return ProbFunTreeMap(this)
        }
    }

    // The set of elements to be picked from, mapped to the probabilities of getting picked
    protected var probabilityMap: MutableMap<T, Double>

    protected var roundingError = 0.0

    protected val id by lazy { hashCode() }

    init {
        Objects.requireNonNull(choices)
        require(choices.isNotEmpty()) {
            "Must have at least 1 element in the choices passed to the ProbFunTree constructor\n"
        }
        require(choices.size < 2000000000000000) {
            "ProbFun will not work with a size greater than 2,000,000,000,000,000"
        }
        require((maxPercent >= (1.0 / choices.size) && maxPercent <= (1.0 - (choices.size*MIN_VALUE)))) {
            "maxPercent passed into the ProbFunTree constructor must be above 0 and under 1.0" +
                    "value was $maxPercent"
        }
        probabilityMap = if (comparable) TreeMap() else LinkedHashMap()
        for (choice in choices) {
            probabilityMap[choice] = 1.0 / choices.size
        }
        fixProbSum()
    }

    /**
     * Adds element to this ProbFunTree, making the probability equal to 1.0/n
     * where n is the number of elements contained in this ProbFunTree,
     * and appends elements as a child ProbFunTree, where elements are the choices.
     *
     * @param element as the element to add to this ProbFunTree.
     * @throws NullPointerException if element is null.
     */
    fun add(element: T) {
        Objects.requireNonNull(element)
        val probability = 1.0 / probabilityMap.size
        if (!probabilityMap.containsKey(element)) {
            probabilityMap[element] = probability
            scaleProbs()
        }
    }

    /**
     * Adds an element to this ProbFunTree with the specified probability.
     * If the element exists in this ProbFunTree then it's probability will be overwritten with percent.
     *
     * @param element as the element to add to this ProbFunTree.
     * @param percent between 0 and 1 exclusive, as the chance of this ProbFunTree returning element.
     * @throws NullPointerException     if element is null.
     * @throws IllegalArgumentException if percent is not between 0.0 and 1.0 (exclusive).
     */
    fun add(element: T, percent: Double) {
        Objects.requireNonNull(element)
        require((percent >= ((size()+1)*MIN_VALUE)) && percent <= (1.0 - ((size()+1)*MIN_VALUE))) {
            "percent passed to add() is not between 0.0 and 1.0 (exclusive)"
        }
        val scale = 1.0 - percent
        val probabilities = probabilityMap.entries
        for (e in probabilities) {
            e.setValue(e.value * scale)
        }
        probabilityMap[element] = percent
        scaleProbs()
    }

    /**
     * Removes an element from this ProbFunTree unless there is only one element.
     *
     * @param element as the element to remove from this ProbFunTree.
     * @return True if this ProbFunTree's parent contained the element and it was removed, else false.
     * @throws NullPointerException if element is null.
     */
    fun remove(element: T): Boolean {
        Objects.requireNonNull(element)
        if (size() == 1) {
            return false
        }
        if (probabilityMap.remove(element) == null) {
            return false
        }
        scaleProbs()
        return true
    }

    fun swapTwoPositions(oldPosition: Int, newPosition: Int) {
        val oldMap = probabilityMap
        val keys = ArrayList(oldMap.keys)
        Collections.swap(keys, oldPosition, newPosition)
        val swappedMap = LinkedHashMap<T, Double>()
        for (key in keys) {
            swappedMap[key] = oldMap[key] ?: error("Problem swapping elements")
        }
        probabilityMap = swappedMap
    }

    fun switchOnesPosition(oldPosition: Int, newPosition: Int) {
        val oldMap = probabilityMap
        val keys = ArrayList(oldMap.keys)
        val d = keys[oldPosition]
        keys.removeAt(oldPosition)
        keys.add(newPosition, d)
        val switchedMap = LinkedHashMap<T, Double>()
        for (key in keys) {
            switchedMap[key] = oldMap[key]!!
        }
        probabilityMap = switchedMap
    }

    operator fun contains(t: T): Boolean {
        return probabilityMap.containsKey(t)
    }

    fun getKeys() = ArrayList(probabilityMap.keys)

    fun iterator() = probabilityMap.iterator()

    /**
     * Returns the number of elements in this ProbFunTree.
     *
     * @return the number of elements in this ProbFunTree.
     */
    fun size(): Int {
        return probabilityMap.size
    }

    /**
     * Adjust the probability to make element more likely to be returned when fun() is called from this ProbFunTree.
     *
     * @param element as the element to make appear more often
     * @param percent as the percentage between 0 and 1 (exclusive),
     * of the probability of getting element to add to the probability.
     * @param scale   as whether or not to scale the percent down
     * to avoid hitting a ceiling for the probability.
     * @return the adjusted probability.
     * @throws NullPointerException     if element is null.
     * @throws IllegalArgumentException if the percent isn't between 0 and 1 exclusive.
     */
    fun good(element: T, percent: Double): Double {
        Objects.requireNonNull(element)
        require((percent < 1.0 && percent > 0.0)) {
            "percent passed to good() is not between 0.0 and 1.0 (exclusive)"
        }
        if ((!contains(element)) || (probabilityMap[element]!! >= maxPercent)) return -1.0
        val oldProb = probabilityMap[element] ?: return -1.0
        var add = probToAddForGood(oldProb, percent)
        var newPercent = percent
        while (oldProb + add >= maxPercent - roundingError) {
            newPercent *= percent
            add = probToAddForGood(oldProb, newPercent)
        }
        val goodProbability = oldProb + add
        if(goodProbability >= (1.0-(size()*MIN_VALUE))) return -1.0
        probabilityMap[element] = goodProbability
        val leftover = 1.0 - goodProbability
        val sumOfLeftovers = probSum() - goodProbability
        val leftoverScale = leftover / sumOfLeftovers
        for (e in probabilityMap.entries) {
            e.setValue(e.value * leftoverScale)
        }
        probabilityMap[element] = goodProbability
        fixProbSum()
        return probabilityMap[element]!!
    }

    private fun probToAddForGood(oldProb: Double, percent: Double): Double {
        return if (oldProb > 0.5) {
            (1.0 - oldProb) * percent
        } else {
            oldProb * percent
        }
    }

    /**
     * Adjust the probability to make element less likely to be returned when fun() is called from this ProbFunTree.
     *
     * @param element as the element to make appear less often
     * @param percent as the percentage between 0 and 1 (exclusive),
     * of the probability of getting element to subtract from the probability.
     * @return the adjusted probability or -1 if this ProbFunTree didn't contain the element.
     * @throws NullPointerException     if element is null.
     * @throws IllegalArgumentException if the percent isn't between 0 and 1 exclusive.
     */
    fun bad(element: T, percent: Double): Double {
        // TODO Fix how maxPercent can be lower than the max prob here
        Objects.requireNonNull(element)
        require((percent < 1.0 && percent > 0.0)) {
            "percent passed to good() is not between 0.0 and 1.0 (exclusive)"
        }
        val oldProb = probabilityMap[element] ?: return -1.0
        val sub = oldProb * percent
        if (oldProb - sub <= roundingError) return oldProb
        val badProbability = oldProb - sub
        if(badProbability <= (size()*MIN_VALUE)) return -1.0
        probabilityMap[element] = badProbability
        val leftover = 1.0 - badProbability
        val sumOfLeftovers = probSum() - badProbability
        val leftoverScale = leftover / sumOfLeftovers
        for (e in probabilityMap.entries) {
            e.setValue(e.value * leftoverScale)
        }
        probabilityMap[element] = badProbability
        fixProbSum()
        return probabilityMap[element]!!
    }

    /**
     * Lowers the probabilities so there is about at most a low chance of getting any element from this ProbFunTree.
     *
     * @param low as the lowest chance of an object being returned when fun() is called.
     */
    fun lowerProbs(low: Double) {
        require((low >= (size()*MIN_VALUE)) && low <= (1.0 - (size()*MIN_VALUE)))
        val probs: Collection<T> = probabilityMap.keys
        for (t in probs) {
            if (probabilityMap[t]!! > low) {
                probabilityMap[t] = low
            }
        }
        var maxSum = 0.0
        var otherSum = 0.0
        for (t in probs) {
            if (probabilityMap[t] == low) {
                maxSum += probabilityMap[t]!!
            } else {
                otherSum += probabilityMap[t]!!
            }
        }
        val leftovers = 1.0 - maxSum
        val scale = leftovers / otherSum
        for (t in probs) {
            if (probabilityMap[t] != low) {
                probabilityMap[t] = probabilityMap[t]!! * scale
            }
        }
        scaleProbs()
    }

    /**
     * Sets the probabilities to there being an equal chance of getting any element from this ProbFunTree.
     */
    fun resetProbabilities() {
        for (e in probabilityMap.entries) {
            e.setValue(1.0)
        }
        scaleProbs()
    }

    /**
     * Fixes rounding error in the probabilities by adding up the probabilities
     * and changing the first probability so all probabilities add up to 1.0.
     * TODO This is a terrible solution to the rounding error
     */
    private fun fixProbSum() {
        roundingError = 1.0 - probSum()
        var firstProb = probabilityMap.entries.iterator().next()
        while (firstProb.value * 2.0 < roundingError) {
            var p: Double
            for (e in probabilityMap.entries) {
                p = e.value + (roundingError / probabilityMap.size.toDouble())
                e.setValue(p)
            }
            firstProb = probabilityMap.entries.iterator().next()
        }
        firstProb.setValue(firstProb.value + roundingError)
    }

    /**
     * @return the sum of all the probabilities in order to fix rounding error.
     */
    private fun probSum(): Double {
        val probabilities = probabilityMap.values
        var sum = 0.0
        for (p in probabilities) {
            sum += p
        }
        return sum
    }

    /**
     * Scales the probabilities so they add up to 1.0.
     */
    private fun scaleProbs() {
        val scale = 1.0 / probSum()
        val probabilities = probabilityMap.entries
        for (e in probabilities) {
            e.setValue(e.value * scale)
        }
        fixProbSum()
    }

    fun getProbability(t: T): Double {
        return probabilityMap[t]!!
    }

    /**
     * Returns a randomly picked element from this ProbFunTree, based on the previously returned elements.
     *
     * @return a randomly picked element from this ProbFunTree.
     * Any changes in the element will be reflected in this ProbFunTree.
     */
    fun next(random: Random): T {
        val randomChoice = random.nextDouble()
        val entries = probabilityMap.entries.iterator()
        var element: T? = null
        var sumOfProbabilities = 0.0
        var e: Map.Entry<T, Double>
        while (randomChoice > sumOfProbabilities) {
            e = entries.next()
            element = e.key
            sumOfProbabilities += e.value
        }
        return element!!
    }

    abstract override fun clone(): ProbFun<T>

    override fun toString(): String {
        val id = hashCode()
        val sb = StringBuilder()
        sb.append("PF ")
        sb.append(id)
        sb.append(": [")
        for ((key, value) in probabilityMap) {
            sb.append("[")
            sb.append(key)
            sb.append(" = ")
            sb.append(value * 100.0)
            sb.append("%],")
        }
        sb.append("\n")
        return sb.toString()
    }

    override fun hashCode() = System.identityHashCode(this)

    override fun equals(other: Any?): Boolean {
        if (other is ProbFun<*>) {
            return hashCode() == other.hashCode()
        }
        return false
    }

    operator fun getValue(randomPlaylist: RandomPlaylist, property: KProperty<*>): Double {
        if(property.name == "maxPercent"){
            return maxPercent
        }
        return -1.0
    }

    companion object {

        private const val serialVersionUID = -6556634307811294014L

    }
}