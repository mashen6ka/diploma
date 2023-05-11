package gui

import simulator.Simulator
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*

class StatisticsPanel() {
    var jpanel: JPanel = JPanel()
        private set
    private var jtextArea: JTextArea = JTextArea()

    init {
        this.jpanel = JPanel(GridBagLayout())
        this.jpanel.setBorder(BorderFactory.createTitledBorder("Статистика:"))

        this.jtextArea.rows = 15
        this.jtextArea.isEditable = false

        val scrollPane = JScrollPane(this.jtextArea)

        this.jpanel.add(scrollPane, GridBagConstraints().apply {
            weightx = 1.0
            insets = Insets(5, 5, 5, 5)
            fill = GridBagConstraints.HORIZONTAL
        })
    }

    private fun clear() {
        this.jtextArea.isEditable = true
        this.jtextArea.text = ""
        this.jtextArea.isEditable = false
    }

    fun update(statistics: Simulator.Statistics, generatorsInfo: List<GeneratorInfo>, processorsInfo: List<ProcessorInfo>,) {
        clear()

        this.jtextArea.isEditable = true
        this.jtextArea.append("Elapsed Time: ${statistics.elapsed} ms\n\n")
        statistics.generators.forEachIndexed {i, g ->
            this.jtextArea.append("[${generatorsInfo[i]}]\n")
            this.jtextArea.append("Total Requests: ${g.totalRequests}\n")
            this.jtextArea.append("Average Generation Time: ${g.averageGenerationTime}\n\n")
        }
        statistics.processors.forEachIndexed {i, g ->
            this.jtextArea.append("[${processorsInfo[i]}]\n")
            this.jtextArea.append("Total Requests: ${g.totalRequests}\n")
            this.jtextArea.append("Average Processing Time: ${g.averageProcessingTime}\n")
            this.jtextArea.append("Average Waiting Time: ${g.averageWaitingTime}\n\n")
        }
        this.jtextArea.caretPosition = 0
        this.jtextArea.isEditable = false
    }

}