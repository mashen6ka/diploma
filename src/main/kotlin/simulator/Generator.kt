package simulator

import time.Time
import time.DurationGenerator

data class GeneratorStatistics(
    val totalRequests: Int,
    val averageGenerationTime: Time
)

class Generator(
    private val durationGenerator: DurationGenerator,
    private val receivers: Array<Processor>
) {
    private var currentRequest: Request? = null
    private var currentStartTime: Time = 0
    var currentFinishTime: Time = 0
        private set

    private var totalRequests: Int = 0
    private var totalGenerationDuration: Time = 0

    fun statistics(): GeneratorStatistics =
        GeneratorStatistics(
            totalRequests = totalRequests,
            averageGenerationTime = totalGenerationDuration / totalRequests
        )

    fun startGeneration(currentTime: Time): Time? {
        if (currentRequest != null) return null

        val finishTime = currentTime + durationGenerator.generate()
        currentRequest = Request(finishTime)
        currentStartTime = currentTime
        currentFinishTime = finishTime

        return currentFinishTime
    }

    fun finishGeneration(currentTime: Time): Processor? {
        if (currentRequest == null) return null;

        totalRequests += 1
        totalGenerationDuration += currentFinishTime - currentStartTime

        val receiver = receivers.minByOrNull { it.queueSize() }
        receiver?.enqueueRequest(currentRequest!!)

        currentRequest = null
        currentStartTime = 0
        currentFinishTime = 0

        return receiver
    }
}