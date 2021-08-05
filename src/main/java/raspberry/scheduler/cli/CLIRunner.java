package raspberry.scheduler.cli;

import raspberry.scheduler.cli.CLIConfig;

public class CLIRunner {
    public static void main(String[] inputs) throws ParserException {
//        for (String input: inputs){
//            System.out.println(input);
//        }

        CLIConfig CLIConfig = CLIParser.parser(inputs);

    }
}
