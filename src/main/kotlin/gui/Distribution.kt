package gui

import time.Time
import java.awt.*
import javax.swing.*

class UniformDistribution(
	var min: Time,
	var max: Time
) {
}

class PeakDistribution(
	var peakLength: Time,
	var frequency: Time
) {
}

class DistributionPanel() {
	var panel: JPanel = JPanel()
	init {
		this.panel = JPanel()
		this.panel.setBorder(BorderFactory.createTitledBorder("Распределение:"))
		val comboBoxDistr = JComboBox(arrayOf("Uniform", "Peak"))
		this.panel.add(comboBoxDistr)
	
		var panelCard = JPanel(CardLayout())
		panelCard.add(createUniformPanel(), "Uniform")
		panelCard.add(createPeakPanel(), "Peak")
		this.panel.add(panelCard, BorderLayout.CENTER)
	
		comboBoxDistr.addActionListener {
				val cardLayout = panelCard.layout as CardLayout
				cardLayout.show(panelCard, comboBoxDistr.selectedItem.toString())
		}
	}

	private fun createUniformPanel(): JPanel {
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

	private fun createPeakPanel(): JPanel {
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

}