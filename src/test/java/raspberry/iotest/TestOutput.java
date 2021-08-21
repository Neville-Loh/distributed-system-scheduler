package raspberry.iotest;

import org.junit.Test;
import raspberry.scheduler.algorithm.OutputChecker;
import raspberry.scheduler.algorithm.Astar;
import raspberry.scheduler.algorithm.OutputSchedule;
import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.io.GraphReader;
import raspberry.scheduler.io.exceptions.InvalidFormatException;
import raspberry.scheduler.io.Writer;

import java.io.IOException;

/**
 * This class tests the output from the writer class.
 */
public class TestOutput {

    /**
     * write output file prototype
     */
    @Test
    public void testWriter() throws IOException, InvalidFormatException, EdgeDoesNotExistException {
        //read in graph
        GraphReader file1 = new GraphReader("src/test/resources/input/example1.dot");
        IGraph graph = file1.read();

        //run algo and get output schedule
        Astar astar = new Astar(graph,2);
        OutputSchedule schedule = astar.findPath();

        //write to output file
        Writer writer = new Writer("src/test/resources/output/outputExample.dot", graph, schedule);
        writer.write();
    }
}
