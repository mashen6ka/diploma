package gui

import simulator.Processor
import time.DurationGenerator

class ProcessorInfo(
    private var index: Int,
    private var durationGenerator: DurationGenerator,
    private var receiversInfo: List<ProcessorInfo>?
): BlockInfo {

    override fun update(durationGenerator: DurationGenerator, receiversInfo: List<ProcessorInfo>?) {
        this.durationGenerator = durationGenerator
        this.receiversInfo = receiversInfo
    }

    override fun getBlock(): Processor {
        if (this.receiversInfo == null)
            return Processor(this.durationGenerator, null)
        else {
            val receivers = this.receiversInfo!!.map { it.getBlock() }
            return Processor(this.durationGenerator, receivers)
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
        return "PROCESSOR${index}"
    }
}