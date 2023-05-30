package gui

import time.DurationGenerator
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

    private var addButton: JButton? = null
    private var deleteButton: JButton? = null
    private var updateButton: JButton? = null

    init {
        this.jpanel = JPanel(GridBagLayout())
        this.jpanel.setBorder(BorderFactory.createTitledBorder("${this.blockName}:"))

        this.jlist = createBlockList()
        val scroll = JScrollPane(this.jlist)

        this.addButton = JButton("+")
        this.deleteButton = JButton("-")
        this.updateButton = JButton("ред.")

        this.jpanel.add(scroll, GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            gridwidth = 3
            weightx = 1.0
            weighty = 1.0
            insets = Insets(0, 5, 0, 5)
            fill = GridBagConstraints.HORIZONTAL
        })

        this.jpanel.add(this.addButton, GridBagConstraints().apply {
            gridx = 0
            gridy = 1
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })

        this.jpanel.add(this.deleteButton, GridBagConstraints().apply {
            gridx = 1
            gridy = 1
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })

        this.jpanel.add(this.updateButton, GridBagConstraints().apply {
            gridx = 2
            gridy = 1
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })
    }

//    fun addSelectEvent(f: (selectedBlockInfo: BlockInfo, selectedIndex: Int) -> Any) {
//        var list = this.jlist
//        list!!.addMouseListener(object : java.awt.event.MouseAdapter() {
//            override fun mouseClicked(e: java.awt.event.MouseEvent) {
//                if (e.clickCount == 1) {
//                    val selectedValue = list.selectedValue
//                    val selectedIndex = list.selectedIndex
//                    if (selectedValue != null) {
//                        f(selectedValue, selectedIndex)
//                    }
//                }
//            }
//        })
//    }

    fun createAddButtonEvent(f: (selectedBlockInfo: BlockInfo?, selectedIndex: Int?) -> Any) {
        val list = this.jlist
        this.addButton!!.addActionListener {
            f(list!!.selectedValue, list.selectedIndex)
        }
    }

    fun createDeleteButtonEvent(f: (selectedBlockInfo: BlockInfo?, selectedIndex: Int?) -> Any) {
        val list = this.jlist
        this.deleteButton!!.addActionListener {
            f(list!!.selectedValue, list.selectedIndex)
        }
    }

    fun createUpdateButtonEvent(f: (selectedBlockInfo: BlockInfo?, selectedIndex: Int?) -> Any) {
        val list = this.jlist
        this.updateButton!!.addActionListener {
            f(list!!.selectedValue, list.selectedIndex)
        }
    }

    fun addBlockInfo(durationGenerator: DurationGenerator, receiversInfo: List<ProcessorInfo>?): Int {
        val index = this.blockIndex
        this.blockIndex++
        this.jmodel.addElement(createBlockInfo(index, durationGenerator, receiversInfo))

        return index
    }

    fun deleteBlockInfo(index: Int) {
        this.jmodel.remove(index)
    }

    fun updateBlockInfo(index: Int, durationGenerator: DurationGenerator, receiversInfo: List<ProcessorInfo>?) {
        val old = this.jmodel.get(index)
        old.update(durationGenerator, receiversInfo)
        this.jmodel.set(index, old)
    }

    fun getBlocksInfo(): List<T> {
        return this.jmodel.elements().toList() as List<T>
    }

    private fun createBlockInfo(index: Int, durationGenerator: DurationGenerator, receiversInfo: List<ProcessorInfo>?): BlockInfo {
        return if (this.blockType == GeneratorInfo::class.java)
            GeneratorInfo(index, durationGenerator, receiversInfo)
        else
            ProcessorInfo(index, durationGenerator, receiversInfo)
    }

    private fun createBlockList(): JList<BlockInfo> {
        this.jmodel = DefaultListModel<BlockInfo>()

        val list = JList(jmodel)
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        list.setVisibleRowCount(10)

        val renderer = DefaultListCellRenderer()
        renderer.horizontalAlignment = JLabel.CENTER
        list.cellRenderer = renderer

        return list
    }
}