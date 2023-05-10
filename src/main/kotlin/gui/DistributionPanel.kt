package gui

import time.DurationGenerator
import time.UniformDurationGenerator
import java.awt.*
import javax.swing.*

class ParamField(
	var name: String,
	var field: JTextField,
) {}

class Param(
	var name: String,
){
	var value: Any? = null
	constructor(name: String, value: Any?) : this(name) {
		this.value = value
	}
}

data class Distribution(
	var name: String,
	var params: MutableList<ParamField>
) {}

class DistributionPanel(var currentGenerator: DurationGenerator) {
	var jpanel: JPanel = JPanel()
		private set
	private var distributions: MutableList<Distribution> = mutableListOf()
	var currentDistribution: Distribution? = null
		private set
	init {
		this.jpanel = JPanel()
		this.jpanel.setBorder(BorderFactory.createTitledBorder("Распределение:"))
		val comboBox = JComboBox(arrayOf("Равномерное", "Равномерное (пиковое)"))
		this.jpanel.add(comboBox)
	
		var panelCard = JPanel(CardLayout())

		val uniformParams = listOf<Param>(Param("Min"), Param("Max"))
		panelCard.add(createDistributionPanel("Равномерное", uniformParams), "Равномерное")

		val uniformPeakParams = listOf<Param>(Param("Длина пика"), Param("Частота пика"))
		panelCard.add(createDistributionPanel("Равномерное (пиковое)", uniformPeakParams), "Равномерное (пиковое)")

		this.jpanel.add(panelCard, BorderLayout.CENTER)

		setCurrentInfo(comboBox)
		comboBox.addActionListener {
				val selected = comboBox.selectedItem.toString()
				val cardLayout = panelCard.layout as CardLayout
				cardLayout.show(panelCard, selected)
				this.currentDistribution = this.distributions.firstOrNull({ it.name == selected })
		}
	}

	private fun setCurrentInfo(comboBox: JComboBox<String>)
	{
		if (this.currentGenerator is UniformDurationGenerator) {
			val distrName = "Равномерное"
			var distr = this.distributions.firstOrNull({ it.name ==  distrName})
			var minField = distr!!.params.firstOrNull({ it.name == "Min" })!!.field
			minField.text = "${(this.currentGenerator as UniformDurationGenerator).getMin()}"


			var maxField = distr!!.params.firstOrNull({ it.name == "Max" })!!.field
			maxField.text = "${(this.currentGenerator as UniformDurationGenerator).getMax()}"

			comboBox.setSelectedItem(distrName)
			this.currentDistribution = this.distributions.firstOrNull({ it.name ==  distrName})
		} else {

		}

	}

	fun getDurationGenerator(): DurationGenerator? {
		if (this.currentDistribution!!.name == "Равномерное") {
			val minParam = this.currentDistribution!!.params.firstOrNull({ it.name == "Min" })
			val maxParam = this.currentDistribution!!.params.firstOrNull({ it.name == "Max" })

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

	private fun createDistributionPanel(distrName: String, params: List<Param>): JPanel {
		val panel = JPanel(GridBagLayout())
		val c = GridBagConstraints()

		var paramFields = mutableListOf<ParamField>()
		params.forEachIndexed { i, p ->
			c.gridx = 0
			c.gridy = i
			c.anchor = GridBagConstraints.LINE_END
			panel.add(JLabel("${p.name}:"), c)

			c.gridx = 1
			c.gridy = i
			c.anchor = GridBagConstraints.LINE_END
			var field = JTextField(7)

			panel.add(field, c)
			paramFields.add(ParamField(p.name, field))
		}
		this.distributions.add(Distribution(distrName, paramFields))
		return panel
	}
}