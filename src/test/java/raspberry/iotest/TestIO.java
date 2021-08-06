package raspberry.iotest;

import static org.junit.Assert.*;

import com.paypal.digraph.parser.GraphParserException;
import org.junit.Test;


import raspberry.scheduler.io.GraphReader;

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
        GraphReader graphReader = new GraphReader("src/test/resources/input/example.dot");
        graphReader.read();
    }

    /**
     * test input file with incorrect first line format
     */
    @Test
    public void testIncorrectInvalidFormat() throws FileNotFoundException {
            try {
                GraphReader graphReader = new GraphReader("src/test/resources/input/incorrectexample.dot");
                graphReader.read();
                fail();
            } catch (GraphParserException e) {
                // This exception is expected - ignore it.
            }
    }
}
