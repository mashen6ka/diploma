package gui

import simulator.Generator
import simulator.Processor
import time.DurationGenerator

class GeneratorInfo(
    private var index: Int,
    private var durationGenerator: DurationGenerator,
    private var receivers: List<Processor>?
): BlockInfo {

    override fun update(durationGenerator: DurationGenerator, receivers: List<Processor>?) {
        this.durationGenerator = durationGenerator
        this.receivers = receivers
    }

    override fun getBlock(): Generator {
        return Generator(this.durationGenerator, this.receivers)
    }

    override fun getDurationGenerator(): DurationGenerator {
        return durationGenerator
    }

    override fun getReceivers(): List<Processor>? {
        return receivers
    }

    override fun toString(): String {
        return "GENERATOR${index}"
    }
}