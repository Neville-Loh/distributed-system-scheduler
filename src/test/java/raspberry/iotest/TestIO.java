package raspberry.iotest;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;


import raspberry.scheduler.io.Reader;

public class TestIO {
	
	@Before
	public void setup() {
//		URL url = getClass().getResource("example.dot");
		Reader file1 = new Reader("src/test/resources/example.dot");


//		System.out.println(directory.getAbsolutePath());
	}

	@Test
	public void testNode() {
		//fail("Not yet implemented");
	}

	@Test
	public void testEdge() {
		//fail("Not yet implemented");
	}


}