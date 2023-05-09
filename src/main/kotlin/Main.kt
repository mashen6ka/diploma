import gui.DistributionPanel

import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder


fun main() {
		SwingUtilities.invokeLater {
				createAndShowGUI()
		}
}

fun createAndShowGUI() {
		val frame = JFrame("Modelling")
		frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
		frame.preferredSize = Dimension(400, 300)
		frame.isResizable = false

		var panelBlocks = createBlocksPanel(frame)

		var contentPane = JPanel(GridBagLayout())
		var c = GridBagConstraints()

		c.fill = GridBagConstraints.HORIZONTAL
		c.gridx = 0
		c.gridy = 0
		contentPane.add(panelBlocks, c)

		frame.contentPane = contentPane
		frame.pack()
		frame.setLocationRelativeTo(null)
		frame.isVisible = true
}

fun createBlocksPanel(frame: JFrame): JPanel {
	var panel = JPanel()

	var panelGen = JPanel(GridBagLayout())
	panelGen.setBorder(BorderFactory.createTitledBorder("Генераторы:"))

	var panelProc = JPanel(GridBagLayout())
	panelProc.setBorder(BorderFactory.createTitledBorder("Процессоры:"))

	var generators = arrayOf("GENERATOR1", "GENERATOR2", "GENERATOR3", "GENERATOR4", "GENERATOR5", "GENERATOR6")
	var listGen = createList(frame, generators)
	var scrollGen = JScrollPane(listGen)

	var processors = arrayOf("PROCESSOR1", "PROCESSOR2", "PROCESSOR3")
	var listProc = createList(frame, processors)
	var scrollProc = JScrollPane(listProc);

	var buttonAddGen = JButton("+")
	var buttonDelGen = JButton("-")

	var buttonAddProc = JButton("+")
	var buttonDelProc = JButton("-")


	panelGen.add(scrollGen, GridBagConstraints().apply {
		gridwidth = 2
		weightx = 1.0
		weighty = 1.0
		insets = Insets(0, 5, 0, 5)
		fill = GridBagConstraints.HORIZONTAL
	})

	panelGen.add(buttonAddGen, GridBagConstraints().apply {
		gridx = 0
		gridy = 1
		weightx = 1.0
		weighty = 1.0
		fill = GridBagConstraints.HORIZONTAL
	})

	panelGen.add(buttonDelGen, GridBagConstraints().apply {
		gridx = 1
		gridy = 1
		weightx = 1.0
		weighty = 1.0
		fill = GridBagConstraints.HORIZONTAL
	})

	panelProc.add(scrollProc, GridBagConstraints().apply {
		gridwidth = 2
		weightx = 1.0
		weighty = 1.0
		insets = Insets(0, 5, 0, 5)
		fill = GridBagConstraints.HORIZONTAL
	})

	panelProc.add(buttonAddProc, GridBagConstraints().apply {
		gridx = 0
		gridy = 1
		weightx = 1.0
		weighty = 1.0
		fill = GridBagConstraints.HORIZONTAL
	})

	panelProc.add(buttonDelProc, GridBagConstraints().apply {
		gridx = 1
		gridy = 1
		weightx = 1.0
		weighty = 1.0
		fill = GridBagConstraints.HORIZONTAL
	})

	panel.add(panelGen)
	panel.add(panelProc)

	return panel
}

fun createList(frame: JFrame, items: Array<String>): JList<String> {
	val list = JList(items)
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
	// list.border = EmptyBorder(10, 0, 10, 0)
	list.setVisibleRowCount(5)
	list.addMouseListener(object : java.awt.event.MouseAdapter() {
			override fun mouseClicked(e: java.awt.event.MouseEvent) {
					if (e.clickCount == 1) {
							val selected = list.selectedValue
							if (selected != null) {
									createAndShowModal(frame, selected)
							}
					}
			}
	})
	val renderer = DefaultListCellRenderer()
	renderer.horizontalAlignment = JLabel.CENTER
	list.cellRenderer = renderer
	return list
}

fun createAndShowModal(parentFrame: JFrame, title: String) {
	val modal = JDialog(parentFrame, title, true)
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
	modal.setLocationRelativeTo(parentFrame)
	modal.isVisible = true
}