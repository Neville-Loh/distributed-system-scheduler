package main.java.raspberry.scheduler.cli;

import main.java.raspberry.scheduler.cli.CLIConfig;
import java.io.File;
import java.net.URISyntaxException;

// This class handles the parsing of the commands and exceptions thrown when the program is accessed from the command line.
public class CLIParser {
    public static final String WRONG_ARGUMENTS = "The arguments entered are not registered. Please try -help for more options.";
    public final String HELP_MENU = "Help Menu: \n \n" +
            "java -jar " + getJARFileName() + " INPUT.dot P [OPTION] \n \n" +
            "INPUT.dot : a task graph with integer weights in dot format \n" +
            "P : number of processors to schedule the input graph on \n \n" +
            "Optional: \n" +
            "-p N : Use N cores for execution in parallel (default is sequential) \n" +
            // Visualisation still needs to be implemented
            // "-v : visualise the search \n" +
            "-o OUTPUT : output file is name OUTPUT (default is INPUT-output.dot)";

    // Takes in command inputs and creates CLIConfig object
    // inputs should be in the form [InputFileName, NumberOfProcessors, Option,
    // NumberOfCores if -p is chosen/ Name of OUTPUT file if -o is chosen]
    public CLIConfig parser (String[] inputs) throws ParserException {
        CLIConfig CLIConfig = new CLIConfig();

        // Check if the user requested for help.
        for (String input: inputs) {
            if (input == "-help") {
                throw new ParserException(HELP_MENU);
            }
        }

        // Check if input is of the correct format, otherwise, direct the user to help menu.
        if (inputs.length < 2) {
            throw new ParserException(WRONG_ARGUMENTS);
        }

        // Get the Input File Name (inputs[0]) and Number of Processors (inputs[1]).
        // Number of Processors is processed as a string.
        // Have to add in exception if input is not an integer.
        CLIConfig.setDotFile(inputs[0]);
        CLIConfig.setNumProcessors(Integer.parseInt(inputs[1]));

        // Check for option to select number of parallel cores used
        // Need to check whether there is an input at all and if it is in integer, do later
        // Have to add default values.
        if (inputs[2] == "-p"){
            CLIConfig.setNumCores(Integer.parseInt(inputs[3]));
        }

        // Visualisation still needs to be implemented
        // else if (inputs[2] == "-v"){

        //}

        // Check for option to select name of OUTPUT file (default is INPUT-output.dot)
        // Check if there is an input.
        // Have to add default values.
        else if (inputs[2] == "-o"){
            CLIConfig.setOutputFile(inputs[3]);
        }

    return CLIConfig;
    }

    public String getJARFileName(){
        return new File(CLIParser.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
    }

    // I tried to implement the method so that it checked for URI exceptions, but it didn't work.
//    // Method to get JAR file while checking for URI exceptions
//    public String getJARFileName() throws ParserException {
//        try {
//            // This is static.
//            String filePath = CLIParser.class.getProtectionDomain().getCodeSource()
//                .getLocation().toURI().getPath();
//            return String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
//        }
//        catch (URISyntaxException URIError) {
//            throw new ParserException("There was an error while processing the JAR file name.");
//        }
//
//}


}
