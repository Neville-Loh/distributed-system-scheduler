package raspberry.scheduler.graph;

import org.junit.Test;
import raspberry.scheduler.graph.adjacencylist.Graph;
import raspberry.scheduler.graph.adjacencylist.Node;
import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;
import raspberry.scheduler.graph.util.TopologicalOrder;
import raspberry.scheduler.io.GraphReader;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestTopologicalOrder {
    private String INPUT_PATH = "src/test/resources/input/";
    private Graph _testTopGraph;
    private TopologicalOrder _testTopOrder;


    /**
     * Manually testing the computerOrder Method in TopologicalOrder by creating a graph directly.
     */
    @Test
    public void manualTestComputeOrder() {

        //Setup graph to insert topological order.
        _testTopGraph = new Graph("testTopGraph");
        _testTopGraph.addNode("A", 2);
        _testTopGraph.addNode("B", 2);
        _testTopGraph.addNode("C", 2);
        _testTopGraph.addNode("D", 3);
        _testTopGraph.addNode("E", 2);
        _testTopGraph.addNode("F", 3);
        _testTopGraph.addNode("G", 2);

        _testTopGraph.addEdge("A","B",1);
        _testTopGraph.addEdge("A","C",3);
        _testTopGraph.addEdge("A","D",1);
        _testTopGraph.addEdge("B","E",3);
        _testTopGraph.addEdge("B","G",4);
        _testTopGraph.addEdge("C","F",1);
        _testTopGraph.addEdge("D","F",1);
        _testTopGraph.addEdge("E","G",2);
        _testTopGraph.addEdge("F","G",2);

        //Assertion
        assertEquals(0, _testTopGraph.getIndex(_testTopGraph.getNode("A")));
        assertEquals(1, _testTopGraph.getIndex(_testTopGraph.getNode("B")));
        assertEquals(2, _testTopGraph.getIndex(_testTopGraph.getNode("C")));
        assertEquals(3, _testTopGraph.getIndex(_testTopGraph.getNode("D")));
        assertEquals(4, _testTopGraph.getIndex(_testTopGraph.getNode("E")));
        assertEquals(5, _testTopGraph.getIndex(_testTopGraph.getNode("F")));
        assertEquals(6, _testTopGraph.getIndex(_testTopGraph.getNode("G")));
    }

    /**
     *
     * @throws FileNotFoundException file does not exist
     */
    @Test
    public void testNodes9SeriesParallel() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        IGraph graph = read("Nodes_9_SeriesParallel.dot");
        //Assertion
        assertEquals(0, graph.getIndex(graph.getNode("0")));
        assertEquals(1, graph.getIndex(graph.getNode("2")));
        assertEquals(2, graph.getIndex(graph.getNode("3")));
        assertEquals(3, graph.getIndex(graph.getNode("4")));
        assertEquals(4, graph.getIndex(graph.getNode("6")));
        assertEquals(5, graph.getIndex(graph.getNode("7")));
        assertEquals(6, graph.getIndex(graph.getNode("8")));
        assertEquals(7, graph.getIndex(graph.getNode("5")));
        assertEquals(8, graph.getIndex(graph.getNode("1")));
    }


    /**
     *
     * @throws FileNotFoundException file does not exist
     */
    @Test
    public void testNodes7OutTree4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        IGraph graph = read("Nodes_7_OutTree.dot");
        //Assertion
        assertEquals(0, graph.getIndex(graph.getNode("0")));
        assertEquals(1, graph.getIndex(graph.getNode("1")));
        assertEquals(2, graph.getIndex(graph.getNode("2")));
        assertEquals(3, graph.getIndex(graph.getNode("3")));
        assertEquals(4, graph.getIndex(graph.getNode("4")));
        assertEquals(5, graph.getIndex(graph.getNode("5")));
        assertEquals(6, graph.getIndex(graph.getNode("6")));
    }



    private void readAndTest(String filename) throws
            FileNotFoundException, EdgeDoesNotExistException {

        // read graph
        GraphReader reader = new GraphReader(INPUT_PATH+ filename);
        IGraph graph = reader.read();

        TopologicalOrder to = new TopologicalOrder(graph);
        to.computeOrder();
        to.printReport();
    }

    private IGraph read (String filename) throws
            FileNotFoundException, EdgeDoesNotExistException {

        // read graph
        GraphReader reader = new GraphReader(INPUT_PATH+ filename);
        return reader.read();
    }


}
