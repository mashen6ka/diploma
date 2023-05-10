package gui

import simulator.Processor
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder
import time.UniformDurationGenerator

class MainWindow() {
    private var frame: JFrame = JFrame("Modelling")
    private var generatorsPanel: BlockListPanel<GeneratorInfo>? = null
    private var processorsPanel: BlockListPanel<ProcessorInfo>? = null

    init {
        this.frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.frame.preferredSize = Dimension(400, 300)
        this.frame.isResizable = false

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
        val panel = JPanel()

        this.generatorsPanel = BlockListPanel("Генераторы", GeneratorInfo::class.java)
        this.processorsPanel = BlockListPanel("Процессоры", ProcessorInfo::class.java)

        this.generatorsPanel!!.addSelectEvent(::createAndShowModal)
        this.processorsPanel!!.addSelectEvent(::createAndShowModal)

        val durationGenerator = UniformDurationGenerator(1, 10)
        val procIndex = this.processorsPanel!!.addBlockInfo(durationGenerator, null)
        val genIndex = this.generatorsPanel!!.addBlockInfo(durationGenerator, null)

        panel.add(this.generatorsPanel!!.jpanel)
        panel.add(this.processorsPanel!!.jpanel)

        return panel
    }

    private fun createAndShowModal(blockInfo: BlockInfo, index: Int) {
        val modal = JDialog(this.frame, blockInfo.toString(), true)
        modal.isResizable = false
        println("BLOCK ${blockInfo.getReceiversInfo()}")
        val distributionPanel = DistributionPanel(blockInfo.getDurationGenerator())
        val receiversPanel = ReceiversPanel(blockInfo.getReceiversInfo(), this.processorsPanel!!.getBlocksInfo())


        val panelButton = JPanel(FlowLayout(FlowLayout.CENTER))
        val okButton = JButton("OK")
        panelButton.add(okButton)


        okButton.addActionListener {
            var durationGenerator = distributionPanel.getDurationGenerator()
            var receiversInfo = receiversPanel.getReceiversInfo()
            if (durationGenerator != null) {
                if (blockInfo is GeneratorInfo)
                    this.generatorsPanel!!.updateBlockInfo(index, durationGenerator, receiversInfo)
                else if (blockInfo is ProcessorInfo)
                    this.processorsPanel!!.updateBlockInfo(index, durationGenerator, receiversInfo)
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