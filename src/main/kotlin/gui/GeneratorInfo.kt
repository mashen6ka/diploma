package gui

import simulator.Generator
import time.DurationGenerator

class GeneratorInfo(
    private var index: Int,
    private var durationGenerator: DurationGenerator,
    private var receiversInfo: List<ProcessorInfo>?
): BlockInfo {
    private var block: Generator? = null

    init {
        val receivers = this.receiversInfo?.map { it.getBlock() }
        this.block = Generator(this.durationGenerator, receivers)
    }

    override fun update(durationGenerator: DurationGenerator, receiversInfo: List<ProcessorInfo>?) {
        this.durationGenerator = durationGenerator
        this.receiversInfo = receiversInfo

        this.block!!.durationGenerator = durationGenerator

        val receivers = this.receiversInfo?.map { it.getBlock() }
        this.block!!.receivers = receivers
    }

    override fun getBlock(): Generator {
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
        return "GENERATOR${index+1}"
    }
}