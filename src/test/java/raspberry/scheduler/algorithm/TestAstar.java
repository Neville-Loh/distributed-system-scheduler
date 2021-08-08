package raspberry.scheduler.algorithm;

import org.junit.Before;
import org.junit.Test;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.io.GraphReader;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestAstar {

    @Before
    public void setup() throws FileNotFoundException {
    }

    /**
     * Test performance of Astar algorithm and correctness of output
     * Name: Nodes_7_OutTree.dot
     * Expected total Time for schedule: 28
     * @throws FileNotFoundException file does not exists
     */
    @Test
    public void testNodes7OutTree2Processor() throws FileNotFoundException {
        GraphReader file1 = new GraphReader("input/Nodes_7_OutTree.dot");
        IGraph graph = file1.read();
        Astar astar = new Astar(graph,2);
        OutputSchedule schedule = astar.findPath();

        System.out.println(schedule);
    }

}
