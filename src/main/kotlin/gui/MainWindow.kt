package gui

import simulator.*
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder
import time.UniformDurationGenerator

class MainWindow() {
    private var frame: JFrame = JFrame("Modelling")
    private var generatorsPanel: BlockListPanel<GeneratorInfo>? = null
    private var processorsPanel: BlockListPanel<ProcessorInfo>? = null
    private var statisticsPanel: StatisticsPanel? = null
    private var settingsPanel: SettingsPanel? = null

    init {
        this.frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.frame.isResizable = false

        frame.contentPane = createWindowContent()
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }

    private fun createWindowContent(): JPanel {
        val contentPane = JPanel(GridBagLayout())
        contentPane.border = EmptyBorder(10, 10, 10, 10)

        this.settingsPanel = SettingsPanel()
        contentPane.add(this.settingsPanel!!.jpanel, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            gridx = 0
            gridy = 0
        })

        val blocksPanel = createBlocksPanel()
        contentPane.add(blocksPanel, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            gridx = 0
            gridy = 1
        })

        val simulateButton = JButton("Моделировать")
        contentPane.add(simulateButton, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            gridx = 0
            gridy = 2
        })
        simulateButton.addActionListener{
            simulate()
        }

        this.statisticsPanel = StatisticsPanel()
        contentPane.add(this.statisticsPanel!!.jpanel, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            gridx = 0
            gridy = 3
        })

        return contentPane
    }

    private fun simulate() {
        val processorsInfo = this.processorsPanel!!.getBlocksInfo()
        val generatorsInfo = this.generatorsPanel!!.getBlocksInfo()

        val processors = processorsInfo.map{it.getBlock()}
        val generators = generatorsInfo.map{it.getBlock()}

        var simulator: Simulator? = null
        if (this.settingsPanel!!.getMethod() == Method.HYBRID.value) {
            val arraySize = this.settingsPanel!!.getArraySize()
            val tableWidth = this.settingsPanel!!.getTableWidth()
            if (arraySize != null && tableWidth != null)
                simulator = HybridSimulator(generators, processors, arraySize, tableWidth)
        } else if (this.settingsPanel!!.getMethod() == Method.TIMEBASED.value) {
            val deltaT = this.settingsPanel!!.getDeltaT()
            if (deltaT != null)
                simulator = TimeBasedSimulator(generators, processors, deltaT)
        } else if (this.settingsPanel!!.getMethod() == Method.EVENTBASED.value) {
            simulator = EventBasedSimulator(generators, processors)
        }

        if (simulator != null) {
            val time = this.settingsPanel!!.getTime()
            if (time != null) {
                val statistics = simulator.simulate(time)
                this.statisticsPanel!!.update(statistics, generatorsInfo, processorsInfo)
            }
        }
    }

    private fun createBlocksPanel(): JPanel {
        val panel = JPanel()

        this.generatorsPanel = BlockListPanel("Генераторы", GeneratorInfo::class.java)
        this.processorsPanel = BlockListPanel("Процессоры", ProcessorInfo::class.java)

        this.generatorsPanel!!.createUpdateButtonEvent(::createBlockModal)
        this.generatorsPanel!!.createAddButtonEvent{ blockInfo, index ->
            val durationGenerator = UniformDurationGenerator(1, 10)
            this.generatorsPanel!!.addBlockInfo(durationGenerator, null)
        }
        this.generatorsPanel!!.createDeleteButtonEvent{ blockInfo, index ->
            if (blockInfo != null && index != null)
                this.generatorsPanel!!.deleteBlockInfo(index)
        }

        this.processorsPanel!!.createUpdateButtonEvent(::createBlockModal)
        this.processorsPanel!!.createAddButtonEvent{ blockInfo, index ->
            val durationGenerator = UniformDurationGenerator(1, 10)
            this.processorsPanel!!.addBlockInfo(durationGenerator, null)
        }
        this.processorsPanel!!.createDeleteButtonEvent{ blockInfo, index ->
            if (blockInfo != null && index != null) {
//                 TODO: удалять из ресиверов генераторов/процессоров удаляемый процессор
                this.processorsPanel!!.deleteBlockInfo(index)
            }
        }

        val durationGenerator = UniformDurationGenerator(1, 10)
        val procIndex = this.processorsPanel!!.addBlockInfo(durationGenerator, null)
        val genIndex = this.generatorsPanel!!.addBlockInfo(durationGenerator, null)

        panel.add(this.generatorsPanel!!.jpanel)
        panel.add(this.processorsPanel!!.jpanel)

        return panel
    }

    private fun createBlockModal(blockInfo: BlockInfo?, index: Int?) {
        if (blockInfo == null || index == null) return

        val modal = JDialog(this.frame, blockInfo.toString(), true)
        modal.isResizable = false
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