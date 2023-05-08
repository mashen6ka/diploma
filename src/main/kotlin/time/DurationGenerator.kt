package time

import kotlin.random.Random;

typealias Time = Int

interface DurationGenerator {
    fun generate(): Time
}
