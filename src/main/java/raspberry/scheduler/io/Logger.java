package raspberry.scheduler.io;

import au.com.bytecode.opencsv.CSVWriter;
import raspberry.scheduler.algorithm.Astar;
import raspberry.scheduler.cli.CLIConfig;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import au.com.bytecode.opencsv.CSVReader;

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
        String[] _dataLines = new String[]{
                java.time.LocalDateTime.now().toString(),
                CLIConfig.getDotFile(),
                Integer.toString(CLIConfig.get_numProcessors()),
                Double.toString((currentTime - startTime)/1000000000.0)};
        fileOutput(_dataLines);
    }

    /**
     * Outputs the statistics to csv file.
     * @throws IOException
     */
    public static void fileOutput(String[] input) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(CSV_FILE, true));

        writer.writeNext(input);
        writer.close();
    }
}
