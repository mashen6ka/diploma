package time

import kotlin.random.Random

class UniformDurationGenerator(
    private val min: Time,
    private val max: Time
) : DurationGenerator {
    private val random = Random(System.currentTimeMillis())

    override fun generate(): Time {
        return random.nextInt(min, max + 1)
    }
}
