package raspberry.scheduler;

import raspberry.scheduler.algorithm.astar.Astar;
import raspberry.scheduler.algorithm.bNb.BNB;
import raspberry.scheduler.algorithm.bNb.BNBParallel;
import raspberry.scheduler.algorithm.common.OutputSchedule;
import raspberry.scheduler.cli.CLIConfig;
import raspberry.scheduler.cli.CLIParser;
import raspberry.scheduler.cli.exception.ParserException;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.io.GraphReader;
import raspberry.scheduler.io.Logger;
import raspberry.scheduler.io.Writer;
import raspberry.scheduler.app.*;

import java.io.IOException;

public class Main {
    public static final boolean COLLECT_STATS_ENABLE = false;
    private static double _startTime;
    public static void main(String[] inputs) throws NumberFormatException {
        try {
            CLIConfig CLIConfig = CLIParser.parser(inputs);
            GraphReader reader = new GraphReader(CLIConfig.getDotFile());

            // Start visualisation if appropriate argument is given.
            if (CLIConfig.getVisualise()) {
                startVisualisation(CLIConfig, reader);
            } else {
                IGraph graph = reader.read();
                if (COLLECT_STATS_ENABLE) {_startTime = System.nanoTime();}
                if (CLIConfig.getNumCores()>1) {
                    BNBParallel bnb = new BNBParallel(graph, CLIConfig.getNumProcessors(), Integer.MAX_VALUE,CLIConfig.getNumCores());
                    OutputSchedule outputSchedule = bnb.findPath();
                    if (COLLECT_STATS_ENABLE) {Logger.log(CLIConfig, _startTime, System.nanoTime());}
                    Writer writer = new Writer(CLIConfig.getOutputFile(), graph, outputSchedule);
                    writer.write();
                } else {
                    BNB bnb = new BNB(graph, CLIConfig.getNumProcessors(), Integer.MAX_VALUE);
                    OutputSchedule outputSchedule = bnb.findPath();
                    if (COLLECT_STATS_ENABLE) {Logger.log(CLIConfig, _startTime, System.nanoTime());}
                    Writer writer = new Writer(CLIConfig.getOutputFile(), graph, outputSchedule);
                    writer.write();
                }
            }
        } catch (IOException | ParserException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void startVisualisation(CLIConfig config, GraphReader reader) {
//        new Thread(()-> {
        VisualisationLauncher.main(config, reader);
//        }).start();
    }
}