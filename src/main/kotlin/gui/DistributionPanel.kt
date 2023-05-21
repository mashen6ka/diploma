package gui

import time.DurationGenerator
import time.PoissonDurationGenerator
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

enum class DistributionType(val value: String) {
	UNIFORM("Равномерное"),
	POISSONPEAK("Равномерное с пиками")
}

data class Distribution(
	var name: String,
	var params: MutableList<ParamField>
) {}

class DistributionPanel(var currentGenerator: DurationGenerator) {
	var jpanel: JPanel = JPanel()
		private set

	private var panelCard: JPanel = JPanel()
	private var distributions: MutableList<Distribution> = mutableListOf()

	var currentDistribution: Distribution? = null
		private set

	init {
		this.jpanel = JPanel()
		this.jpanel.setBorder(BorderFactory.createTitledBorder("Распределение:"))
		val comboBox = JComboBox(arrayOf(DistributionType.UNIFORM.value, DistributionType.POISSONPEAK.value))
		this.jpanel.add(comboBox)
	
		this.panelCard = JPanel(CardLayout())

		val uniformParams = listOf<Param>(Param("Min"), Param("Max"))
		this.panelCard.add(createDistributionPanel(DistributionType.UNIFORM.value, uniformParams), DistributionType.UNIFORM.value)

		val uniformPeakParams = listOf<Param>(Param("Min"), Param("Max"), Param("Длина пика"))
		this.panelCard.add(createDistributionPanel(DistributionType.POISSONPEAK.value, uniformPeakParams), DistributionType.POISSONPEAK.value)

		this.jpanel.add(this.panelCard, BorderLayout.CENTER)

		setCurrentInfo(comboBox)
		comboBox.addActionListener {
				val selected = comboBox.selectedItem.toString()
				val cardLayout = this.panelCard.layout as CardLayout
				cardLayout.show(this.panelCard, selected)
				this.currentDistribution = this.distributions.firstOrNull({ it.name == selected })
		}
	}

	private fun setCurrentInfo(comboBox: JComboBox<String>)
	{
		var distrName: String = ""
		if (this.currentGenerator is UniformDurationGenerator) {
			distrName = DistributionType.UNIFORM.value
			val distr = this.distributions.firstOrNull({ it.name ==  distrName})

			val minField = distr!!.params.firstOrNull({ it.name == "Min" })!!.field
			minField.text = "${(this.currentGenerator as UniformDurationGenerator).getMin()}"

			val maxField = distr.params.firstOrNull({ it.name == "Max" })!!.field
			maxField.text = "${(this.currentGenerator as UniformDurationGenerator).getMax()}"
		} else if (this.currentGenerator is PoissonDurationGenerator) {
			distrName = DistributionType.POISSONPEAK.value
			val distr = this.distributions.firstOrNull({ it.name ==  distrName})

			val minField = distr!!.params.firstOrNull{ it.name == "Min" }!!.field
			minField.text = "${(this.currentGenerator as PoissonDurationGenerator).getMin()}"

			val maxField = distr.params.firstOrNull({ it.name == "Max" })!!.field
			maxField.text = "${(this.currentGenerator as PoissonDurationGenerator).getMax()}"

			val peakLengthField = distr!!.params.firstOrNull({ it.name == "Длина пика" })!!.field
			peakLengthField.text = "${(this.currentGenerator as PoissonDurationGenerator).getPeakLength()}"
		}
		comboBox.selectedItem = distrName

		val cardLayout = this.panelCard.layout as CardLayout
		cardLayout.show(panelCard, distrName)

		this.currentDistribution = this.distributions.firstOrNull({ it.name ==  distrName})
	}

	fun getDurationGenerator(): DurationGenerator? {
		if (this.currentDistribution!!.name == DistributionType.UNIFORM.value) {
			val minParam = this.currentDistribution!!.params.firstOrNull({ it.name == "Min" })
			val maxParam = this.currentDistribution!!.params.firstOrNull({ it.name == "Max" })

			val min = minParam!!.field.getText()
			val max = maxParam!!.field.getText()

			try {
				return UniformDurationGenerator(min.toInt(), max.toInt())
			}
			catch (e: NumberFormatException) {
				return null
			}
		}
		else if (this.currentDistribution!!.name == DistributionType.POISSONPEAK.value){
			val minParam = this.currentDistribution!!.params.firstOrNull({ it.name == "Min" })
			val maxParam = this.currentDistribution!!.params.firstOrNull({ it.name == "Max" })
			val peakLengthParam = this.currentDistribution!!.params.firstOrNull({ it.name == "Длина пика" })

			val min = minParam!!.field.getText()
			val max = maxParam!!.field.getText()
			val peakLength = peakLengthParam!!.field.getText()

			try {
				return PoissonDurationGenerator(min.toInt(), max.toInt(), peakLength.toInt())
			}
			catch (e: NumberFormatException) {
				return null
			}
		}
		else {
			return  null
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