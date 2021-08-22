package raspberry.scheduler.algorithm;

import org.junit.Test;
import raspberry.scheduler.algorithm.astar.AstarParallel;
import raspberry.scheduler.algorithm.common.OutputSchedule;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;
import raspberry.scheduler.io.GraphReader;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Integrated test for A star algorithm
 * Test 5 graph in the resource folder with specified number of processor
 * output was given prior to the development
 * @Author Neville, Young
 */
public class TestParallelization {
    // input path of the resource folder
    private String INPUT_PATH = "src/test/resources/input/";

    /**
     * Test performance of A* algorithm and correctness of output
     * Name: Nodes_11_OutTree.dot
     * Expected total Time for schedule: 227
     * @throws FileNotFoundException file does not exists
     */
    @Test
    public void testNodes7OutTree2Core() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        OutputSchedule output = readAndFindPath("Nodes_7_OutTree.dot", 4, 2);
        assertEquals(22, output.getFinishTime());
    }

    /**
     * Test performance of A* algorithm and correctness of output
     * Name: Nodes_11_OutTree.dot
     * Expected total Time for schedule: 227
     * @throws FileNotFoundException file does not exists
     */
    @Test
    public void testNodes7OutTree3Core() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        OutputSchedule output = readAndFindPath("Nodes_7_OutTree.dot", 4, 3);
        assertEquals(22, output.getFinishTime());
    }

    /**
     * Test performance of A* algorithm and correctness of output
     * Name: Nodes_11_OutTree.dot
     * Expected total Time for schedule: 227
     * @throws FileNotFoundException file does not exists
     */
    @Test
    public void testNodes7OutTree4Core() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        OutputSchedule output = readAndFindPath("Nodes_7_OutTree.dot", 4, 4);
        assertEquals(22, output.getFinishTime());
    }

    /**
     * Test performance of A* algorithm and correctness of output
     * Name: Nodes_11_OutTree.dot
     * Expected total Time for schedule: 227
     * @throws FileNotFoundException file does not exists
     */
    @Test
    public void testNodes7OutTree5Core() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        OutputSchedule output = readAndFindPath("Nodes_7_OutTree.dot", 4, 5);
        assertEquals(22, output.getFinishTime());
    }

    /**
     * Test performance of A* algorithm and correctness of output
     * Name: Nodes_11_OutTree.dot
     * Expected total Time for schedule: 227
     * @throws FileNotFoundException file does not exists
     */
    @Test
    public void testNodes7OutTree6Core() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        OutputSchedule output = readAndFindPath("Nodes_7_OutTree.dot", 4, 6);
        assertEquals(22, output.getFinishTime());
    }

    /**
     * Helper method to read the file and run a star
     * with specified number of processor.
     * Do validity check upon finish
     *
     * @param filename filename of the dot file of dependency graph
     * @param numProcessors number of resource available to allocate to task
     * @return output schedule
     * @throws FileNotFoundException if file does not exists
     * @throws EdgeDoesNotExistException if get edges yield error
     */
    private OutputSchedule readAndFindPath(String filename, int numProcessors, int numCore) throws
            FileNotFoundException, EdgeDoesNotExistException {

        // read graph
        GraphReader reader = new GraphReader(INPUT_PATH+ filename);
        IGraph graph = reader.read();

        // run and time a* algorithm
        long startTime = System.nanoTime();
//        Astar astar = new Astar(graph,numProcessors);
        AstarParallel astar = new AstarParallel(graph,numProcessors, numCore);
        OutputSchedule output = astar.findPath();
        System.out.printf("------------------------\n" +
                        "File: %s, Number of Processor: %d \nNumber of cores: %d \nRUNNING TIME : %.2f seconds\n ",
                filename, numProcessors, numCore, (System.nanoTime() - startTime) / 1000000000.0);

        // check if output violate any dependency
        if (!OutputChecker.isValid(graph,output)){
            fail("Schedule is not valid");
        }

        return output;
    }
}
