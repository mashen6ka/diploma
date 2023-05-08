import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
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

    val items = arrayOf("GEN1", "GEN2", "GEN3")
    val list = JList(items)
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    list.border = EmptyBorder(10, 10, 10, 10)
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

    val contentPane = JPanel(BorderLayout())
    contentPane.add(list, BorderLayout.CENTER)
    contentPane.border = EmptyBorder(10, 10, 10, 10)

    frame.contentPane = contentPane
    frame.pack()
    frame.setLocationRelativeTo(null)
    frame.isVisible = true
}

fun createUniformPanel(): JPanel {
	val panel = JPanel(GridBagLayout())
	val c = GridBagConstraints()

	c.gridx = 0
	c.gridy = 0
	c.anchor = GridBagConstraints.LINE_END
	panel.add(JLabel("Min:"), c)

	c.gridx = 1
	c.gridy = 0
	c.anchor = GridBagConstraints.LINE_START
	panel.add(JTextField(10), c)

	c.gridx = 0
	c.gridy = 1
	c.anchor = GridBagConstraints.LINE_END
	panel.add(JLabel("Max:"), c)

	c.gridx = 1
	c.gridy = 1
	c.anchor = GridBagConstraints.LINE_START
	panel.add(JTextField(10), c)

	return panel
}

fun createPeakPanel(): JPanel {
	val panel = JPanel(GridBagLayout())
	val c = GridBagConstraints()

	c.gridx = 0
	c.gridy = 0
	c.anchor = GridBagConstraints.LINE_END
	panel.add(JLabel("Peak Length:"), c)

	c.gridx = 1
	c.gridy = 0
	c.anchor = GridBagConstraints.LINE_START
	panel.add(JTextField(10), c)

	c.gridx = 0
	c.gridy = 1
	c.anchor = GridBagConstraints.LINE_END
	panel.add(JLabel("Frequency:"), c)

	c.gridx = 1
	c.gridy = 1
	c.anchor = GridBagConstraints.LINE_START
	panel.add(JTextField(10), c)

	return panel
}

fun createAndShowModal(parentFrame: JFrame, title: String) {
	val modal = JDialog(parentFrame, title, true)
	modal.isResizable = false

	val comboPanel = JPanel()
	val comboBox = JComboBox(arrayOf("Uniform", "Peak"))
	comboPanel.add(comboBox)

	val mainPanel = JPanel(BorderLayout())
	mainPanel.add(comboPanel, BorderLayout.PAGE_START)

	var cardPanel = JPanel(CardLayout())
	cardPanel.add(createUniformPanel(), "Uniform")
	cardPanel.add(createPeakPanel(), "Peak")
	mainPanel.add(cardPanel, BorderLayout.CENTER)

	comboBox.addActionListener {
			val cardLayout = cardPanel.layout as CardLayout
			cardLayout.show(cardPanel, comboBox.selectedItem.toString())
	}

	val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER))
	val okButton = JButton("OK")
	buttonPanel.add(okButton)

	okButton.addActionListener {
			modal.dispose()
	}

	modal.add(mainPanel)
	modal.add(buttonPanel, BorderLayout.PAGE_END)

	modal.pack()
	modal.setLocationRelativeTo(parentFrame)
	modal.isVisible = true
}