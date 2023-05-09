package simulator

import time.Time

interface Simulator {
    data class Statistics (
        val elapsed: Long,
        val generators: List<Generator.Statistics>,
        val processors: List<Processor.Statistics>
    )

    fun simulate(time: Time): Statistics
}
