package raspberry.scheduler.graph;

import org.junit.Before;
import org.junit.Test;
import raspberry.scheduler.graph.adjacencylist.Graph;
import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;

import java.util.ArrayList;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * This class tests the graph class.
 * @author: Jonathon
 */
public class TestAdjacencyListGraph {

    private Graph _testGraph;

    /**
     * Set up test graph for other tests.
     */
    @Before
    public void GraphTestSetUp() {
        _testGraph = new Graph("testGraph");
        _testGraph.addNode("1", 12);
        _testGraph.addNode("2", 11);
        _testGraph.addNode("3", 10);
        _testGraph.addNode("4", 9);
        _testGraph.addNode("5", 8);
        _testGraph.addNode("6", 7);

        _testGraph.addEdge("1","2",1);
        _testGraph.addEdge("1","3",2);
        _testGraph.addEdge("2","4",3);
        _testGraph.addEdge("3","5",4);
        _testGraph.addEdge("3","6",5);
        _testGraph.addEdge("2","6",6);
    }

    /**
     * Test if there are illegal nodes (no weight is specified)
     */
    @Test
    public void testNoWeightSpecified() {
        for (INode node : _testGraph.getAllNodes()) {
            if (0 == node.getValue()){
                System.out.println("A node has been found with a weight of 0.");
                fail();
            }
        }
    }

    /**
     * Test the getEdgeWeight method's EdgeDoesNotExistException
     */
    @Test (expected = EdgeDoesNotExistException.class)
    public void testGetEdgeWeightException() throws EdgeDoesNotExistException {
        _testGraph.getEdgeWeight(
                    _testGraph.getNode("1"), _testGraph.getNode("6"));
    }

    /**
     * Test if there are illegal edges. Does not exist.
     */
    @Test
    public void testIllegalEdges() {
        assertEquals( null , _testGraph.getOutgoingEdges("10"));
    }

    /**
     * Test the toString method.
     */
    @Test
    public void testToString() {
        assertEquals("Graph: testGraph\n" +
                "Node:6 cost=7 []\n" +
                "Node:5 cost=8 []\n" +
                "Node:4 cost=9 []\n" +
                "Node:3 cost=10 [(pointsto=5, weight=4), (pointsto=6, weight=5)]\n" +
                "Node:2 cost=11 [(pointsto=4, weight=3), (pointsto=6, weight=6)]\n" +
                "Node:1 cost=12 [(pointsto=2, weight=1), (pointsto=3, weight=2)]\n", _testGraph.toString());
    }

    /**
     * Test the getNodesWithNoInDegree method.
     */
    @Test
    public void TestGetNodesWithNoInDegree() {
        ArrayList<INode> node1 = new ArrayList<INode>();
        node1.add(_testGraph.getNode("1"));
        assertEquals(node1, _testGraph.getNodesWithNoInDegree());
    }

    /**
     * Test the getInDegreeCountOfAllNodes method.
     */
    @Test
    public void TestGetInDegreeCountOfAllNodes() {
        Hashtable<INode, Integer> inCount = new Hashtable<INode, Integer>();
        inCount.put(_testGraph.getNode("6"), 2);
        inCount.put(_testGraph.getNode("5"), 1);
        inCount.put(_testGraph.getNode("4"), 1);
        inCount.put(_testGraph.getNode("3"), 1);
        inCount.put(_testGraph.getNode("2"), 1);
        inCount.put(_testGraph.getNode("1"), 0);
        assertEquals(inCount, _testGraph.getInDegreeCountOfAllNodes());
    }

    /**
     * Test the getCriticalPathWeightTable method.
     */
    @Test
    public void TestGetCriticalPathWeightTable() {
        Hashtable<INode, Integer> criticalCount = new Hashtable<INode, Integer>();
        criticalCount.put(_testGraph.getNode("6"), 0);
        criticalCount.put(_testGraph.getNode("5"), 0);
        criticalCount.put(_testGraph.getNode("4"), 0);
        criticalCount.put(_testGraph.getNode("3"), 8);
        criticalCount.put(_testGraph.getNode("2"), 9);
        criticalCount.put(_testGraph.getNode("1"), 20);
        assertEquals(criticalCount, _testGraph.getCriticalPathWeightTable());
    }


}
