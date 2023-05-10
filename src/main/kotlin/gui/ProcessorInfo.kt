package gui

import simulator.Processor
import time.DurationGenerator

class ProcessorInfo(
    private var index: Int,
    private var durationGenerator: DurationGenerator,
    private var receiversInfo: List<ProcessorInfo>?
): BlockInfo {
    private var block: Processor? = null

    init {
        if (this.receiversInfo == null)
            this.block = Processor(this.durationGenerator, null)
        else {
            val receivers = this.receiversInfo!!.map { it.getBlock() }
            this.block = Processor(this.durationGenerator, receivers)
        }
    }

    override fun update(durationGenerator: DurationGenerator, receiversInfo: List<ProcessorInfo>?) {
        this.durationGenerator = durationGenerator
        this.receiversInfo = receiversInfo

        this.block!!.durationGenerator = durationGenerator
        if (this.receiversInfo == null)
            this.block!!.receivers = null
        else {
            val receivers = this.receiversInfo!!.map { it.getBlock() }
            this.block!!.receivers = receivers
        }
    }

    override fun getBlock(): Processor {
        return this.block!!
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