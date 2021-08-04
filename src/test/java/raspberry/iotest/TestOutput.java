package raspberry.iotest;

import org.junit.Test;
import raspberry.scheduler.algorithm.Astar;
import raspberry.scheduler.algorithm.OutputSchedule;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.io.GraphReader;
import raspberry.scheduler.io.InvalidFormatException;
import raspberry.scheduler.io.Reader;
import raspberry.scheduler.io.Writer;

import java.io.IOException;

public class TestOutput {

    /**
     * write output file prototype
     */
    @Test
    public void testWriter() throws IOException, InvalidFormatException {

        //read in graph
        GraphReader file1 = new GraphReader("src/test/resources/input/example1.dot");
        file1.read();
        IGraph graph = file1.getGraph();

        //run algo and get output schedule
        Astar astar = new Astar(graph,2);
        OutputSchedule schedule = astar.findPath();

        //write to output file
        Writer writer = new Writer("outputExample","src/test/resources/output", graph, schedule);
        writer.write();
    }
}
