
package raspberry.scheduler;
import raspberry.scheduler.algorithm.BNB;
import raspberry.scheduler.algorithm.*;
import raspberry.scheduler.graph.Graph;


public class Main {

    public static int NUM_NODE;

    public static void main(String[] args) {

        // This is unit test. (I will make proper Junit test later)
        long startTime = System.nanoTime();
        //test_Astar();
        long endTime = System.nanoTime();
        System.out.printf("\n===== TOTAL RUNNING TIME : %.2f seconds=====", (endTime - startTime) / 1000000000.0);
    }


    public static void test_Astar() {
//        System.out.println("======== RUNNING Astar ========");
//
//        Graph g = new Graph("test graph");
//        makeGraph(g);
//        Astar a = new Astar(g,2);
//
//        a.findPath();
    }
}

