package time

import kotlin.random.Random

class ConstPeakDurationGenerator(
    private val peakDuration: Time,
    private val waitDuration: Time,
    private val peakValueCount: Int
) : DurationGenerator {
    private var count: Int = 0

    override fun generate(): Time {
        if (count < peakValueCount) {
            val nextTime = peakDuration / peakValueCount
            count++
            return nextTime
        } else {
            count = 0
            return waitDuration
        }
    }
}