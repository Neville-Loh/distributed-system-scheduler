package raspberry.scheduler;

import raspberry.scheduler.algorithm.*;
import raspberry.scheduler.cli.CLIConfig;
import raspberry.scheduler.cli.CLIParser;
import raspberry.scheduler.cli.exception.ParserException;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.io.GraphReader;
import raspberry.scheduler.io.Writer;
import raspberry.scheduler.app.*;

import java.io.IOException;


public class Main {
    public static void main(String[] inputs) throws NumberFormatException {
        try {
            CLIConfig CLIConfig = CLIParser.parser(inputs);
            GraphReader reader = new GraphReader(CLIConfig.getDotFile());

            // Start visualisation if appropriate argument is given.
            if (CLIConfig.getVisualise()){
                startVisualisation(CLIConfig);
            }

            IGraph graph = reader.read();
            BNB astar = new BNB(graph, CLIConfig.get_numProcessors());
            OutputSchedule outputSchedule = astar.findPath();
            Writer writer = new Writer(CLIConfig.getOutputFile(), graph, outputSchedule);
            writer.write();
        } catch (IOException | ParserException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void startVisualisation(CLIConfig config){
        new Thread(()-> {
            App.main(config);
        }).start();
    }
}