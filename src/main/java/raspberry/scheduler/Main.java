
package raspberry.scheduler;
import raspberry.scheduler.algorithm.BNB;
import raspberry.scheduler.graph.Graph;


public class Main {

    public static int NUM_NODE;

    public static void main(String[] args) {

        // This is unit test. (I will make proper Junit test later)
        test();
    }

    public static void test(){
        System.out.println("======== RUNNING TEST ========");
        Graph g = new Graph("test graph");
        makeGraph2(g);
        BNB a = new BNB(g,2);
        NUM_NODE = 7;
        System.out.printf("\n Number of NODES : %d \n", NUM_NODE);

        a.findPath();
    }

    private static void makeGraph(Graph graph) {
        graph.addNode("a", 2);
        graph.addNode("b", 2);
        graph.addNode("c", 2);
        graph.addNode("d", 3);
        graph.addNode("e", 2);
        graph.addNode("f", 3);
        graph.addNode("g", 2);

        graph.addEdge("a", "b",1);
        graph.addEdge("a", "c",3);
        graph.addEdge("a", "d",1);

        graph.addEdge("b", "e",3);
        graph.addEdge("b", "g",4);

        graph.addEdge("c", "f",1);
        graph.addEdge("d", "f",1);
        graph.addEdge("e", "g",2);
        graph.addEdge("f", "g",2);
    }

    private static void makeGraph2(Graph graph) {
        graph.addNode("0" , 6);
        graph.addNode("3" , 10);
        graph.addEdge("0" , "3", 34);
        graph.addNode("4" , 3);
        graph.addEdge("0" , "4", 24);
        graph.addNode("9" , 8);
        graph.addEdge("0" , "9", 44);
        graph.addNode("1" , 5);
        graph.addNode("2" , 5);
        graph.addEdge("1" , "2", 48);
        graph.addNode("5" , 7);
        graph.addEdge("1" , "5", 19);
        graph.addNode("6" , 8);
        graph.addEdge("1" , "6", 39);
        graph.addEdge("2" , "3", 10);
        graph.addNode("7" , 3);
        graph.addEdge("2" , "7", 48);
        graph.addNode("8" , 8);
        graph.addEdge("2" , "8", 48);
        graph.addEdge("4" , "6", 10);
        graph.addEdge("4" , "7", 48);
        graph.addEdge("4" , "8", 48);
        graph.addEdge("4" , "9", 39);
        graph.addEdge("6" , "7", 15);
        graph.addEdge("6" , "8", 39);
        graph.addEdge("6" , "9", 29);
        graph.addEdge("7" , "8", 15);
        graph.addEdge("7" , "9", 34);
        graph.addEdge("8" , "9", 39);
    }