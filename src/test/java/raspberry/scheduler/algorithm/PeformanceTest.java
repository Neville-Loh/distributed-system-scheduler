package raspberry.scheduler.algorithm;

import raspberry.scheduler.algorithm.astar.AStar;
import raspberry.scheduler.algorithm.astar.WeightedAStar;
import raspberry.scheduler.algorithm.common.OutputSchedule;
import raspberry.scheduler.algorithm.common.OutputChecker;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;
import raspberry.scheduler.io.GraphReader;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PeformanceTest {
    
    private String INPUT_PATH = "src/test/resources/input/";
    

    
    public void testNodes16_2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        OutputSchedule output = readAndFindPath("16_466.dot", 2);
        assertEquals(624, output.getFinishTime());
    }

    
    public void testNodes16_5Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        OutputSchedule output = readAndFindPath("16_466.dot", 5);
        assertEquals(466, output.getFinishTime());
    }

   public void testNodesBIG_2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
       // read input graph and find path
       OutputSchedule output = readAndFindPath("big.dot", 2);
       assertEquals(92, output.getFinishTime());
   }

   public void testNodesBIG_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
       // read input graph and find path
       OutputSchedule output = readAndFindPath("big.dot", 5);
       assertEquals(227, output.getFinishTime());
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
    private OutputSchedule readAndFindPath(String filename, int numProcessors) throws
            FileNotFoundException, EdgeDoesNotExistException {

//        // read graph
//        GraphReader reader = new GraphReader(INPUT_PATH+ filename);
//        IGraph graph = reader.read();
//
//        // run and time a* algorithm
//        long startTime = System.nanoTime();
//        WeightedAstar wA = new WeightedAstar(graph,numProcessors);
//        OutputSchedule outputBound = wA.findPath();
//        wA = null;
//        int upperbound = outputBound.getFinishTime();
//        outputBound = null;
//        System.out.printf("UPPERBOUND : %d", upperbound);
//
//        //Astar astar = new Astar(graph,numProcessors,upperbound);
//        MemoryBoundAStar astar = new MemoryBoundAStar(graph,numProcessors,3000);
//        OutputSchedule output = astar.findPath();
//        System.out.printf("------------------------\n" +
//                        "File: %s, Number of Processor: %d \nRUNNING TIME : %.2f seconds\n",
//                filename, numProcessors, (System.nanoTime() - startTime) / 1000000000.0);
//
//        // check if output violate any dependency
//        if (!OutputChecker.isValid(graph,output)){
//            fail("Schedule is not valid");
//        }
        GraphReader reader = new GraphReader(INPUT_PATH+ filename);
        IGraph graph = reader.read();

        // run and time a* algorithm (seeker weighted a* routine)
        long startTime = System.nanoTime();
        WeightedAStar wA = new WeightedAStar(graph,numProcessors);
        OutputSchedule outputBound = wA.findPath();
        int upperbound = outputBound.getFinishTime();
        wA = null;
        outputBound = null;

        // run a star
        AStar astar = new AStar(graph,numProcessors, upperbound);
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
