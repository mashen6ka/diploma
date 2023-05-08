import simulator.Generator
import simulator.HybridSimulator
import simulator.Processor
import simulator.TimeBasedSimulator
import time.UniformDurationGenerator

fun main() {
    val processor = Processor(UniformDurationGenerator(10, 100))
    val generator = Generator(UniformDurationGenerator(1, 10), arrayOf(processor))
    val simulator = HybridSimulator(arrayOf(generator), arrayOf(processor), 15, 10)
    println(simulator.simulate(100_000))
}


//import com.mxgraph.layout.hierarchical.mxHierarchicalLayout
//import com.mxgraph.swing.mxGraphComponent
//import com.mxgraph.view.mxGraph
//import java.awt.BorderLayout
//import java.awt.Dimension
//import javax.swing.JButton
//import javax.swing.JFrame

//fun main() {
//    val graph = mxGraph()
//    val parent = graph.getDefaultParent();
//
//    val source1 = graph.insertVertex(parent, null, "Source Vertex", 0.0, 0.0, 100.0, 50.0)
//    val source2 = graph.insertVertex(parent, null, "Source Vertex", 0.0, 0.0, 100.0, 50.0)
//    val target = graph.insertVertex(parent, null, "Target Vertex", 0.0, 0.0, 100.0, 50.0)
//    graph.insertEdge(parent, null, "", source1, target)
//    graph.insertEdge(parent, null, "", source2, target)
//
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
//
//    frame.pack()
//    frame.size = Dimension(500, 500)
//    frame.isVisible = true
//}
