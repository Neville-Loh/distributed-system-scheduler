package raspberry.scheduler.algorithm;

import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the output checker to see whether it actually works.
 * @Author: Jonathon
 */
public class TestOutputChecker {

//    /**
//     * Setup OutputChecker for testing.
//     */
//    @Before
//    public void OutputCheckerSetup(){
//
//    }

    /**
     * Test case for when all three tests: Dependency Check, Overlap,
     * Tasks Present should be satisfied. This means the output test schedule
     * checker will not a false negative result.
     */
    @Test
    public void testOutputCheckerCorrect(){

    }

    /**
     * Test case for when dependency check should be violated. This ensures
     * that if the output schedule given, violates any constraint given by the
     * dependency graph, then the output checker will return a negative result
     * so a false positive result is not returned.
     */
    @Test
    public void testIsValid(){

    }

    /**
     * Test for when Overlap check should be violated. This ensures that
     * if the output schedule given, violates any of these conditions.
     * overlap occurs when the following happens:
     * - a child node begins execution prior to the parent node
     * - a child node begins execution while parent node is still in progress.
     * - node with no dependency relation overlap
     * If any of these conditions are violated, this test should pick it up or
     * a false positive result may be returned.
     */
    @Test
    public void testIsOverlap(){

    }

    /**
     * Test for when not all tasks given in the input graph are not
     * present in the output schedule, to avoid false positive.
     */
    @Test
    public void testAllTasksPresent(){

    }

}