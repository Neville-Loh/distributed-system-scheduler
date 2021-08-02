package raspberry.iotest;

import static org.junit.Assert.*;

import org.junit.Test;


import raspberry.scheduler.io.InvalidFormatException;
import raspberry.scheduler.io.Reader;

public class TestIO {

    /**
     * test normal input
     * @throws InvalidFormatException
     */
    @Test
    public void testInput() throws InvalidFormatException {
        Reader file1 = new Reader("src/test/resources/example.dot");
        file1.read();
    }

    /**
     * test input file with incorrect first line format
     */
    @Test
    public void testIncorrectFirstLineFormat() {
        try {
            Reader file1 = new Reader("src/test/resources/incorrectfirstline.dot");
            file1.read();
            fail();
        } catch (InvalidFormatException e) {
            // This exception is expected - ignore it.
        }
    }


    /**
     * test input file with incorrect edge line format
     */
    @Test
    public void testIncorrectEdgeFormat() {
        try {
            Reader file1 = new Reader("src/test/resources/incorrectedgeline.dot");
            file1.read();
            fail();
        } catch (InvalidFormatException e) {
            // This exception is expected - ignore it.
        }
    }

    /**
     * test input file with incorrect node line format
     */
    @Test
    public void testIncorrectNodeFormat() {
        try {
            Reader file1 = new Reader("src/test/resources/incorrectnodeline.dot");
            file1.read();
            fail();
        } catch (InvalidFormatException e) {
            // This exception is expected - ignore it.
        }
    }

    /**
     * test input file with incorrect last line format
     */
    @Test
    public void testIncorrectLastLine() {
        try {
            Reader file1 = new Reader("src/test/resources/incorrectlastline.dot");
            file1.read();
            fail();
        } catch (InvalidFormatException e) {
            // This exception is expected - ignore it.
        }
    }


}
