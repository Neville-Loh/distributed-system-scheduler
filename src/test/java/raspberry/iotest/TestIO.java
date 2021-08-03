package raspberry.iotest;

import static org.junit.Assert.*;

import org.junit.Test;


import raspberry.scheduler.io.GraphReader;
import raspberry.scheduler.io.InvalidFormatException;
import raspberry.scheduler.io.Reader;

import java.io.FileNotFoundException;

public class TestIO {

    /**
     * test normal input
     * @throws FileNotFoundException
     */
    @Test
    public void testInput() throws  FileNotFoundException {
//        Reader file1 = new Reader("src/test/resources/input/example1.dot");
//        file1.read();
        GraphReader graphReader = new GraphReader("src/test/resources/output/outputExample.dot");
        graphReader.read();
    }

    /**
     * test input file with incorrect first line format
     */
    @Test
    public void testIncorrectFirstLineFormat() {
        try {
            Reader file1 = new Reader("src/test/resources/input/incorrectfirstline.dot");
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
            Reader file1 = new Reader("src/test/resources/input/incorrectedgeline.dot");
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
            Reader file1 = new Reader("src/test/resources/input/incorrectnodeline.dot");
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
            Reader file1 = new Reader("src/test/resources/input/incorrectlastline.dot");
            file1.read();
            fail();
        } catch (InvalidFormatException e) {
            // This exception is expected - ignore it.
        }
    }
}
