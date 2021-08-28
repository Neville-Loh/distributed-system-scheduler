package raspberry.scheduler.algorithm.astar;

import org.junit.Test;
import raspberry.scheduler.algorithm.bNb.BNBParallel;
import raspberry.scheduler.algorithm.common.OutputSchedule;
import raspberry.scheduler.algorithm.OutputChecker;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;
import raspberry.scheduler.io.GraphReader;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestComprehensiveAstar {
    private String INPUT_PATH = "src/test/resources/input/dotfiles/";

    /**
     * Helper method to read the file and run a star
     * with specified number of processor.
     * Do validity check upon finish
     * @param filename filename of the dot file of dependency graph
     * @param numProcessors number of resource available to allocate to task
     * @return output schedule
     * @throws FileNotFoundException if file does not exists
     * @throws EdgeDoesNotExistException if get edges yield error
     */
    private int readAndFindFinishTime(String filename, int numProcessors) throws
            FileNotFoundException, EdgeDoesNotExistException {

        // read graph
        GraphReader reader = new GraphReader(INPUT_PATH+ filename);
        IGraph graph = reader.read();

        // run and time a* algorithm (seeker weighted a* routine)
        long startTime = System.nanoTime();
        WeightedAstar wA = new WeightedAstar(graph,numProcessors);
        OutputSchedule outputBound = wA.findPath();
        int upperbound = outputBound.getFinishTime();
        wA = null;
        outputBound = null;

        // run a star
        Astar astar = new Astar(graph,numProcessors, upperbound);
        OutputSchedule output = astar.findPath();

        System.out.printf("------------------------\n" +
                        "File: %s, Number of Processor: %d \nRUNNING TIME : %.2f seconds\n",
                filename, numProcessors, (System.nanoTime() - startTime) / 1000000000.0);

        // check if output violate any dependency
        if (!OutputChecker.isValid(graph,output)){
            fail("Schedule is not valid");
        }

//        // read graph
//        GraphReader reader = new GraphReader(INPUT_PATH+ filename);
//        IGraph graph = reader.read();
//
//        // run and time a* algorithm
//        long startTime = System.nanoTime();
//
//
//        BNBParallel bnb = new BNBParallel(graph,numProcessors, Integer.MAX_VALUE, 5);
//        OutputSchedule output = bnb.findPath();
//        System.out.printf("------------------------\n" +
//                        "File: %s, Number of Processor: %d \nRUNNING TIME : %.2f seconds\n",
//                filename, numProcessors, (System.nanoTime() - startTime) / 1000000000.0);
//
//        // check if output violate any dependency
//        if (!OutputChecker.isValid(graph,output)){
//            fail("Schedule is not valid");
//        }

        return output.getFinishTime();
    }

    @Test
    public void testNodes7OutTree2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(895,
                readAndFindFinishTime("8p_Random_Nodes_10_Density_2.30_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test2() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(341,
                readAndFindFinishTime("4p_Random_Nodes_10_Density_1.30_CCR_0.10_WeightType_Random.dot", 4));
    }
    @Test
    public void test2p_SeriesParallelMaxBf4_Nodes_10_CCR_205_WeightType_Randomwith_2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(49,
                readAndFindFinishTime("2p_SeriesParallel-MaxBf-4_Nodes_10_CCR_2.05_WeightType_Random.dot", 2));
    }

    @Test
    public void test2p_SeriesParallelMaxBf5_Nodes_10_CCR_010_WeightType_Randomwith_2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(579,
                readAndFindFinishTime("2p_SeriesParallel-MaxBf-5_Nodes_10_CCR_0.10_WeightType_Random.dot", 2));
    }

    @Test
    public void test2p_SeriesParallelMaxBf5_Nodes_10_CCR_100_WeightType_Randomwith_2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(45,
                readAndFindFinishTime("2p_SeriesParallel-MaxBf-5_Nodes_10_CCR_1.00_WeightType_Random.dot", 2));
    }

    @Test
    public void test2p_SeriesParallelMaxBf5_Nodes_10_CCR_199_WeightType_Randomwith_2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(57,
                readAndFindFinishTime("2p_SeriesParallel-MaxBf-5_Nodes_10_CCR_1.99_WeightType_Random.dot", 2));
    }

    @Test
    public void test2p_SeriesParallelMaxBf5_Nodes_10_CCR_997_WeightType_Randomwith_2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(59,
                readAndFindFinishTime("2p_SeriesParallel-MaxBf-5_Nodes_10_CCR_9.97_WeightType_Random.dot", 2));
    }

    @Test
    public void test2p_Stencil_Nodes_10_CCR_010_WeightType_Randomwith_2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(450,
                readAndFindFinishTime("2p_Stencil_Nodes_10_CCR_0.10_WeightType_Random.dot", 2));
    }

    @Test
    public void test2p_Stencil_Nodes_10_CCR_101_WeightType_Randomwith_2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(77,
                readAndFindFinishTime("2p_Stencil_Nodes_10_CCR_1.01_WeightType_Random.dot", 2));
    }

    @Test
    public void test2p_Stencil_Nodes_10_CCR_197_WeightType_Randomwith_2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(57,
                readAndFindFinishTime("2p_Stencil_Nodes_10_CCR_1.97_WeightType_Random.dot", 2));
    }

    @Test
    public void test2p_Stencil_Nodes_10_CCR_998_WeightType_Randomwith_2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(46,
                readAndFindFinishTime("2p_Stencil_Nodes_10_CCR_9.98_WeightType_Random.dot", 2));
    }

    @Test
    public void test4p_Fork_Join_Nodes_10_CCR_010_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(342,
                readAndFindFinishTime("4p_Fork_Join_Nodes_10_CCR_0.10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Fork_Join_Nodes_10_CCR_101_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(45,
                readAndFindFinishTime("4p_Fork_Join_Nodes_10_CCR_1.01_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Fork_Join_Nodes_10_CCR_184_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(33,
                readAndFindFinishTime("4p_Fork_Join_Nodes_10_CCR_1.84_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Fork_Join_Nodes_10_CCR_1001_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(69,
                readAndFindFinishTime("4p_Fork_Join_Nodes_10_CCR_10.01_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Fork_Nodes_10_CCR_010_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(204,
                readAndFindFinishTime("4p_Fork_Nodes_10_CCR_0.10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Fork_Nodes_10_CCR_099_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(25,
                readAndFindFinishTime("4p_Fork_Nodes_10_CCR_0.99_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Fork_Nodes_10_CCR_197_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(34,
                readAndFindFinishTime("4p_Fork_Nodes_10_CCR_1.97_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Fork_Nodes_10_CCR_1000_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(47,
                readAndFindFinishTime("4p_Fork_Nodes_10_CCR_10.00_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_InTreeBalancedMaxBf3_Nodes_10_CCR_010_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(144,
                readAndFindFinishTime("4p_InTree-Balanced-MaxBf-3_Nodes_10_CCR_0.10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_InTreeBalancedMaxBf3_Nodes_10_CCR_104_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(21,
                readAndFindFinishTime("4p_InTree-Balanced-MaxBf-3_Nodes_10_CCR_1.04_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_InTreeBalancedMaxBf3_Nodes_10_CCR_1000_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(56,
                readAndFindFinishTime("4p_InTree-Balanced-MaxBf-3_Nodes_10_CCR_10.00_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_InTreeBalancedMaxBf3_Nodes_10_CCR_202_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(36,
                readAndFindFinishTime("4p_InTree-Balanced-MaxBf-3_Nodes_10_CCR_2.02_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_InTreeUnbalancedMaxBf3_Nodes_10_CCR_010_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(278,
                readAndFindFinishTime("4p_InTree-Unbalanced-MaxBf-3_Nodes_10_CCR_0.10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_InTreeUnbalancedMaxBf3_Nodes_10_CCR_102_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(38,
                readAndFindFinishTime("4p_InTree-Unbalanced-MaxBf-3_Nodes_10_CCR_1.02_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_InTreeUnbalancedMaxBf3_Nodes_10_CCR_1000_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(56,
                readAndFindFinishTime("4p_InTree-Unbalanced-MaxBf-3_Nodes_10_CCR_10.00_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_InTreeUnbalancedMaxBf3_Nodes_10_CCR_202_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(34,
                readAndFindFinishTime("4p_InTree-Unbalanced-MaxBf-3_Nodes_10_CCR_2.02_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Independent_Nodes_10_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(16,
                readAndFindFinishTime("4p_Independent_Nodes_10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Join_Nodes_10_CCR_010_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(178,
                readAndFindFinishTime("4p_Join_Nodes_10_CCR_0.10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Join_Nodes_10_CCR_100_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(26,
                readAndFindFinishTime("4p_Join_Nodes_10_CCR_1.00_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Join_Nodes_10_CCR_1007_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(52,
                readAndFindFinishTime("4p_Join_Nodes_10_CCR_10.07_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Join_Nodes_10_CCR_200_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(27,
                readAndFindFinishTime("4p_Join_Nodes_10_CCR_2.00_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_OutTreeBalancedMaxBf3_Nodes_10_CCR_010_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(206,
                readAndFindFinishTime("4p_OutTree-Balanced-MaxBf-3_Nodes_10_CCR_0.10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_OutTreeBalancedMaxBf3_Nodes_10_CCR_093_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(22,
                readAndFindFinishTime("4p_OutTree-Balanced-MaxBf-3_Nodes_10_CCR_0.93_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_OutTreeBalancedMaxBf3_Nodes_10_CCR_197_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(35,
                readAndFindFinishTime("4p_OutTree-Balanced-MaxBf-3_Nodes_10_CCR_1.97_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_OutTreeBalancedMaxBf3_Nodes_10_CCR_1000_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(35,
                readAndFindFinishTime("4p_OutTree-Balanced-MaxBf-3_Nodes_10_CCR_10.00_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_OutTreeUnbalancedMaxBf3_Nodes_10_CCR_010_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(280,
                readAndFindFinishTime("4p_OutTree-Unbalanced-MaxBf-3_Nodes_10_CCR_0.10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_OutTreeUnbalancedMaxBf3_Nodes_10_CCR_109_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(32,
                readAndFindFinishTime("4p_OutTree-Unbalanced-MaxBf-3_Nodes_10_CCR_1.09_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_OutTreeUnbalancedMaxBf3_Nodes_10_CCR_196_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(26,
                readAndFindFinishTime("4p_OutTree-Unbalanced-MaxBf-3_Nodes_10_CCR_1.96_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_OutTreeUnbalancedMaxBf3_Nodes_10_CCR_1001_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(71,
                readAndFindFinishTime("4p_OutTree-Unbalanced-MaxBf-3_Nodes_10_CCR_10.01_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Pipeline_Nodes_10_CCR_010_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(481,
                readAndFindFinishTime("4p_Pipeline_Nodes_10_CCR_0.10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Pipeline_Nodes_10_CCR_100_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(66,
                readAndFindFinishTime("4p_Pipeline_Nodes_10_CCR_1.00_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Pipeline_Nodes_10_CCR_197_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(53,
                readAndFindFinishTime("4p_Pipeline_Nodes_10_CCR_1.97_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Pipeline_Nodes_10_CCR_1000_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(50,
                readAndFindFinishTime("4p_Pipeline_Nodes_10_CCR_10.00_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Random_Nodes_10_Density_020_CCR_100_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(17,
                readAndFindFinishTime("4p_Random_Nodes_10_Density_0.20_CCR_1.00_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Random_Nodes_10_Density_040_CCR_1000_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(17,
                readAndFindFinishTime("4p_Random_Nodes_10_Density_0.40_CCR_10.00_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Random_Nodes_10_Density_040_CCR_1002_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(18,
                readAndFindFinishTime("4p_Random_Nodes_10_Density_0.40_CCR_10.02_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Random_Nodes_10_Density_050_CCR_010_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(98,
                readAndFindFinishTime("4p_Random_Nodes_10_Density_0.50_CCR_0.10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Random_Nodes_10_Density_060_CCR_102_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(18,
                readAndFindFinishTime("4p_Random_Nodes_10_Density_0.60_CCR_1.02_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Random_Nodes_10_Density_060_CCR_200_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(35,
                readAndFindFinishTime("4p_Random_Nodes_10_Density_0.60_CCR_2.00_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Random_Nodes_10_Density_130_CCR_010_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(341,
                readAndFindFinishTime("4p_Random_Nodes_10_Density_1.30_CCR_0.10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Random_Nodes_10_Density_140_CCR_185_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(32,
                readAndFindFinishTime("4p_Random_Nodes_10_Density_1.40_CCR_1.85_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Random_Nodes_10_Density_150_CCR_1000_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(57,
                readAndFindFinishTime("4p_Random_Nodes_10_Density_1.50_CCR_10.00_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Random_Nodes_10_Density_150_CCR_203_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(34,
                readAndFindFinishTime("4p_Random_Nodes_10_Density_1.50_CCR_2.03_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Random_Nodes_10_Density_200_CCR_102_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(79,
                readAndFindFinishTime("4p_Random_Nodes_10_Density_2.00_CCR_1.02_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Random_Nodes_10_Density_230_CCR_010_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(895,
                readAndFindFinishTime("4p_Random_Nodes_10_Density_2.30_CCR_0.10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Random_Nodes_10_Density_450_CCR_010_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(2680,
                readAndFindFinishTime("4p_Random_Nodes_10_Density_4.50_CCR_0.10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Random_Nodes_10_Density_450_CCR_099_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(274,
                readAndFindFinishTime("4p_Random_Nodes_10_Density_4.50_CCR_0.99_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Random_Nodes_10_Density_450_CCR_1000_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(66,
                readAndFindFinishTime("4p_Random_Nodes_10_Density_4.50_CCR_10.00_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Random_Nodes_10_Density_450_CCR_200_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(126,
                readAndFindFinishTime("4p_Random_Nodes_10_Density_4.50_CCR_2.00_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_SeriesParallelMaxBf2_Nodes_10_CCR_010_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(494,
                readAndFindFinishTime("4p_SeriesParallel-MaxBf-2_Nodes_10_CCR_0.10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_SeriesParallelMaxBf2_Nodes_10_CCR_102_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(48,
                readAndFindFinishTime("4p_SeriesParallel-MaxBf-2_Nodes_10_CCR_1.02_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_SeriesParallelMaxBf2_Nodes_10_CCR_198_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(55,
                readAndFindFinishTime("4p_SeriesParallel-MaxBf-2_Nodes_10_CCR_1.98_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_SeriesParallelMaxBf2_Nodes_10_CCR_1003_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(76,
                readAndFindFinishTime("4p_SeriesParallel-MaxBf-2_Nodes_10_CCR_10.03_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_SeriesParallelMaxBf3_Nodes_10_CCR_010_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(358,
                readAndFindFinishTime("4p_SeriesParallel-MaxBf-3_Nodes_10_CCR_0.10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_SeriesParallelMaxBf3_Nodes_10_CCR_101_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(47,
                readAndFindFinishTime("4p_SeriesParallel-MaxBf-3_Nodes_10_CCR_1.01_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_SeriesParallelMaxBf3_Nodes_10_CCR_198_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(50,
                readAndFindFinishTime("4p_SeriesParallel-MaxBf-3_Nodes_10_CCR_1.98_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_SeriesParallelMaxBf3_Nodes_10_CCR_1002_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(62,
                readAndFindFinishTime("4p_SeriesParallel-MaxBf-3_Nodes_10_CCR_10.02_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_SeriesParallelMaxBf4_Nodes_10_CCR_010_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(476,
                readAndFindFinishTime("4p_SeriesParallel-MaxBf-4_Nodes_10_CCR_0.10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_SeriesParallelMaxBf4_Nodes_10_CCR_097_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(51,
                readAndFindFinishTime("4p_SeriesParallel-MaxBf-4_Nodes_10_CCR_0.97_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_SeriesParallelMaxBf4_Nodes_10_CCR_1004_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(50,
                readAndFindFinishTime("4p_SeriesParallel-MaxBf-4_Nodes_10_CCR_10.04_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_SeriesParallelMaxBf4_Nodes_10_CCR_205_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(49,
                readAndFindFinishTime("4p_SeriesParallel-MaxBf-4_Nodes_10_CCR_2.05_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_SeriesParallelMaxBf5_Nodes_10_CCR_010_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(467,
                readAndFindFinishTime("4p_SeriesParallel-MaxBf-5_Nodes_10_CCR_0.10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_SeriesParallelMaxBf5_Nodes_10_CCR_100_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(37,
                readAndFindFinishTime("4p_SeriesParallel-MaxBf-5_Nodes_10_CCR_1.00_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_SeriesParallelMaxBf5_Nodes_10_CCR_199_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(57,
                readAndFindFinishTime("4p_SeriesParallel-MaxBf-5_Nodes_10_CCR_1.99_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_SeriesParallelMaxBf5_Nodes_10_CCR_997_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(59,
                readAndFindFinishTime("4p_SeriesParallel-MaxBf-5_Nodes_10_CCR_9.97_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Stencil_Nodes_10_CCR_010_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(450,
                readAndFindFinishTime("4p_Stencil_Nodes_10_CCR_0.10_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Stencil_Nodes_10_CCR_101_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(77,
                readAndFindFinishTime("4p_Stencil_Nodes_10_CCR_1.01_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Stencil_Nodes_10_CCR_197_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(57,
                readAndFindFinishTime("4p_Stencil_Nodes_10_CCR_1.97_WeightType_Random.dot", 4));
    }

    @Test
    public void test4p_Stencil_Nodes_10_CCR_998_WeightType_Randomwith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(46,
                readAndFindFinishTime("4p_Stencil_Nodes_10_CCR_9.98_WeightType_Random.dot", 4));
    }

    @Test
    public void test8p_Fork_Join_Nodes_10_CCR_010_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(262,
                readAndFindFinishTime("8p_Fork_Join_Nodes_10_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Fork_Join_Nodes_10_CCR_101_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(44,
                readAndFindFinishTime("8p_Fork_Join_Nodes_10_CCR_1.01_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Fork_Join_Nodes_10_CCR_184_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(33,
                readAndFindFinishTime("8p_Fork_Join_Nodes_10_CCR_1.84_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Fork_Join_Nodes_10_CCR_1001_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(69,
                readAndFindFinishTime("8p_Fork_Join_Nodes_10_CCR_10.01_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Fork_Nodes_10_CCR_010_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(167,
                readAndFindFinishTime("8p_Fork_Nodes_10_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Fork_Nodes_10_CCR_099_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(20,
                readAndFindFinishTime("8p_Fork_Nodes_10_CCR_0.99_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Fork_Nodes_10_CCR_197_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(34,
                readAndFindFinishTime("8p_Fork_Nodes_10_CCR_1.97_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Fork_Nodes_10_CCR_1000_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(47,
                readAndFindFinishTime("8p_Fork_Nodes_10_CCR_10.00_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_InTreeBalancedMaxBf3_Nodes_10_CCR_010_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(144,
                readAndFindFinishTime("8p_InTree-Balanced-MaxBf-3_Nodes_10_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_InTreeBalancedMaxBf3_Nodes_10_CCR_104_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(21,
                readAndFindFinishTime("8p_InTree-Balanced-MaxBf-3_Nodes_10_CCR_1.04_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_InTreeBalancedMaxBf3_Nodes_10_CCR_1000_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(56,
                readAndFindFinishTime("8p_InTree-Balanced-MaxBf-3_Nodes_10_CCR_10.00_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_InTreeBalancedMaxBf3_Nodes_10_CCR_202_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(36,
                readAndFindFinishTime("8p_InTree-Balanced-MaxBf-3_Nodes_10_CCR_2.02_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_InTreeUnbalancedMaxBf3_Nodes_10_CCR_010_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(278,
                readAndFindFinishTime("8p_InTree-Unbalanced-MaxBf-3_Nodes_10_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_InTreeUnbalancedMaxBf3_Nodes_10_CCR_102_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(38,
                readAndFindFinishTime("8p_InTree-Unbalanced-MaxBf-3_Nodes_10_CCR_1.02_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_InTreeUnbalancedMaxBf3_Nodes_10_CCR_1000_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(56,
                readAndFindFinishTime("8p_InTree-Unbalanced-MaxBf-3_Nodes_10_CCR_10.00_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_InTreeUnbalancedMaxBf3_Nodes_10_CCR_202_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(34,
                readAndFindFinishTime("8p_InTree-Unbalanced-MaxBf-3_Nodes_10_CCR_2.02_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Independent_Nodes_10_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(10,
                readAndFindFinishTime("8p_Independent_Nodes_10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Join_Nodes_10_CCR_010_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(130,
                readAndFindFinishTime("8p_Join_Nodes_10_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Join_Nodes_10_CCR_100_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(26,
                readAndFindFinishTime("8p_Join_Nodes_10_CCR_1.00_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Join_Nodes_10_CCR_1007_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(52,
                readAndFindFinishTime("8p_Join_Nodes_10_CCR_10.07_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Join_Nodes_10_CCR_200_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(24,
                readAndFindFinishTime("8p_Join_Nodes_10_CCR_2.00_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_OutTreeBalancedMaxBf3_Nodes_10_CCR_010_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(206,
                readAndFindFinishTime("8p_OutTree-Balanced-MaxBf-3_Nodes_10_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_OutTreeBalancedMaxBf3_Nodes_10_CCR_093_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(22,
                readAndFindFinishTime("8p_OutTree-Balanced-MaxBf-3_Nodes_10_CCR_0.93_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_OutTreeBalancedMaxBf3_Nodes_10_CCR_197_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(35,
                readAndFindFinishTime("8p_OutTree-Balanced-MaxBf-3_Nodes_10_CCR_1.97_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_OutTreeBalancedMaxBf3_Nodes_10_CCR_1000_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(35,
                readAndFindFinishTime("8p_OutTree-Balanced-MaxBf-3_Nodes_10_CCR_10.00_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_OutTreeUnbalancedMaxBf3_Nodes_10_CCR_010_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(280,
                readAndFindFinishTime("8p_OutTree-Unbalanced-MaxBf-3_Nodes_10_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_OutTreeUnbalancedMaxBf3_Nodes_10_CCR_109_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(32,
                readAndFindFinishTime("8p_OutTree-Unbalanced-MaxBf-3_Nodes_10_CCR_1.09_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_OutTreeUnbalancedMaxBf3_Nodes_10_CCR_196_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(26,
                readAndFindFinishTime("8p_OutTree-Unbalanced-MaxBf-3_Nodes_10_CCR_1.96_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_OutTreeUnbalancedMaxBf3_Nodes_10_CCR_1001_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(71,
                readAndFindFinishTime("8p_OutTree-Unbalanced-MaxBf-3_Nodes_10_CCR_10.01_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Pipeline_Nodes_10_CCR_010_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(481,
                readAndFindFinishTime("8p_Pipeline_Nodes_10_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Pipeline_Nodes_10_CCR_100_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(66,
                readAndFindFinishTime("8p_Pipeline_Nodes_10_CCR_1.00_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Pipeline_Nodes_10_CCR_197_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(53,
                readAndFindFinishTime("8p_Pipeline_Nodes_10_CCR_1.97_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Pipeline_Nodes_10_CCR_1000_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(50,
                readAndFindFinishTime("8p_Pipeline_Nodes_10_CCR_10.00_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Random_Nodes_10_Density_020_CCR_100_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(17,
                readAndFindFinishTime("8p_Random_Nodes_10_Density_0.20_CCR_1.00_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Random_Nodes_10_Density_040_CCR_1000_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(17,
                readAndFindFinishTime("8p_Random_Nodes_10_Density_0.40_CCR_10.00_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Random_Nodes_10_Density_040_CCR_1002_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(18,
                readAndFindFinishTime("8p_Random_Nodes_10_Density_0.40_CCR_10.02_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Random_Nodes_10_Density_050_CCR_010_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(88,
                readAndFindFinishTime("8p_Random_Nodes_10_Density_0.50_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Random_Nodes_10_Density_060_CCR_102_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(18,
                readAndFindFinishTime("8p_Random_Nodes_10_Density_0.60_CCR_1.02_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Random_Nodes_10_Density_060_CCR_200_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(35,
                readAndFindFinishTime("8p_Random_Nodes_10_Density_0.60_CCR_2.00_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Random_Nodes_10_Density_130_CCR_010_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(341,
                readAndFindFinishTime("8p_Random_Nodes_10_Density_1.30_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Random_Nodes_10_Density_140_CCR_185_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(32,
                readAndFindFinishTime("8p_Random_Nodes_10_Density_1.40_CCR_1.85_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Random_Nodes_10_Density_150_CCR_1000_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(57,
                readAndFindFinishTime("8p_Random_Nodes_10_Density_1.50_CCR_10.00_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Random_Nodes_10_Density_150_CCR_203_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(34,
                readAndFindFinishTime("8p_Random_Nodes_10_Density_1.50_CCR_2.03_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Random_Nodes_10_Density_200_CCR_102_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(79,
                readAndFindFinishTime("8p_Random_Nodes_10_Density_2.00_CCR_1.02_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Random_Nodes_10_Density_230_CCR_010_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(895,
                readAndFindFinishTime("8p_Random_Nodes_10_Density_2.30_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Random_Nodes_10_Density_450_CCR_010_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(2680,
                readAndFindFinishTime("8p_Random_Nodes_10_Density_4.50_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Random_Nodes_10_Density_450_CCR_099_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(274,
                readAndFindFinishTime("8p_Random_Nodes_10_Density_4.50_CCR_0.99_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Random_Nodes_10_Density_450_CCR_1000_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(66,
                readAndFindFinishTime("8p_Random_Nodes_10_Density_4.50_CCR_10.00_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Random_Nodes_10_Density_450_CCR_200_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(126,
                readAndFindFinishTime("8p_Random_Nodes_10_Density_4.50_CCR_2.00_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_SeriesParallelMaxBf2_Nodes_10_CCR_010_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(494,
                readAndFindFinishTime("8p_SeriesParallel-MaxBf-2_Nodes_10_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_SeriesParallelMaxBf2_Nodes_10_CCR_102_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(48,
                readAndFindFinishTime("8p_SeriesParallel-MaxBf-2_Nodes_10_CCR_1.02_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_SeriesParallelMaxBf2_Nodes_10_CCR_198_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(55,
                readAndFindFinishTime("8p_SeriesParallel-MaxBf-2_Nodes_10_CCR_1.98_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_SeriesParallelMaxBf2_Nodes_10_CCR_1003_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(76,
                readAndFindFinishTime("8p_SeriesParallel-MaxBf-2_Nodes_10_CCR_10.03_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_SeriesParallelMaxBf3_Nodes_10_CCR_010_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(358,
                readAndFindFinishTime("8p_SeriesParallel-MaxBf-3_Nodes_10_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_SeriesParallelMaxBf3_Nodes_10_CCR_101_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(47,
                readAndFindFinishTime("8p_SeriesParallel-MaxBf-3_Nodes_10_CCR_1.01_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_SeriesParallelMaxBf3_Nodes_10_CCR_198_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(50,
                readAndFindFinishTime("8p_SeriesParallel-MaxBf-3_Nodes_10_CCR_1.98_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_SeriesParallelMaxBf3_Nodes_10_CCR_1002_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(62,
                readAndFindFinishTime("8p_SeriesParallel-MaxBf-3_Nodes_10_CCR_10.02_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_SeriesParallelMaxBf4_Nodes_10_CCR_010_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(476,
                readAndFindFinishTime("8p_SeriesParallel-MaxBf-4_Nodes_10_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_SeriesParallelMaxBf4_Nodes_10_CCR_097_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(51,
                readAndFindFinishTime("8p_SeriesParallel-MaxBf-4_Nodes_10_CCR_0.97_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_SeriesParallelMaxBf4_Nodes_10_CCR_1004_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(50,
                readAndFindFinishTime("8p_SeriesParallel-MaxBf-4_Nodes_10_CCR_10.04_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_SeriesParallelMaxBf4_Nodes_10_CCR_205_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(49,
                readAndFindFinishTime("8p_SeriesParallel-MaxBf-4_Nodes_10_CCR_2.05_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_SeriesParallelMaxBf5_Nodes_10_CCR_010_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(467,
                readAndFindFinishTime("8p_SeriesParallel-MaxBf-5_Nodes_10_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_SeriesParallelMaxBf5_Nodes_10_CCR_100_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(37,
                readAndFindFinishTime("8p_SeriesParallel-MaxBf-5_Nodes_10_CCR_1.00_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_SeriesParallelMaxBf5_Nodes_10_CCR_199_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(57,
                readAndFindFinishTime("8p_SeriesParallel-MaxBf-5_Nodes_10_CCR_1.99_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_SeriesParallelMaxBf5_Nodes_10_CCR_997_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(59,
                readAndFindFinishTime("8p_SeriesParallel-MaxBf-5_Nodes_10_CCR_9.97_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Stencil_Nodes_10_CCR_010_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(450,
                readAndFindFinishTime("8p_Stencil_Nodes_10_CCR_0.10_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Stencil_Nodes_10_CCR_101_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(77,
                readAndFindFinishTime("8p_Stencil_Nodes_10_CCR_1.01_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Stencil_Nodes_10_CCR_197_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(57,
                readAndFindFinishTime("8p_Stencil_Nodes_10_CCR_1.97_WeightType_Random.dot", 8));
    }

    @Test
    public void test8p_Stencil_Nodes_10_CCR_998_WeightType_Randomwith_8Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(46,
                readAndFindFinishTime("8p_Stencil_Nodes_10_CCR_9.98_WeightType_Random.dot", 8));
    }

    @Test
    public void testsmol_boiwith_2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(9,
                readAndFindFinishTime("smol_boi.dot", 2));
    }

    @Test
    public void testNodes_7_OutTreewith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(22,
                readAndFindFinishTime("Nodes_7_OutTree.dot", 4));
    }

    @Test
    public void testNodes_7_OutTreewith_2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(28,
                readAndFindFinishTime("Nodes_7_OutTree.dot", 2));
    }

    @Test
    public void testNodes_7_OutTreewith_1Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(40,
                readAndFindFinishTime("Nodes_7_OutTree.dot", 1));
    }

    @Test
    public void testNodes_11_OutTreewith_1Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(640,
                readAndFindFinishTime("Nodes_11_OutTree.dot", 1));
    }

    @Test
    public void testNodes_11_OutTreewith_2Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(350,
                readAndFindFinishTime("Nodes_11_OutTree.dot", 2));
    }

    @Test
    public void testNodes_11_OutTreewith_4Processor() throws FileNotFoundException, EdgeDoesNotExistException {
        // read input graph and find path
        assertEquals(227,
                readAndFindFinishTime("Nodes_11_OutTree.dot", 4));
    }

}
