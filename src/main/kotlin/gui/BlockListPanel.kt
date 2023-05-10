package gui

import simulator.Block
import time.DurationGenerator
import time.UniformDurationGenerator
import java.awt.*
import javax.swing.*

class BlockListPanel<T: BlockInfo>(
    private val blockName: String,
    private val blockType: Class<T>
) {
    var jpanel: JPanel = JPanel()
        private set
    private var jlist: JList<BlockInfo>? = null
    private var jmodel: DefaultListModel<BlockInfo> = DefaultListModel<BlockInfo>()
    private var blockIndex: Int = 0

    init {
        this.jpanel = JPanel(GridBagLayout())
        this.jpanel.setBorder(BorderFactory.createTitledBorder("${this.blockName}:"))

        this.jlist = createBlockList()
        var scroll = JScrollPane(this.jlist)

        var addButton = JButton("+")
        addButton.addActionListener {
            addBlockInfo(UniformDurationGenerator(1, 10), null)
        }

        var delButton = JButton("-")
        delButton.addActionListener {
            val selected = this.jlist!!.selectedValue
            if (selected != null)
                deleteBlockInfo(this.jlist!!.selectedIndex)
        }

        this.jpanel.add(scroll, GridBagConstraints().apply {
            gridwidth = 2
            weightx = 1.0
            weighty = 1.0
            insets = Insets(0, 5, 0, 5)
            fill = GridBagConstraints.HORIZONTAL
        })

        this.jpanel.add(addButton, GridBagConstraints().apply {
            gridx = 0
            gridy = 1
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })

        this.jpanel.add(delButton, GridBagConstraints().apply {
            gridx = 1
            gridy = 1
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })
    }

    fun addSelectEvent(f: (selectedBlockInfo: BlockInfo, selectedIndex: Int) -> Any) {
        var list = this.jlist
        list!!.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                if (e.clickCount == 1) {
                    val selectedValue = list.selectedValue
                    val selectedIndex = list.selectedIndex
                    if (selectedValue != null) {
                        f(selectedValue, selectedIndex)
                    }
                }
            }
        })
    }

    fun addBlockInfo(durationGenerator: DurationGenerator, receiversInfo: List<ProcessorInfo>?): Int {
        val index = this.blockIndex
        this.blockIndex++
        this.jmodel.addElement(createBlockInfo(index, durationGenerator, receiversInfo))

        return index
    }

    fun deleteBlockInfo(index: Int) {
        this.jmodel!!.remove(index)
    }

    fun updateBlockInfo(index: Int, durationGenerator: DurationGenerator, receiversInfo: List<ProcessorInfo>?) {
        val old = this.jmodel.get(index)
        this.jmodel.set(index, createBlockInfo(old.getIndex(), durationGenerator, receiversInfo))
    }

    fun getBlocksInfo(): List<T> {
        return this.jmodel.elements().toList() as List<T>
    }

    private fun createBlockInfo(index: Int, durationGenerator: DurationGenerator, receiversInfo: List<ProcessorInfo>?): BlockInfo {
        if (this.blockType == GeneratorInfo::class.java)
            return  GeneratorInfo(index, durationGenerator, receiversInfo)
        else
            return ProcessorInfo(index, durationGenerator, receiversInfo)
    }

    private fun createBlockList(): JList<BlockInfo> {
        this.jmodel = DefaultListModel<BlockInfo>()

        val list = JList(jmodel)
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        list.setVisibleRowCount(5)

        val renderer = DefaultListCellRenderer()
        renderer.horizontalAlignment = JLabel.CENTER
        list.cellRenderer = renderer

        return list
    }
}