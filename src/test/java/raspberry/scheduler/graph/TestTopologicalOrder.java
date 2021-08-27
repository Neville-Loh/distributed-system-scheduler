package raspberry.scheduler.graph;

import org.junit.Test;
import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;
import raspberry.scheduler.graph.util.TopologicalOrder;
import raspberry.scheduler.io.GraphReader;

import java.io.FileNotFoundException;

public class TestTopologicalOrder {
    private String INPUT_PATH = "src/test/resources/input/";



    /**
     *
     * @throws FileNotFoundException file does not exist
     */
    @Test
    public void testNodes9SeriesParallel() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        readAndTest("Nodes_9_SeriesParallel.dot");
    }


    /**
     *
     * @throws FileNotFoundException file does not exist
     */
    @Test
    public void testNodes7OutTree4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        readAndTest("Nodes_7_OutTree.dot");
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


}
