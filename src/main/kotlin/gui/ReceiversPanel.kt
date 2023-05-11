package gui

import simulator.Processor
import java.awt.*
import javax.swing.*

class ReceiversPanel(var currentReceiversInfo: List<ProcessorInfo>?, var processorsInfo: List<ProcessorInfo>?) {
    var jpanel: JPanel = JPanel()
        private set
    private var selectedReceiversInfo: List<ProcessorInfo>? = null

    init {
        this.jpanel = JPanel(GridBagLayout())
        this.jpanel.setBorder(BorderFactory.createTitledBorder("Получатели:"))

        var model = DefaultListModel<ProcessorInfo>()
        for (item in this.processorsInfo!!) {
            model.addElement(item)
        }
        var list = JList(model)
        list.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION

        setCurrentInfo(list)

        val renderer = DefaultListCellRenderer()
        renderer.horizontalAlignment = JLabel.CENTER
        list.cellRenderer = renderer

        list.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                val selected = list.selectedValuesList
//                println("Selected items: $selected")
                if (selected == listOf<ProcessorInfo>())
                    this.selectedReceiversInfo = null
                else
                    this.selectedReceiversInfo = selected
            }
        }
        this.jpanel.add(list, GridBagConstraints().apply {
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })
    }

    private fun setCurrentInfo(list: JList<ProcessorInfo>) {
        if (this.currentReceiversInfo == null) return
        if (this.processorsInfo == null) return

        val selectedIndices = mutableListOf<Int>()
        for (i in this.currentReceiversInfo!!.indices) {
            selectedIndices.add(this.processorsInfo!!.indexOf(this.currentReceiversInfo!![i]))
        }

        list.selectedIndices = selectedIndices.toIntArray()
        this.selectedReceiversInfo = this.currentReceiversInfo
    }

    fun getReceiversInfo(): List<ProcessorInfo>? {
        return selectedReceiversInfo
    }

}