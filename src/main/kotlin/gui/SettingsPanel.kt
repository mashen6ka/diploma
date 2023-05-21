package gui

import simulator.Simulator
import java.awt.*
import javax.swing.*


enum class Method(val value: String) {
    HYBRID("Комбинированный"),
    TIMEBASED("Дельта Т"),
    EVENTBASED("Событийный")
}

class SettingsPanel() {
    var jpanel: JPanel = JPanel()
        private set
    private var comboBox: JComboBox<String> = JComboBox()

    private var timeField: JTextField = JTextField()
    private var deltaTField: JTextField = JTextField()
    private var arraySizeField: JTextField = JTextField()
    private var tableWidthField: JTextField = JTextField()

    init {
        this.jpanel = JPanel(GridBagLayout())
        this.jpanel.setBorder(BorderFactory.createTitledBorder("Настройки:"))

        this.jpanel.add(JLabel("Время (тик):"), GridBagConstraints().apply {
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(0, 5, 0, 5)
        })

        this.timeField = JTextField(7)
        this.jpanel.add(this.timeField, GridBagConstraints().apply {
            gridx = 1
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(0, 5, 0, 5)
        })

        this.jpanel.add(JLabel("Алгоритм:"), GridBagConstraints().apply {
            gridy = 1
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(0, 5, 0, 5)
        })

        this.comboBox = JComboBox(arrayOf(Method.HYBRID.value, Method.TIMEBASED.value, Method.EVENTBASED.value))
        this.jpanel.add(this.comboBox, GridBagConstraints().apply {
            gridx = 1
            gridy = 1
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(0, 5, 0, 5)
        })

        this.jpanel.add(createCardPanel(), GridBagConstraints().apply {
            gridy = 2
            gridwidth = 2
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(0, 5, 0, 5)
        })

        setCurrentInfo()
    }

    fun getMethod(): String {
        return this.comboBox.selectedItem.toString()
    }

    fun getTime(): Int? {
        try {
            return this.timeField.text.toInt()
        } catch (e: NumberFormatException) {
            return null
        }
    }

    fun getDeltaT(): Int? {
        try {
            return this.deltaTField.text.toInt()
        } catch (e: NumberFormatException) {
            return null
        }
    }

    fun getArraySize(): Int? {
        try {
            return this.arraySizeField.text.toInt()
        } catch (e: NumberFormatException) {
            return null
        }
    }

    fun getTableWidth(): Int? {
        try {
            return this.tableWidthField.text.toInt()
        } catch (e: NumberFormatException) {
            return null
        }
    }

    private fun setCurrentInfo() {
        this.comboBox.selectedItem = Method.HYBRID.value
        this.timeField.text = "${100}"
        this.deltaTField.text = "${1}"
        this.tableWidthField.text = "${10}"
        this.arraySizeField.text = "${15}"
    }

    private fun createCardPanel(): JPanel {
        val cardPanel = JPanel(CardLayout())

        cardPanel.add(createCombinedPanel(), Method.HYBRID.value)
        cardPanel.add(createTimeBasedPanel(), Method.TIMEBASED.value)
        cardPanel.add(createEventBasedPanel(), Method.EVENTBASED.value)

        this.comboBox.addActionListener {
            val selected = this.comboBox.selectedItem.toString()
            val cardLayout = cardPanel.layout as CardLayout
            cardLayout.show(cardPanel, selected)
        }
        return cardPanel
    }

    private fun createTimeBasedPanel(): JPanel {
        val panel = JPanel(GridBagLayout())

        panel.add(JLabel("Delta t (тик):"), GridBagConstraints().apply {
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })

        this.deltaTField = JTextField(7)
        panel.add(this.deltaTField, GridBagConstraints().apply {
            gridx = 1
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })

        return panel
    }

    private fun createEventBasedPanel(): JPanel {
        val panel = JPanel(GridBagLayout())
        return panel
    }

    private fun createCombinedPanel(): JPanel {
        val panel = JPanel(GridBagLayout())

        panel.add(JLabel("Размер нулевого уровня часов (тик):"), GridBagConstraints().apply {
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })

        this.arraySizeField = JTextField(7)
        panel.add(this.arraySizeField, GridBagConstraints().apply {
            gridx = 1
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })

        panel.add(JLabel("Размер ненулевого уровня часов (тик):"), GridBagConstraints().apply {
            gridy = 1
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })

        this.tableWidthField = JTextField(7)
        panel.add(this.tableWidthField, GridBagConstraints().apply {
            gridx = 1
            gridy = 1
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })

        return panel
    }
}