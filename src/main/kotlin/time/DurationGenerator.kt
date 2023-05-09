package time

typealias Time = Int

interface DurationGenerator {
    fun generate(): Time
}
