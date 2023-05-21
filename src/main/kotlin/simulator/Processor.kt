package simulator

import time.Time
import time.DurationGenerator
import mathutils.average

class Processor(
    var durationGenerator: DurationGenerator,
    var receivers: List<Processor>?
) : Block {
    data class Statistics(
        val totalRequests: Int,
        val averageProcessingTime: Time,
        val averageWaitingTime: Time
    )

    private val queue: MutableList<Request> = mutableListOf()
    private var currentRequest: Request? = null
    private var currentStartTime: Time = 0
    private var currentFinishTime: Time = 0
    private var totalRequests: Int = 0
    private var totalProcessingTime: Time = 0
    private var totalWaitingTime: Time = 0

    fun statistics(): Statistics = Statistics(
        totalRequests = totalRequests,
        averageProcessingTime = average(totalProcessingTime, totalRequests),
        averageWaitingTime =  average(totalWaitingTime, totalRequests),
    )

    fun enqueue(request: Request) {
//        println(this)
        queue.add(request)
    }

    fun queueSize(): Int = queue.size

    override fun cleanupState() {
        queue.clear()
        currentRequest = null
        currentStartTime = 0
        currentFinishTime = 0
        totalRequests = 0
        totalProcessingTime = 0
        totalWaitingTime = 0
    }

    override fun currentFinishTime(): Time = currentFinishTime

    override fun start(currentTime: Time): Time? {
        if (currentRequest != null || queue.isEmpty())
            return null;

        val finishTime = currentTime + durationGenerator.generate()
        currentRequest = queue.removeFirst()
        currentStartTime = currentTime
        currentFinishTime = finishTime
        totalWaitingTime += currentTime - currentRequest!!.timeIn

        return currentFinishTime
    }

    override fun finish(currentTime: Time): Processor? {
        if (currentRequest == null) return null

        totalRequests += 1
        totalProcessingTime += currentFinishTime - currentStartTime
        currentRequest!!.timeOut = currentTime

        val receiver = receivers?.minByOrNull { it.queueSize() }
        receiver?.enqueue(currentRequest!!)

        currentRequest = null
        currentStartTime = 0
        currentFinishTime = 0

        return receiver
    }
}
