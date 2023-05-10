package gui

import simulator.Processor
import time.DurationGenerator

class ProcessorInfo(
    private var index: Int,
    private var durationGenerator: DurationGenerator,
    private var receivers: List<Processor>?
): BlockInfo {

    override fun update(durationGenerator: DurationGenerator, receivers: List<Processor>?) {
        this.durationGenerator = durationGenerator
        this.receivers = receivers
    }

    override fun getBlock(): Processor {
        return Processor(this.durationGenerator, this.receivers)
    }

    override fun getDurationGenerator(): DurationGenerator {
        return durationGenerator
    }

    override fun getReceivers(): List<Processor>? {
        return receivers
    }

    override fun toString(): String {
        return "PROCESSOR${index}"
    }
}