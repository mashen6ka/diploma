import java.awt.Dimension

import java.awt.Color;
import java.awt.GridBagLayout
import java.awt.GridBagConstraints
import javax.swing.*
import javax.swing.border.TitledBorder

fun main() {
//    var req = Request(10)
//    println(req.timeIn)

//    val graph = mxGraph()
//    val parent = graph.getDefaultParent();
//
//    val source1 = graph.insertVertex(parent, null, "Source Vertex", 0.0, 0.0, 100.0, 50.0)
//    val source2 = graph.insertVertex(parent, null, "Source Vertex", 0.0, 0.0, 100.0, 50.0)
//    val target = graph.insertVertex(parent, null, "Target Vertex", 0.0, 0.0, 100.0, 50.0)
//    graph.insertEdge(parent, null, "", source1, target)
//    graph.insertEdge(parent, null, "", source2, target)

//    val layout = mxHierarchicalLayout(graph)
//    layout.execute(graph.defaultParent)
//
//    val component = mxGraphComponent(graph)
//    component.isEnabled = false
//
//    val frame = JFrame("Graph")
//    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
//    frame.add(component)
//    val button = JButton("Click me!")
//    frame.add(button, BorderLayout.NORTH)
//    button.addActionListener {
//        graph.removeCells(graph.getChildCells(graph.defaultParent))
//        graph.refresh()
//        component.repaint()
//    }

//    frame.contentPane.add(component)
//    frame.pack()
//    frame.size = Dimension(500, 500)
//    frame.isVisible = true


//    val textArea = JTextArea("Hello, Kotlin/Swing world")
//    val scrollPane = JScrollPane(textArea)



    val frame = JFrame("Combined modelling")
    var layout = GridBagLayout()
    var constraints = GridBagConstraints()
    frame.layout = layout

    var labelTime = JLabel("Время моделирования: ")
    constraints.gridy = 0
    constraints.gridx = 0
    frame.add(labelTime, constraints)

    var entryTime = JTextField("", 16)
    constraints.gridy = 0
    constraints.gridx = 1
    frame.add(entryTime, constraints)


    val data = arrayOf(
        arrayOf("1", "GEN1"),
        arrayOf("2", "GEN2"),
        arrayOf("3", "GEN3"),
    )
    val header = arrayOf("№", "Name")
    var panel = JPanel()
    panel.border = TitledBorder("Generators")
    var tableGen = JTable(data, header)

    tableGen.setShowGrid(false);
//    tableGen.showHorizontalLines = true
//    tableGen.showVerticalLines = true

//    tableGen.setShowHorizontalLines(true);

    tableGen.setGridColor(Color.gray);

    constraints.gridy = 1
    constraints.gridx = 0
    constraints.gridwidth = 2
    constraints.fill = GridBagConstraints.HORIZONTAL
    var scrollPane = JScrollPane(tableGen)
    panel.add(scrollPane)
    frame.add(panel, constraints)



//    frame.getContentPane().add(scrollPane, BorderLayout.CENTER)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setSize(Dimension(600, 400))
    frame.setLocationRelativeTo(null)
    frame.setVisible(true)
}
