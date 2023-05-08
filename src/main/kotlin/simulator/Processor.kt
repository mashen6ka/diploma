package simulator

import time.Time
import time.DurationGenerator
import java.util.LinkedList
import java.util.Queue

class Processor(
    private val durationGenerator: DurationGenerator
) {
    private val queue: Queue<Request> = LinkedList()
    private var busy: Boolean = false
    private var currentRequest: Request? = null
    private var currentStartTime: Time = 0
    var currentFinishTime: Time = 0
        private set

    var totalRequests: Int = 0
        private set
    var totalProcessingDuration: Time = 0
        private set
    var totalWaitingDuration: Time = 0
        private set

    fun enqueueRequest(request: Request) {
        queue.add(request)
    }

    fun queueSize(): Int {
        return queue.size
    }

    fun startProcessing(currentTime: Time): Time? {
        if (busy || queue.isEmpty())
            return null;

        busy = true
        currentRequest = queue.poll()
        currentStartTime = currentTime
        currentFinishTime = currentTime + durationGenerator.generate()
        totalWaitingDuration += currentTime - currentRequest!!.timeIn

        return currentFinishTime
    }

    fun finishProcessing(currentTime: Time) {
        if (!busy) return

        totalRequests += 1
        totalProcessingDuration += currentFinishTime - currentStartTime
        currentRequest!!.timeOut = currentTime

        currentRequest = null
        currentStartTime = 0
        currentFinishTime = 0
        busy = false
    }
}
