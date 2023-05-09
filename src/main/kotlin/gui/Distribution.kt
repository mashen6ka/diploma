package gui

import time.DurationGenerator
import time.UniformDurationGenerator
import java.awt.*
import javax.swing.*

class ParamField(
	var name: String,
	var field: JTextField
) {}

data class Distribution(
	var name: String,
	var params: MutableList<ParamField>
) {}

class DistributionPanel() {
	var panel: JPanel = JPanel()
		private set
	private var distributions: MutableList<Distribution> = mutableListOf()
	var current: Distribution? = null
		private set

	init {
		this.panel = JPanel()
		this.panel.setBorder(BorderFactory.createTitledBorder("Распределение:"))
		val comboBoxDistr = JComboBox(arrayOf("Равномерное", "Равномерное (пиковое)"))
		this.panel.add(comboBoxDistr)
	
		var panelCard = JPanel(CardLayout())
		panelCard.add(createDistributionPanel("Равномерное", listOf("Min", "Max")), "Равномерное")
		panelCard.add(createDistributionPanel("Равномерное (пиковое)", listOf("Min", "Max", "Длина пика", "Частота пика")), "Равномерное (пиковое)")
		this.panel.add(panelCard, BorderLayout.CENTER)

		val defaultDistr = "Равномерное"
		comboBoxDistr.setSelectedItem(defaultDistr)
		this.current = this.distributions.firstOrNull({ it.name ==  defaultDistr})
		comboBoxDistr.addActionListener {
				val selected = comboBoxDistr.selectedItem.toString()
				val cardLayout = panelCard.layout as CardLayout
				cardLayout.show(panelCard, selected)
				this.current = this.distributions.firstOrNull({ it.name == selected })
		}
	}

	public fun getDurationGenerator(): DurationGenerator? {
		if (this.current!!.name == "Uniform") {
			val minParam = this.current!!.params.firstOrNull({ it.name == "Min" })
			val maxParam = this.current!!.params.firstOrNull({ it.name == "Max" })

			val min = minParam!!.field.getText()
			val max = maxParam!!.field.getText()

			try {
				min.toInt()
				max.toInt()
				return UniformDurationGenerator(min.toInt(), max.toInt())
			}
			catch (e: NumberFormatException) {
				return null
			}
		}
		else {
			return null
		}
	}

	private fun createDistributionPanel(distrName: String, paramsNames: List<String>): JPanel {
		val panel = JPanel(GridBagLayout())
		val c = GridBagConstraints()

		var params = mutableListOf<ParamField>()
		paramsNames.forEachIndexed { i, p ->
			c.gridx = 0
			c.gridy = i
			c.anchor = GridBagConstraints.LINE_END
			panel.add(JLabel("$p:"), c)

			c.gridx = 1
			c.gridy = i
			c.anchor = GridBagConstraints.LINE_END
			var field = JTextField(7)
			panel.add(field, c)
			params.add(ParamField(p, field))
		}
		this.distributions.add(Distribution(distrName, params))
		return panel
	}
}