package simulator

import time.Time
import time.DurationGenerator
import java.util.LinkedList
import java.util.Queue

data class ProcessorStatistics(
    val totalRequests: Int,
    val averageProcessingTime: Time,
    val averageWaitingTime: Time
)

class Processor(
    private val durationGenerator: DurationGenerator
) {
    private val queue: Queue<Request> = LinkedList()
    private var currentRequest: Request? = null
    private var currentStartTime: Time = 0
    var currentFinishTime: Time = 0
        private set

    private var totalRequests: Int = 0
    private var totalProcessingTime: Time = 0
    private var totalWaitingTime: Time = 0

    fun statistics(): ProcessorStatistics =
        ProcessorStatistics(
            totalRequests = totalRequests,
            averageProcessingTime = totalProcessingTime / totalRequests,
            averageWaitingTime =  totalWaitingTime / totalRequests,
        )

    fun enqueueRequest(request: Request) {
        queue.add(request)
    }

    fun queueSize(): Int = queue.size

    fun startProcessing(currentTime: Time): Time? {
        if (currentRequest != null || queue.isEmpty())
            return null;

        val finishTime = currentTime + durationGenerator.generate()
        currentRequest = queue.poll()
        currentStartTime = currentTime
        currentFinishTime = finishTime
        totalWaitingTime += currentTime - currentRequest!!.timeIn

        return currentFinishTime
    }

    fun finishProcessing(currentTime: Time) {
        if (currentRequest == null) return

        totalRequests += 1
        totalProcessingTime += currentFinishTime - currentStartTime
        currentRequest!!.timeOut = currentTime

        currentRequest = null
        currentStartTime = 0
        currentFinishTime = 0
    }
}
