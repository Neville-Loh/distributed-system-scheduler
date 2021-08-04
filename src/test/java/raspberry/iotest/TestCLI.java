package raspberry.iotest;


import org.junit.Before;
import main.java.raspberry.scheduler.cli.CLIParser;
import main.java.raspberry.scheduler.cli.ParserException;
import org.junit.Test;

public class TestCLI {
    private CLIParser parser;


    @Before
    public void setup(){
       parser =  new CLIParser();
    }

    @Test
    public void testCLINoArgs() throws ParserException {
        String[] testArgs = {"input.dot","4"};

    }

    @Test
    public void testCLINoOptionals(){
        String[] testArgs = {"input.dot","4"};

    }

    @Test
    public void testSimpleInputs(){

    }

    @Test
    public void testHasVisualisation(){

    }



    @Test
    public void testOutputFileName(){

    }

    @Test
    public void testNumCores(){

    }

}
