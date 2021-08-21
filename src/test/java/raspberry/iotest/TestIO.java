package raspberry.iotest;

import static org.junit.Assert.*;

import com.paypal.digraph.parser.GraphParserException;
import org.junit.Test;


import raspberry.scheduler.io.GraphReader;

import java.io.FileNotFoundException;

/**
 * This class tests the input dot file for the reader.
 */
public class TestIO {

    /**
     * test normal input
     * @throws FileNotFoundException
     */
    @Test
    public void testInput() throws  FileNotFoundException {
        GraphReader graphReader = new GraphReader("src/test/resources/input/example.dot");
        graphReader.read();
    }

}
