package raspberry.iotest;


import org.junit.Before;
import main.java.raspberry.scheduler.cli.CLIParser;
import main.java.raspberry.scheduler.cli.ParserException;
import org.junit.Test;
import org.junit.Assert.*;

import static org.junit.Assert.fail;

public class TestCLI {
    private CLIParser parser;


    @Before
    public void setup(){
       parser =  new CLIParser();
    }

//Test when no inputs are given
    @Test
    public void testCLINoArgs() throws ParserException {
        String[] testArgs = {"input.dot","4"};
        parser.parser(testArgs);

    }
//Test when no optional inputs are given
    @Test
    public void testSimpleInputs(){
        String[] testArgs = {"input.dot","4"};
        parser.parser(testArgs);
    }
//test when -v is given
    @Test
    public void testHasVisualisation(){
        String[] testArgs = {"input.dot","4"};
        parser.parser(testArgs);
    }

//test that the correct output file name is set when a outfile file name is given
    @Test
    public void testOutputFileName(){
        String[] testArgs = {"input.dot","4"};
        parser.parser(testArgs);
    }
//test valid num cores is set
    @Test
    public void testNumCores(){
        String[] testArgs = {"input.dot","4"};
        parser.parser(testArgs);
    }
//test that when inavlid arguemtns are given an exception is thrown
    @Test public void testInvalidArgs(){
        String[] testArgs = {"input.dot","4","-failpls"};
        try{
            parser.parser(testArgs);
            fail();
        }catch(Exception e){

        }
    }
//test when --help is given that a help menu is given
    @Test
    public void testHelp(){
        String[] help = {"--help"};
        parser.parser(help);
        try{
            fail();
        }catch(Exception e){

        }

    }
//test for when an input file that doesn't exist is given an exception is thrown
    @Test
    public void testInvaidInputFile(){
try{}
catch(Exception e){

}
    }

}
