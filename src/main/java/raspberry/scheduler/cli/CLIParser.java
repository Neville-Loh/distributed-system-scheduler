package raspberry.scheduler.cli;

import raspberry.scheduler.cli.CLIConfig;

import javax.swing.text.html.parser.Parser;
import java.awt.print.PrinterAbortException;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;

// This class handles the parsing of the commands and exceptions thrown when the program is accessed from the command line.
public class CLIParser {
    public static final String WRONG_ARGUMENTS = "The arguments entered are not registered. Please try -help for more options.";
    public static final String HELP_MENU = "Help Menu: \n \n" +
            "java -jar " + getJARFileName() + " INPUT.dot P [OPTION] \n \n" +
            "INPUT.dot : a task graph with integer weights in dot format \n" +
            "P : number of processors to schedule the input graph on \n \n" +
            "Optional: \n" +
            "-p N : Use N cores for execution in parallel (default is sequential) \n" +
            // Visualisation still needs to be implemented
            // "-v : visualise the search \n" +
            "-o OUTPUT : output file is name OUTPUT (default is INPUT-output.dot)";
    public static final String NO_INPUT_NUM_CORES = "No input was detected for number of cores.";
    public static final String NO_INTEGER_NUM_CORES = "Please enter an valid integer for number of cores.";
    public static final String NO_OUTPUT_FILE_INPUT = "Please enter a name for the output file.";

    /* Takes in command inputs and creates CLIConfig object
    * inputs should be in the form [InputFileName, NumberOfProcessors, Option,
    * NumberOfCores if -p is chosen/ Name of OUTPUT file if -o is chosen]
    */
    public static CLIConfig parser(String[] inputs) throws ParserException {
        CLIConfig CLIConfig = new CLIConfig();

        // Check if the user requested for help.
        for (String input: inputs) {
            if (input.equals("-help")) {
                System.out.println(HELP_MENU);
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
        System.out.println(String.format("---input file set ---- : %s",CLIConfig.getDotFile()));
        try {
            CLIConfig.setNumProcessors(Integer.parseInt(inputs[1]));
        }catch(NumberFormatException e){
            throw new ParserException("Please input a valid number of processors");
        }
        if(Integer.parseInt(inputs[1]) > 0) {
            System.out.println(String.format("---number of processors set ---- : %s", CLIConfig.get_numProcessors()));
        }else{
            throw new ParserException("Number of processors cannot be less than 1");
        }

        for (int i = 2; i < inputs.length; i++) {

            // Check for option to select number of parallel cores used
            // Need to check whether there is an input at all and if it is in integer, do later
            // Have to add default values.
            if (Objects.equals(inputs[i], "-p")) {
                try {
                    if(Integer.parseInt(inputs[i+1])> 0){
                        CLIConfig.setNumCores(Integer.parseInt(inputs[i + 1]));
                        System.out.println(String.format("---number of cores set---- : %s", inputs[i + 1]));
                        i++;
                    } else{
                        throw new ParserException("Number of cores cannot be less than 1");
                    }
                }
                catch (ArrayIndexOutOfBoundsException e){
                    throw new ParserException(NO_INPUT_NUM_CORES);
                }
                catch (NumberFormatException e){
                    throw new ParserException(NO_INTEGER_NUM_CORES);
                }
            }

            // Visualisation still needs to be implemented
            // else if (inputs[2] == "-v"){

            //}

            // Check for option to select name of OUTPUT file (default is INPUT-output.dot)
            // Check if there is an input.
            // Have to add default values.
            else if (Objects.equals(inputs[i], "-o")) {
                try {
                    CLIConfig.setOutputFile(inputs[i + 1]);
                    i++;
                    System.out.println(String.format("---output filename set---- : %s",CLIConfig.getOutputFile()));
                }
                catch (ArrayIndexOutOfBoundsException e){
                    throw new ParserException(NO_OUTPUT_FILE_INPUT);
                }
            }
            else {
                throw new ParserException(String.format("Invalid Argument: %s,   -help", inputs[i]));
            }
        }


        // if user has not chosen to select an output file name, the default will be given.
        if (CLIConfig.getOutputFile() == null){
            CLIConfig.defaultOutput();
        }

    return CLIConfig;
    }

    // Return the JAR file name.
    public static String getJARFileName(){
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