package time

import kotlin.random.Random;

typealias Time = Int

interface DurationGenerator {
    fun generate(): Time
}

class UniformDurationGenerator(private val min: Int, private val max: Int) : DurationGenerator {
    private val random = Random(System.currentTimeMillis())

    override fun generate(): Time {
        return random.nextInt(min, max + 1)
    }
}