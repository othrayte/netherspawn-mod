package net.othrayte.netherspawn.extentions

object SequenceEx {
    // Get a random element from the sequence weighted by the given function
    fun <T> Sequence<T>.weightedRandom(weight: (T) -> Int): T? {
        val totalWeight = sumOf { weight(it) }
        var currentWeight = (0..totalWeight).random()
        return find {
            currentWeight -= weight(it)
            currentWeight <= 0
        }
    }
}