package raspberry.scheduler.algorithm.astar;

import org.junit.Test;
import raspberry.scheduler.algorithm.common.OutputChecker;
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
 * @Author Neville
 */
public class FastTestAStar {
    // input path of the resource folder
    private String INPUT_PATH = "src/test/resources/input/";

    /*
     * ===========================================
     * 2 processor test
     *
     * ===========================================
     */
    /**
     * Test performance of A* algorithm and correctness of output
     * Name: Nodes_7_OutTree.dot
     * Expected total Time for schedule: 28
     * @throws FileNotFoundException file does not exist
     */
    @Test
    public void testNodes7OutTree2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        OutputSchedule output = readAndFindPath("Nodes_7_OutTree.dot", 2);
        assertEquals(28,output.getFinishTime());
    }

    /**
     * Test performance of A* algorithm and correctness of output
     * Name: Nodes_8_Random
     * Expected total Time for schedule: 571
     * @throws FileNotFoundException file does not exist
     */
    @Test
    public void testNodes8Random2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        OutputSchedule output = readAndFindPath("Nodes_8_Random.dot", 2);
        assertEquals(581,output.getFinishTime());
    }

    /**
     * Test performance of A* algorithm and correctness of output
     * Name: Nodes_9_SeriesParallel.dot
     * Expected total Time for schedule: 55
     * @throws FileNotFoundException file does not exist
     */
    @Test
    public void testNodes9SeriesParallel2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        OutputSchedule output = readAndFindPath("Nodes_9_SeriesParallel.dot", 2);
        assertEquals(55,output.getFinishTime());
    }

    /**
     * Test performance of A* algorithm and correctness of output
     * Name: Nodes_10_Random.dot
     * Expected total Time for schedule: 50
     * @throws FileNotFoundException file does not exist
     */
    @Test
    public void testNodes10Random2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        OutputSchedule output = readAndFindPath("Nodes_10_Random.dot", 2);
        assertEquals(50, output.getFinishTime());
    }

    /*
     * ===========================================
     * 4 processor test
     *
     * ===========================================
     */

    /**
     * Test performance of A* algorithm and correctness of output
     * Name: Nodes_7_OutTree.dot
     * Expected total Time for schedule: 22
     * @throws FileNotFoundException file does not exist
     */
    @Test
    public void testNodes7OutTree4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        OutputSchedule output = readAndFindPath("Nodes_7_OutTree.dot", 4);
        assertEquals(22,output.getFinishTime());
    }

    /**
     * Test performance of A* algorithm and correctness of output
     * Name: Nodes_8_Random
     * Expected total Time for schedule: 581
     * @throws FileNotFoundException file does not exists
     */
    @Test
    public void testNodes8Random4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        OutputSchedule output = readAndFindPath("Nodes_8_Random.dot", 4);
        assertEquals(581,output.getFinishTime());
    }

    /**
     * Test performance of A* algorithm and correctness of output
     * Name: Nodes_9_SeriesParallel.dot
     * Expected total Time for schedule: 55
     * @throws FileNotFoundException file does not exist
     */
    @Test
    public void testNodes9SeriesParallel4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        OutputSchedule output = readAndFindPath("Nodes_9_SeriesParallel.dot", 4);
        assertEquals(55,output.getFinishTime());
    }

    /**
     * Test performance of A* algorithm and correctness of output
     * Name: Nodes_10_Random.dot
     * Expected total Time for schedule: 50
     * @throws FileNotFoundException file does not exist
     */
    @Test
    public void testNodes10Random4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        OutputSchedule output = readAndFindPath("Nodes_10_Random.dot", 4);
        assertEquals(50, output.getFinishTime());
    }
    /**
     * Test performance of A* algorithm and correctness of output
     * Name: Nodes_11_OutTree.dot
     * ExpectSed total Time for schedule: 227
     * @throws FileNotFoundException file does not exist
     */
    @Test
    public void testNodes11OutTree4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        OutputSchedule output = readAndFindPath("Nodes_11_OutTree.dot", 4);
        assertEquals(227, output.getFinishTime());
    }

    /**
     * Test performance of A* algorithm and correctness of output
     * Name: 2p_Stencil_Nodes_10_CCR_1.97_WeightType_Random.dot
     * ExpectSed total Time for schedule: 57
     * @throws FileNotFoundException file does not exist
     */
    @Test
    public void test2p_Stencil_Nodes_10_CCR_197_WeightType_Randomwith_2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        // read input graph and find path
        OutputSchedule output = readAndFindPath("dotfiles/2p_Stencil_Nodes_10_CCR_1.97_WeightType_Random.dot", 2);
        assertEquals(57, output.getFinishTime());
    }

    /**
     * Test performance of A* algorithm and correctness of output
     * Name: 4p_Stencil_Nodes_10_CCR_1.97_WeightType_Random.dot
     * ExpectSed total Time for schedule: 57
     * @throws FileNotFoundException file does not exist
     */
    @Test
    public void test4p_Stencil_Nodes_10_CCR_197_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        OutputSchedule output = readAndFindPath("dotfiles/4p_Stencil_Nodes_10_CCR_1.97_WeightType_Random.dot", 4);
        assertEquals(57, output.getFinishTime());
    }


    /**
     * Helper method to read the file and run a star
     * with specified number of processor.
     * Do validity check upon finish
     *
     * @param filename filename of the dot file of dependency graph
     * @param numProcessors number of resource available to allocate to task
     * @return output schedule
     * @throws FileNotFoundException if file does not exist
     * @throws EdgeDoesNotExistException if get edges yield error
     */
    private OutputSchedule readAndFindPath(String filename, int numProcessors) throws
            FileNotFoundException, EdgeDoesNotExistException {

        // read graph
        GraphReader reader = new GraphReader(INPUT_PATH+ filename);
        IGraph graph = reader.read();

        // run and time a* algorithm (seeker weighted a* routine)
        long startTime = System.nanoTime();

        // run a star
        AStar astar = new AStar(graph,numProcessors,Integer.MAX_VALUE);
        OutputSchedule output = astar.findPath();

        System.out.printf("------------------------\n" +
                        "File: %s, Number of Processor: %d \nRUNNING TIME : %.2f seconds\n",
                filename, numProcessors, (System.nanoTime() - startTime) / 1000000000.0);

        // check if output violate any dependency
        if (!OutputChecker.isValid(graph,output)){
            fail("Schedule is not valid");
        }

        return output;
    }
}
