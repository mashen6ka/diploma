package simulator

import time.Time
import time.DurationGenerator
import mathutils.average

class Generator(
    private val durationGenerator: DurationGenerator,
    private val receivers: List<Processor>?
) : Block {
    data class Statistics(
        val totalRequests: Int,
        val averageGenerationTime: Time
    )

    private var currentRequest: Request? = null
    private var currentStartTime: Time = 0
    private var currentFinishTime: Time = 0
    private var totalRequests: Int = 0
    private var totalGenerationDuration: Time = 0

    fun statistics(): Statistics = Statistics(
        totalRequests = totalRequests,
        averageGenerationTime = average(totalGenerationDuration, totalRequests)
    )

    override fun cleanupState() {
        currentRequest = null
        currentStartTime = 0
        currentFinishTime = 0
        totalRequests = 0
        totalGenerationDuration = 0
    }

    override fun currentFinishTime(): Time = currentFinishTime

    override fun start(currentTime: Time): Time? {
        if (currentRequest != null) return null

        val finishTime = currentTime + durationGenerator.generate()
        currentRequest = Request(finishTime)
        currentStartTime = currentTime
        currentFinishTime = finishTime

        return currentFinishTime
    }

    override fun finish(currentTime: Time): Processor? {
        if (currentRequest == null) return null

        totalRequests += 1
        totalGenerationDuration += currentFinishTime - currentStartTime

        val receiver = receivers?.minByOrNull { it.queueSize() }
        receiver?.enqueue(currentRequest!!)

        currentRequest = null
        currentStartTime = 0
        currentFinishTime = 0

        return receiver
    }
}
