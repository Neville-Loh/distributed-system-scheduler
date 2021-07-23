package test.java.raspberry.iotest;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import main.java.raspberry.scheduler.io.Reader;

public class TestIO {
	
	@Before
	public void setup() {
		Reader file1 = new Reader("C:\\Users\\ym\\Desktop\\softeng306\\example1.dot");
	}

	@Test
	public void testNode() {
		fail("Not yet implemented");
	}

	@Test
	public void testEdge() {
		fail("Not yet implemented");
	}

}
