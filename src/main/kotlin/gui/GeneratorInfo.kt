package gui

import simulator.Generator
import simulator.Processor
import time.DurationGenerator

class GeneratorInfo(
    private var index: Int,
    private var durationGenerator: DurationGenerator,
    private var receiversInfo: List<ProcessorInfo>?
): BlockInfo {

    override fun update(durationGenerator: DurationGenerator, receiversInfo: List<ProcessorInfo>?) {
        this.durationGenerator = durationGenerator
        this.receiversInfo = receiversInfo
    }

    override fun getBlock(): Generator {
        if (this.receiversInfo == null)
            return Generator(this.durationGenerator, null)
        else {
            val receivers = this.receiversInfo!!.map { it.getBlock() }
            return Generator(this.durationGenerator, receivers)
        }
    }

    override fun getDurationGenerator(): DurationGenerator {
        return durationGenerator
    }

    override fun getReceiversInfo(): List<ProcessorInfo>? {
        return receiversInfo
    }

    override fun getIndex(): Int {
        return  index
    }

    override fun toString(): String {
        return "GENERATOR${index}"
    }
}