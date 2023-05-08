package simulator

import time.Time
import time.DurationGenerator

class Generator(
    private val durationGenerator: DurationGenerator,
    private val receivers: List<Processor>
) {
    private var busy: Boolean = false
    private var currentRequest: Request? = null
    private var currentStartTime: Time = 0
    var currentFinishTime: Time = 0
        private set

    var totalRequests: Int = 0
        private set
    var totalGenerationDuration: Time = 0
        private set

    fun startGeneration(currentTime: Time): Time? {
        if (busy) return null

        busy = true
        currentStartTime = currentTime
        currentFinishTime = currentTime + durationGenerator.generate()
        currentRequest = Request(currentFinishTime)

        return currentFinishTime
    }

    fun finishGeneration(currentTime: Time): Processor? {
        if (!busy) return null;

        totalRequests += 1
        totalGenerationDuration += currentFinishTime - currentStartTime

        val receiver = receivers.minByOrNull { it.queueSize() }
        receiver?.enqueueRequest(currentRequest!!)

        currentRequest = null
        currentStartTime = 0
        currentFinishTime = 0
        busy = false

        return receiver
    }
}