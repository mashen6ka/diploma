import simulator.Generator
import simulator.Processor
import simulator.TimeBasedSimulator
import time.UniformDurationGenerator

fun main() {
//    val durationGenerator = UniformDurationGenerator(1, 10)
    val processor = Processor(UniformDurationGenerator(10, 100))
    val generator = Generator(UniformDurationGenerator(1, 10), listOf(processor))
    val simulator = TimeBasedSimulator(listOf(generator), listOf(processor))

    println(simulator.simulate(100_000))
    println()
    println(generator.totalGenerationDuration / generator.totalRequests)
    println(generator.totalRequests)
    println()
    println(processor.totalRequests)
    println(processor.totalProcessingDuration / processor.totalRequests)
    println(processor.totalWaitingDuration / processor.totalRequests)
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
