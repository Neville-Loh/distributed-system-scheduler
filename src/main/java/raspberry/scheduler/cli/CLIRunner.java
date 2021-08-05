package raspberry.scheduler.cli;

import raspberry.scheduler.cli.CLIConfig;

public class CLIRunner {
    public static void main(String[] inputs) {
//        for (String input: inputs){
//            System.out.println(input);
//        }
        try {
            CLIConfig CLIConfig = CLIParser.parser(inputs);

        } catch (ParserException | NumberFormatException e) {
//            System.out.println(e.getMessage());
        }
    }
}
