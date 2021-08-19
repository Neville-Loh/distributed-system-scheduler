package raspberry.scheduler.io;

import raspberry.scheduler.algorithm.Astar;
import raspberry.scheduler.cli.CLIConfig;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

/**
 * This class handles the overall statistics: start time, finish time and memory usage
 * for the program and exports it to a csv class for the report. It will compare the
 * milestone 1, program without multithreading and program with multithreading.
 * @author Jonathon
 */
public class Logger {
//    private double _dateTime;
//    private String _inputFileName;
//    private int _processorNumber;
//    private double _duration;
    private static final String CSV_FILE = "src/test/resources/output/performanceData.csv";


    public static void log(CLIConfig CLIConfig, Double startTime, long currentTime) throws IOException {
        String[] _dataLines = new String[]{"Milestone 1 Statistics:",
                "Date Time: ", java.time.LocalDateTime.now().toString(),
                "Input File Name: ", CLIConfig.getDotFile(),
                "Number of processors: ", Integer.toString(CLIConfig.get_numProcessors()),
                "Duration: ", Double.toString((currentTime - startTime)/1000000000.0)};
        fileOutput(_dataLines);
    }

    /**
     * Outputs the statistics to csv file.
     * @throws IOException
     */
    public static void fileOutput(String[] input) throws IOException {

        try (PrintWriter writer = new PrintWriter(new File(CSV_FILE))){

            StringBuilder sb = new StringBuilder();
            for (String s : input) {
                sb.append(s);
                sb.append(" \n");
            }
            writer.write(sb.toString());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
