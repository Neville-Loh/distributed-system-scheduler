package raspberry.scheduler.algorithm.common;


import org.junit.Assert;
import org.junit.Test;

import raspberry.scheduler.algorithm.bnb.ScheduleB;
import raspberry.scheduler.graph.INode;
import raspberry.scheduler.graph.adjacencylist.Graph;
import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;

import java.util.Hashtable;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the output checker to see whether it actually works.
 * @Author: Jonathon
 */
public class TestOutputChecker {

    /**
     * Test case no.1 for when all three tests: Dependency Check, Overlap,
     * Tasks Present should be satisfied. This means the output test schedule
     * checker will not a false negative result.
     */
    @Test
    public void testOutputCheckerCorrect() throws EdgeDoesNotExistException {

        //Initialise the test graph.
        Graph testGraph1 = new Graph("testGraph1");
        testGraph1.addNode("a", 2);
        testGraph1.addNode("b", 2);
        testGraph1.addNode("c", 2);
        testGraph1.addNode("d", 3);
        testGraph1.addNode("e", 2);
        testGraph1.addNode("f", 3);
        testGraph1.addNode("g", 2);

        testGraph1.addEdge("a","b",1);
        testGraph1.addEdge("a","c",3);
        testGraph1.addEdge("a","d",1);
        testGraph1.addEdge("b","e",3);
        testGraph1.addEdge("b","g",4);
        testGraph1.addEdge("c","f",1);
        testGraph1.addEdge("d","f",1);
        testGraph1.addEdge("e","g",2);
        testGraph1.addEdge("f","g",2);

        //Test output schedule for test case no.1
        // Output schedule expects (Schedule from algorithm, number of processors as int)

        // Create the tasks.
        ScheduledTask taska1 = new ScheduledTask(1, testGraph1.getNode("a"), 0);
        ScheduledTask taskb1 = new ScheduledTask(1, testGraph1.getNode("b"), 4);
        ScheduledTask taskc1 = new ScheduledTask(1, testGraph1.getNode("c"), 2);
        ScheduledTask taskd1 = new ScheduledTask(2, testGraph1.getNode("d"), 3);
        ScheduledTask taske1 = new ScheduledTask(1, testGraph1.getNode("e"), 6);
        ScheduledTask taskf1 = new ScheduledTask(2, testGraph1.getNode("f"), 6);
        ScheduledTask taskg1 = new ScheduledTask(2, testGraph1.getNode("g"), 10);

        // Add an indegree table for BnB schedule typing (empty, just for convention)
        Hashtable<INode, Integer> inDegreeTable1 = new Hashtable<INode, Integer>();

        // Append the tasks to the test output schedule.

        // Set the head of the schedule linked list as taska1.

        ScheduleB testSchedule1 = new ScheduleB( taska1, inDegreeTable1);

        // Add the rest of the tasks to the linked list.
        testSchedule1 = new ScheduleB(testSchedule1, taskb1, inDegreeTable1);
        testSchedule1 = new ScheduleB(testSchedule1, taskc1, inDegreeTable1);
        testSchedule1 = new ScheduleB(testSchedule1, taskd1, inDegreeTable1);
        testSchedule1 = new ScheduleB(testSchedule1, taske1, inDegreeTable1);
        testSchedule1 = new ScheduleB(testSchedule1, taskf1, inDegreeTable1);
        testSchedule1 = new ScheduleB(testSchedule1, taskg1, inDegreeTable1);


        // Make outputSchedule.
        OutputSchedule outputSchedule1 = new Solution(testSchedule1, 2);

        // Should pass all three checks, isValid contains all three checks.
        Assert.assertEquals(true , OutputChecker.isValid(testGraph1, outputSchedule1));
    }

    /**
     * Test case no.2 for when dependency check should be violated. This ensures
     * that if the output schedule given, violates any constraint given by the
     * dependency graph, then the output checker will return a negative result
     * so a false positive result is not returned.
     *
     * Issues: several dependency issues, a task is scheduled twice (g).
     */
    @Test
    public void testIsValid() throws EdgeDoesNotExistException {
        //Initialise the test graph.
        Graph testGraph1 = new Graph("testGraph1");
        testGraph1.addNode("a", 2);
        testGraph1.addNode("b", 2);
        testGraph1.addNode("c", 2);
        testGraph1.addNode("d", 3);
        testGraph1.addNode("e", 2);
        testGraph1.addNode("f", 3);
        testGraph1.addNode("g", 2);

        testGraph1.addEdge("a","b",1);
        testGraph1.addEdge("a","c",3);
        testGraph1.addEdge("a","d",1);
        testGraph1.addEdge("b","e",3);
        testGraph1.addEdge("b","g",4);
        testGraph1.addEdge("c","f",1);
        testGraph1.addEdge("d","f",1);
        testGraph1.addEdge("e","g",2);
        testGraph1.addEdge("f","g",2);

        //Test output schedule for test case no.2
        // Output schedule expects (Schedule from algorithm, number of processors as int)

        // Create the tasks.
        ScheduledTask taska1 = new ScheduledTask(1, testGraph1.getNode("a"), 6);
        ScheduledTask taskb1 = new ScheduledTask(1, testGraph1.getNode("b"), 8);
        ScheduledTask taskc1 = new ScheduledTask(1, testGraph1.getNode("c"), 6);
        ScheduledTask taskd1 = new ScheduledTask(2, testGraph1.getNode("d"), 0);
        ScheduledTask taske1 = new ScheduledTask(1, testGraph1.getNode("e"), 8);
        ScheduledTask taskf1 = new ScheduledTask(2, testGraph1.getNode("f"), 2);
        ScheduledTask taskg1 = new ScheduledTask(2, testGraph1.getNode("g"), 0);
        ScheduledTask taskg2 = new ScheduledTask(2, testGraph1.getNode("g"), 10);

        // Add an indegree table for BnB schedule typing (empty, just for convention)
        Hashtable<INode, Integer> inDegreeTable1 = new Hashtable<INode, Integer>();

        // Append the tasks to the test output schedule.

        // Set the head of the schedule linked list as taska1.
        ScheduleB testSchedule1 = new ScheduleB( taska1, inDegreeTable1);
        // Add the rest of the tasks to the linked list.
        testSchedule1 = new ScheduleB(testSchedule1, taskb1, inDegreeTable1);
        testSchedule1 = new ScheduleB(testSchedule1, taskc1, inDegreeTable1);
        testSchedule1 = new ScheduleB(testSchedule1, taskd1, inDegreeTable1);
        testSchedule1 = new ScheduleB(testSchedule1, taske1, inDegreeTable1);
        testSchedule1 = new ScheduleB(testSchedule1, taskf1, inDegreeTable1);
        testSchedule1 = new ScheduleB(testSchedule1, taskg1, inDegreeTable1);
        testSchedule1 = new ScheduleB(testSchedule1, taskg2, inDegreeTable1);

        // Make outputSchedule.
        OutputSchedule outputSchedule1 = new Solution(testSchedule1, 2);

        // Should fail
        assertEquals(false , OutputChecker.isValid(testGraph1, outputSchedule1));
    }

    /**
     * Test no.3 for when Overlap check should be violated. This ensures that
     * if the output schedule given, violates any of these conditions.
     * overlap occurs when the following happens:
     * - a child node begins execution prior to the parent node
     * - a child node begins execution while parent node is still in progress.
     * - node with no dependency relation overlap
     * If any of these conditions are violated, this test should pick it up or
     * a false positive result may be returned.
     *
     * Issues: several dependency issues
     */
    @Test
    public void testIsOverlap() throws EdgeDoesNotExistException {
        //Initialise the test graph.
        Graph testGraph1 = new Graph("testGraph1");
        testGraph1.addNode("a", 2);
        testGraph1.addNode("b", 2);
        testGraph1.addNode("c", 2);
        testGraph1.addNode("d", 3);
        testGraph1.addNode("e", 2);
        testGraph1.addNode("f", 3);
        testGraph1.addNode("g", 2);

        testGraph1.addEdge("a","b",1);
        testGraph1.addEdge("a","c",3);
        testGraph1.addEdge("a","d",1);
        testGraph1.addEdge("b","e",3);
        testGraph1.addEdge("b","g",4);
        testGraph1.addEdge("c","f",1);
        testGraph1.addEdge("d","f",1);
        testGraph1.addEdge("e","g",2);
        testGraph1.addEdge("f","g",2);

        //Test output schedule for test case no.1
        // Output schedule expects (Schedule from algorithm, number of processors as int)

        // Create the tasks.
        ScheduledTask taska1 = new ScheduledTask(1, testGraph1.getNode("a"), 6);
        ScheduledTask taskb1 = new ScheduledTask(1, testGraph1.getNode("b"), 8);
        ScheduledTask taskc1 = new ScheduledTask(1, testGraph1.getNode("c"), 6);
        ScheduledTask taskd1 = new ScheduledTask(2, testGraph1.getNode("d"), 0);
        ScheduledTask taske1 = new ScheduledTask(1, testGraph1.getNode("e"), 8);
        ScheduledTask taskf1 = new ScheduledTask(2, testGraph1.getNode("f"), 2);
        ScheduledTask taskg1 = new ScheduledTask(2, testGraph1.getNode("g"), 0);

        // Add an indegree table for BnB schedule typing (empty, just for convention)
        Hashtable<INode, Integer> inDegreeTable1 = new Hashtable<INode, Integer>();

        // Append the tasks to the test output schedule.

        // Set the head of the schedule linked list as taska1.
        ScheduleB testSchedule1 = new ScheduleB( taska1, inDegreeTable1);
        // Add the rest of the tasks to the linked list.
        testSchedule1 = new ScheduleB(testSchedule1, taskb1, inDegreeTable1);
        testSchedule1 = new ScheduleB(testSchedule1, taskc1, inDegreeTable1);
        testSchedule1 = new ScheduleB(testSchedule1, taskd1, inDegreeTable1);
        testSchedule1 = new ScheduleB(testSchedule1, taske1, inDegreeTable1);
        testSchedule1 = new ScheduleB(testSchedule1, taskf1, inDegreeTable1);
        testSchedule1 = new ScheduleB(testSchedule1, taskg1, inDegreeTable1);

        // Make outputSchedule.
        OutputSchedule outputSchedule1 = new Solution(testSchedule1, 2);

        // Should fail
        assertEquals(false , OutputChecker.isValid(testGraph1, outputSchedule1));
    }

    /**
     * Test no.4 for when not all tasks given in the input graph are not
     * present in the output schedule, to avoid false positive.
     */
    @Test
    public void testAllTasksPresent() throws EdgeDoesNotExistException {
        try {
            Graph testGraph1 = new Graph("testGraph1");
            testGraph1.addNode("a", 2);
            testGraph1.addNode("b", 2);
            testGraph1.addNode("c", 2);
            testGraph1.addNode("d", 3);
            testGraph1.addNode("e", 2);
            testGraph1.addNode("f", 3);
            testGraph1.addNode("g", 2);

            testGraph1.addEdge("a","b",1);
            testGraph1.addEdge("a","c",3);
            testGraph1.addEdge("a","d",1);
            testGraph1.addEdge("b","e",3);
            testGraph1.addEdge("b","g",4);
            testGraph1.addEdge("c","f",1);
            testGraph1.addEdge("d","f",1);
            testGraph1.addEdge("e","g",2);
            testGraph1.addEdge("f","g",2);

            //Test output schedule for test case no.1
            // Output schedule expects (Schedule from algorithm, number of processors as int)

            // Create the tasks.
            ScheduledTask taska1 = new ScheduledTask(1, testGraph1.getNode("a"), 0);
            ScheduledTask taskb1 = new ScheduledTask(1, testGraph1.getNode("b"), 4);
            ScheduledTask taskc1 = new ScheduledTask(1, testGraph1.getNode("c"), 2);
            ScheduledTask taskd1 = new ScheduledTask(2, testGraph1.getNode("d"), 3);
            ScheduledTask taske1 = new ScheduledTask(1, testGraph1.getNode("e"), 6);
            ScheduledTask taskf1 = new ScheduledTask(2, testGraph1.getNode("f"), 6);
            ScheduledTask taskg1 = new ScheduledTask(2, testGraph1.getNode("g"), 10);
            ScheduledTask taskh1 = new ScheduledTask(2, testGraph1.getNode("h"), 12);

            // Add an indegree table for BnB schedule typing (empty, just for convention)
            Hashtable<INode, Integer> inDegreeTable1 = new Hashtable<INode, Integer>();

            // Append the tasks to the test output schedule.

            // Set the head of the schedule linked list as taska1.
            ScheduleB testSchedule1 = new ScheduleB( taska1, inDegreeTable1);

            // Add the rest of the tasks to the linked list.
            testSchedule1 = new ScheduleB(testSchedule1, taskb1, inDegreeTable1);
            testSchedule1 = new ScheduleB(testSchedule1, taskc1, inDegreeTable1);
            testSchedule1 = new ScheduleB(testSchedule1, taskd1, inDegreeTable1);
            testSchedule1 = new ScheduleB(testSchedule1, taske1, inDegreeTable1);
            testSchedule1 = new ScheduleB(testSchedule1, taskf1, inDegreeTable1);
            testSchedule1 = new ScheduleB(testSchedule1, taskg1, inDegreeTable1);
            testSchedule1 = new ScheduleB(testSchedule1, taskh1, inDegreeTable1);

//            System.out.println(testSchedule1);

            // Make outputSchedule.
            OutputSchedule outputSchedule1 = new Solution(testSchedule1, 2);

//            System.out.println("Graph size " + testGraph1.getAllNodes().size());
//            System.out.println(" Task size " + outputSchedule1.getNumTasks());
//            System.out.println("Hello");

            // Should pass all three checks, isValid contains all three checks.
            assertEquals(false , OutputChecker.isValid(testGraph1, outputSchedule1));
        } catch (NullPointerException e){
        }
    }

}