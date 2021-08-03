package main.java.raspberry.scheduler.cli;

//import org.apache.commons.cli;
import java.io.File;
import java.net.URISyntaxException;
//import java.exception.CommandParserException;

// This class handles the parsing of the commands and exceptions thrown when the program is accessed from the command line.
public class CLIParser {
    public static final string WRONG_ARGUMENTS = "The arguments entered are not registered. Please try -help for more options.";
    public static final string HELP_MENU = "Help Menu:" + "java.";  //JARFileName;

    // Takes in command inputs and creates CLIConfig object
    public CLIConfig parser(String[] inputs){
        //CLIConfig CLIConfig = new CLIConfig();

        // Assign string for JAR file name, by checking path name (also checking for URI error)

        for (String input: inputs) {
            if (input == "-help"){
                throw new CommandParserException(HELP_MENU);
            }
        }

        //
    return parser;
    }



//    public String getJarFileName() {
//        string JARFileName = new File(CLIParser.class.getProtectionDomain().getCodeSource()
//        .getLocation().toURL().getPath().getName());
//        } catch (URISyntaxException URIError) {
//        URIError.System.out.printf("There was an error while processing the JAR file name");
//        }
//}


}
