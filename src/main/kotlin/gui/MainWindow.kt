package gui

import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder
import time.UniformDurationGenerator

class MainWindow() {
    var frame: JFrame = JFrame("Modelling")
    var generatorsInfo: MutableList<GeneratorInfo> = mutableListOf()
    var processorsInfo: MutableList<ProcessorInfo> = mutableListOf()

    init {
        this.frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.frame.preferredSize = Dimension(400, 300)
        this.frame.isResizable = false

        var processorInfo = ProcessorInfo(1, UniformDurationGenerator(1, 10), null)
        var generatorInfo = GeneratorInfo(1, UniformDurationGenerator(1, 10), listOf(processorInfo))
        this.generatorsInfo.add(generatorInfo)
        this.processorsInfo.add(processorInfo)

        frame.contentPane = createWindowContent()
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }

    private fun createWindowContent(): JPanel {
        var contentPane = JPanel(GridBagLayout())
        var blocksPanel = createBlocksPanel()

        contentPane.add(blocksPanel, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            gridx = 0
            gridy = 0
        })

        return contentPane
    }

    private fun createBlocksPanel(): JPanel {
        var panel = JPanel()

        var panelGen = createBlockPanel("Генераторы", this.generatorsInfo as MutableList<BlockInfo>)
        var panelProc = createBlockPanel("Процессы",  this.processorsInfo as MutableList<BlockInfo>)

        panel.add(panelGen)
        panel.add(panelProc)

        return panel
    }

    private fun createBlockPanel(blockName: String, blocksInfo: MutableList<BlockInfo>): JPanel {
        var panel = JPanel(GridBagLayout())
        panel.setBorder(BorderFactory.createTitledBorder("$blockName:"))

        var list = createBlockList(blocksInfo)
        var scroll = JScrollPane(list)

        var addButton = JButton("+")
        var delButton = JButton("-")

        panel.add(scroll, GridBagConstraints().apply {
            gridwidth = 2
            weightx = 1.0
            weighty = 1.0
            insets = Insets(0, 5, 0, 5)
            fill = GridBagConstraints.HORIZONTAL
        })

        panel.add(addButton, GridBagConstraints().apply {
            gridx = 0
            gridy = 1
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })

        panel.add(delButton, GridBagConstraints().apply {
            gridx = 1
            gridy = 1
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })

        return panel
    }

    private fun createBlockList(blocksInfo: MutableList<BlockInfo>): JList<BlockInfo> {
        val model = DefaultListModel<BlockInfo>()
        for (item in blocksInfo) {
            model.addElement(item)
        }
        val list = JList(model)
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        list.setVisibleRowCount(5)
        list.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                if (e.clickCount == 1) {
                    val selectedValue = list.selectedValue
                    val selectedIndex = list.selectedIndex
                    if (selectedValue != null && selectedIndex != null) {
                        createAndShowModal(selectedValue, selectedIndex)
                    }
                }
            }
        })
        val renderer = DefaultListCellRenderer()
        renderer.horizontalAlignment = JLabel.CENTER
        list.cellRenderer = renderer

        return list
    }

    private fun createAndShowModal(blockInfo: BlockInfo, blockIndex: Int) {
        val modal = JDialog(this.frame, blockInfo.toString(), true)
        modal.isResizable = false

        val distributionPanel = DistributionPanel(blockInfo.getDurationGenerator())
        val receiversPanel = ReceiversPanel(blockInfo.getReceiversInfo(), this.processorsInfo)


        val panelButton = JPanel(FlowLayout(FlowLayout.CENTER))
        val okButton = JButton("OK")
        panelButton.add(okButton)


        okButton.addActionListener {
            var durationGenerator = distributionPanel.getDurationGenerator()
            var receiversInfo = receiversPanel.getReceiversInfo()
            if (durationGenerator != null) {
                if (blockInfo is GeneratorInfo)
                    this.generatorsInfo[blockIndex].update(durationGenerator, receiversInfo)
                modal.dispose()
            }
        }

        val panelMain = JPanel(GridBagLayout())
        panelMain.border = EmptyBorder(10, 10, 10, 10)
        panelMain.add(distributionPanel.jpanel, GridBagConstraints().apply{
            gridx = 0
            gridy = 0
            fill = GridBagConstraints.HORIZONTAL
        })

        panelMain.add(receiversPanel.jpanel, GridBagConstraints().apply{
            gridx = 0
            gridy = 1
            fill = GridBagConstraints.HORIZONTAL
        })

        modal.add(panelMain)
        modal.add(panelButton, BorderLayout.PAGE_END)

        modal.pack()
        modal.setLocationRelativeTo(this.frame)
        modal.isVisible = true
    }
}