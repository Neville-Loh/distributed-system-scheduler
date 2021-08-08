package raspberry.scheduler.cli;

import raspberry.scheduler.algorithm.Astar;
import raspberry.scheduler.algorithm.OutputSchedule;
import raspberry.scheduler.cli.CLIConfig;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.io.GraphReader;
import raspberry.scheduler.io.Writer;

import java.io.FileNotFoundException;
import java.io.IOException;

public class CLIRunner {

    public static void main(String[] inputs) throws IOException {
//        for (String input: inputs){
//            System.out.println(input);
//        }
        try {
            CLIConfig CLIConfig = CLIParser.parser(inputs);
            GraphReader reader = new GraphReader(CLIConfig.getDotFile());
            IGraph graph = reader.read();

            Astar astar = new Astar(graph,CLIConfig.get_numProcessors());
            OutputSchedule outputSchedule = astar.findPath();
            System.out.println("-----------");
            System.out.println("------OUTPUT FILE NAME-----");
            System.out.println(CLIConfig.getOutputFile());
            System.out.println("-----------");
            Writer writer = new Writer(CLIConfig.getOutputFile(), graph, outputSchedule);
            writer.write();
        } catch (ParserException | NumberFormatException e) {
//            System.out.println(e.getMessage());
        }
    }
}
