package raspberry.scheduler.io;

import au.com.bytecode.opencsv.CSVWriter;
import raspberry.scheduler.cli.CLIConfig;

import java.io.*;


/**
 * This class handles the overall statistics: start time, finish time and memory usage
 * for the program and exports it to a csv class for the report. It will compare the
 * milestone 1, program without multithreading and program with multithreading.
 * @author Jonathon, Young, Neville
 */
public class Logger {
    // Output file declaration
    private static final String CSV_FILE = "src/test/resources/output/performanceData.csv";

    /**
     * This class collects the passed data from the main class and processes it
     * for the CSV file before it is passed on.
     * @param CLIConfig CLI Configuration object which contains input dotfile and
     *                  number of processors
     * @param startTime start time of algorithm
     * @param currentTime finish time of algorithm
     * @throws IOException for when a file is not called/created properly
     */
    public static void log(CLIConfig CLIConfig, Double startTime, long currentTime) throws IOException {
        String[] _dataLines = new String[]{
                java.time.LocalDateTime.now().toString(),
                CLIConfig.getDotFile(),
                Integer.toString(CLIConfig.get_numProcessors()),
                Double.toString((currentTime - startTime)/1000000000.0)};
        fileOutput(_dataLines);
    }

    /**
     * The correct way of writing this
     * @param fileName File name of the log
     * @param numProcessors number of processor
     * @param duration duration of the time
     * @throws IOException for when a file is not called/created properly
     */
    public static void log(String fileName, int numProcessors, int duration) throws IOException {
        String[] _dataLines = new String[]{
                java.time.LocalDateTime.now().toString(),
                fileName,
                String.valueOf(numProcessors),
                String.valueOf(duration)};
        fileOutput(_dataLines);
    }

    /**
     * This method passes the logged statistics into a CSV file.
     * @param input input data from logger
     * @throws IOException for when a file is not called/created properly
     */
    public static void fileOutput(String[] input) throws IOException {
        // Creates a writer object
        CSVWriter writer = new CSVWriter(new FileWriter(CSV_FILE, true));
        // Writes the data into the writer
        writer.writeNext(input);
        // Close the file
        writer.close();
    }
}
