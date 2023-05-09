package gui

import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

class MainWindow() {
    var frame: JFrame = JFrame("Modelling")
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
        var c = GridBagConstraints()

        var blocksPanel = createBlocksPanel()

        c.fill = GridBagConstraints.HORIZONTAL
        c.gridx = 0
        c.gridy = 0
        contentPane.add(blocksPanel, c)

        return contentPane
    }

    private fun createBlocksPanel(): JPanel {
        var panel = JPanel()

        var panelGen = createBlockPanel("Генераторы",  arrayOf("GENERATOR1", "GENERATOR2", "GENERATOR3", "GENERATOR4", "GENERATOR5", "GENERATOR6"))
        var panelProc = createBlockPanel("Процессы",  arrayOf("PROCESSOR1", "PROCESSOR2", "PROCESSOR3", "PROCESSOR4"))

        panel.add(panelGen)
        panel.add(panelProc)

        return panel
    }

    private fun createBlockPanel(blockName: String, blocks: Array<String>): JPanel {
        var panel = JPanel(GridBagLayout())
        panel.setBorder(BorderFactory.createTitledBorder("$blockName:"))

        var list = createBlockList(blocks)
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

    private fun createBlockList(items: Array<String>): JList<String> {
        val list = JList(items)
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        list.setVisibleRowCount(5)
        list.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                if (e.clickCount == 1) {
                    val selected = list.selectedValue
                    if (selected != null) {
                        createAndShowModal(selected)
                    }
                }
            }
        })
        val renderer = DefaultListCellRenderer()
        renderer.horizontalAlignment = JLabel.CENTER
        list.cellRenderer = renderer

        return list
    }

    private fun createAndShowModal(title: String) {
        val modal = JDialog(this.frame, title, true)
        modal.isResizable = false

        val panelDistr = DistributionPanel()

        val panelReceivers = JPanel(GridBagLayout())
        panelReceivers.setBorder(BorderFactory.createTitledBorder("Получатели:"))
        val listReceivers = JList(arrayOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5"))
        listReceivers.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        val renderer = DefaultListCellRenderer()
        renderer.horizontalAlignment = JLabel.CENTER
        listReceivers.cellRenderer = renderer

        listReceivers.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                val selected = listReceivers.selectedValuesList
                println("Selected items: $selected")
            }
        }
        panelReceivers.add(listReceivers, GridBagConstraints().apply {
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })

        val panelButton = JPanel(FlowLayout(FlowLayout.CENTER))
        val okButton = JButton("OK")
        panelButton.add(okButton)


        okButton.addActionListener {
            println(panelDistr.getDurationGenerator())
            modal.dispose()
        }

        val panelMain = JPanel(GridBagLayout())
        panelMain.border = EmptyBorder(10, 10, 10, 10)
        panelMain.add(panelDistr.panel, GridBagConstraints().apply{
            gridx = 0
            gridy = 0
            fill = GridBagConstraints.HORIZONTAL
        })

        panelMain.add(panelReceivers, GridBagConstraints().apply{
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