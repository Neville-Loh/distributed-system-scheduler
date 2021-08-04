package raspberry.scheduler.cli;

import main.java.raspberry.scheduler.cli.CLIConfig;

public class CLIRunner {
    public static void main(String[] inputs) throws ParserException {
        System.out.println("Hello world");

        for (String input: inputs){
            System.out.println(input);
        }

        CLIConfig CLIConfig = CLIParser.parser(inputs);
    }
}
