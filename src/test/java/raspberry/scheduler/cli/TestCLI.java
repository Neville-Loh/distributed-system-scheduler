package raspberry.scheduler.cli;

import org.junit.Before;
import raspberry.scheduler.cli.CLIConfig;
import raspberry.scheduler.cli.CLIParser;
import raspberry.scheduler.cli.exception.ParserException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Unit test for
 *
 * @author Alan
 * This test class is for the Command line interface
 */
public class TestCLI {

    private CLIParser _parser;
    private CLIConfig _correctConfig;


    /**
     * Set up new
     */
    @Before
    public void setup() {
        _parser = new CLIParser();
        _correctConfig = new CLIConfig();

    }

    /**
     * Test when no inputs are given i.e. no input file and no number of processes - should throw an exception.
     *
     * @throws ParserException
     */
    @Test
    public void testCLINoArgs() {
        try {
            String[] testArgs = {};
            _parser.parser(testArgs);
            fail();
        } catch (ParserException e) {
            //Should throw exception
        }
    }

    /**
     * Test that an exception is thrown when only the input file is given and nothing else
     * Should throw a @ParserException
     */
    @Test
    public void testOnlyInputFileIsGiven() {
        try {
            String[] testArgs = {"input.dot"};
            _parser.parser(testArgs);
            fail();
        } catch (ParserException e) {
           // Should throw exception
        }
    }

    /**
     * Test when no optional inputs are given i.e. no -o is stated
     *
     * @throws ParserException
     */
    @Test
    public void testSimpleInputs() {
        try {
            String[] testArgs = {"input.dot", "4"};
            _correctConfig.setDotFile(testArgs[0]);
            _correctConfig.setNumProcessors(Integer.parseInt(testArgs[1]));
            CLIConfig testConfig = _parser.parser(testArgs);

            String[] testConfigStr = {testConfig.getDotFile(), String.valueOf(testConfig.getNumProcessors())};
            String[] correctConfigStr = {_correctConfig.getDotFile(), String.valueOf(_correctConfig.getNumProcessors())};
            assertEquals(correctConfigStr, testConfigStr);
        } catch (ParserException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    /**
     * test when -v is given, meaning has visualisation - should pass.
     */

    /**
     * Not yet implemented
     */
//    @Test
//    public void testHasVisualisation()throws ParserException {
//        String[] testArgs = {"input.dot","4"};
//        _parser.parser(testArgs);
//    }

    /**
     * test that the correct output file name is set when an outfile file name is given
     */
    @Test
    public void testOutputFileName() {
        try {
            String[] testArgs = {"input.dot", "4", "-o", "output"};
            String expectedFileName = "output.dot";
            CLIConfig testConfig = _parser.parser(testArgs);
            assertEquals(expectedFileName, testConfig.getOutputFile());
        } catch (ParserException e) {
            fail();
        }
    }

    /**
     * Test that the default file name is given correctly
     */
    @Test
    public void testDefaultFileName() {
        try {
            String[] testArgs = {"input.dot", "4"};
            String defaultName = "input-output.dot";
            CLIConfig testConfig = _parser.parser(testArgs);
            assertEquals(defaultName, testConfig.getOutputFile());
        } catch (ParserException e) {
            fail();
        }

    }

    /**
     * This test will have the option selected for setting the output file name - but no output name will be given
     * thus it should throw an exception.
     */
    @Test
    public void testNOOutputFileName() {
        try {
            String[] testArgs = {"input.dot", "4", "-o"};
            CLIConfig testConfig = _parser.parser(testArgs);
            fail();
        } catch (ParserException e) {
            //Should throw exception
        }
    }

    /**
     * This test will have the option selected for setting the number of cores - but invalid number of cores is given
     * thus it should throw an exception.
     */
    @Test
    public void testNoNumCores() {
        try {
            String[] testArgs = {"input.dot", "4", "-p", "-4"};
            CLIConfig testConfig = _parser.parser(testArgs);
            fail();
        } catch (ParserException e) {
            //Should throw exception
        }
    }

    /**
     * test valid num cores is set correctly
     */
    @Test
    public void testNumCores() {
        try {
            String[] testArgs = {"input.dot", "4", "-p", "2"};
            _correctConfig.setDotFile(testArgs[0]);
            _correctConfig.setNumProcessors(Integer.parseInt(testArgs[1]));
            _correctConfig.setNumCores(Integer.parseInt(testArgs[3]));
            CLIConfig testConfig = _parser.parser(testArgs);
            String[] testConfigStr = {testConfig.getDotFile(), String.valueOf(testConfig.getNumProcessors()), String.valueOf(testConfig.getNumCores())};
            String[] correctConfigStr = {_correctConfig.getDotFile(), String.valueOf(_correctConfig.getNumProcessors()), String.valueOf(_correctConfig.getNumCores())};
            assertEquals(correctConfigStr, testConfigStr);
        } catch (ParserException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    /**
     * test that when invalid arguments are given an exception is thrown - i.e. expected int for num process but gives a string instead
     */
    @Test
    public void testInvalidArgs() {
        try {
            String[] testArgs = {"input.dot", "five"};
            CLIConfig testConfig = _parser.parser(testArgs);
            fail();
        } catch (NumberFormatException | ParserException e) {
            //Should throw exception
        }
    }

    /**
     * Test when an invalid option is given i.e.  "-pie" Should throw an exception.
     */
    @Test
    public void testInvalidOptions() {
        try {
            String[] testArgs = {"input.dot", "4", "-pie", "2"};
            CLIConfig testConfig = _parser.parser(testArgs);
            fail();
        } catch (ParserException e) {
            //Should throw exception
        }

    }

    /**
     * test when --help is given that a help exception is given
     */

    @Test
    public void testHelp() {
        try {
            String[] help = {"--help"};
            CLIConfig testConfig = _parser.parser(help);
            fail();
        } catch (ParserException e) {
            //Should throw exception
        }
    }
}
