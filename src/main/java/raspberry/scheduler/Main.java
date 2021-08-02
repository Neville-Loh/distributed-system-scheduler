
package raspberry.scheduler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import raspberry.scheduler.algorithm.Astar;
import raspberry.scheduler.algorithm.MemoryBoundAStar;
import raspberry.scheduler.algorithm.OutputSchedule;
import raspberry.scheduler.graph.EdgeDoesNotExistException;
import raspberry.scheduler.graph.Graph;


public class Main {

    public static int NUM_NODE;

    public static void main(String[] args) throws EdgeDoesNotExistException {

        // This is unit test. (I will make proper Junit test later)
        test();
    }

    public static void test() throws EdgeDoesNotExistException {
        System.out.println("======== RUNNING TEST ========");


        // with h()=0, -> 692 node in pq.
        // with h( return sum(unscheduled node) ) -> 17
        //Hashtable<Node, List<Edge>> table = makeHashTable();
        Graph g = new Graph("test graph");
        makeGraph(g);
        Astar a = new Astar(g,8);
        MemoryBoundAStar mba = new MemoryBoundAStar(g,2);
        NUM_NODE = 7;
        System.out.printf("\n Number of NODES : %d \n", NUM_NODE);
        OutputSchedule output = mba.findPath();
        TestSchedule s = new TestSchedule(g, output);
        System.out.println("Is correct schedule: " +s.isValid());

    }

    private static void makeGraph(Graph graph) {
        graph.addNode("a", 2);
        graph.addNode("b", 2);
        graph.addNode("c", 2);
        graph.addNode("d", 3);
        graph.addNode("e", 2);
        graph.addNode("f", 3);
        graph.addNode("g", 1);


        graph.addEdge("a", "b",1);
        graph.addEdge("a", "c",3);
        graph.addEdge("a", "d",1);

        graph.addEdge("b", "e",3);
        graph.addEdge("b", "g",4);

        graph.addEdge("c", "f",1);
        graph.addEdge("d", "f",1);
        graph.addEdge("e", "g",2);
        graph.addEdge("f", "g",2);


        graph.addNode("i", 5);
        graph.addNode("j", 6);
        graph.addNode("k", 4);

        graph.addNode("i2", 1);
        graph.addNode("j3", 1);
        graph.addNode("k4", 1);
        graph.addEdge("i", "f",3);
        graph.addEdge("d", "j",6);
        graph.addEdge("d", "k",1);
        graph.addEdge("i2", "i",1);
        graph.addEdge("j3", "j",6);
        graph.addEdge("k4", "j3",1);
        graph.addEdge("k4", "j",1);



    }

//    public static Hashtable<Node, List<Edge>> makeHashTable(){
//        Hashtable<Node, List<Edge>> adjacencyList = new Hashtable<Node, List<Edge>>();
//
//        Node node_a = new Node('a', 2);
//        Node node_b = new Node('b', 2);
//        Node node_c = new Node('c', 2);
//        Node node_d = new Node('d', 3);
//        Node node_e = new Node('e', 2);
//        Node node_f = new Node('f', 3);
//        Node node_g = new Node('g', 2);
//
//        Edge edge_a_b = new Edge(node_a,node_b, 1);
//        Edge edge_a_c = new Edge(node_a,node_c, 3);
//        Edge edge_a_d = new Edge(node_a,node_d, 1);
//
//        Edge edge_b_e = new Edge(node_b,node_e, 3);
//        Edge edge_b_g = new Edge(node_b,node_g, 4);
//
//        Edge edge_c_f = new Edge(node_c,node_f, 1);
//        Edge edge_d_f = new Edge(node_d,node_f, 1);
//        Edge edge_e_g = new Edge(node_e,node_g, 2);
//        Edge edge_f_g = new Edge(node_f,node_g, 2);
//
//        List list_a = Arrays.asList(edge_a_b,edge_a_c,edge_a_d);
//        adjacencyList.put(node_a, list_a);
//
//        List list_b = Arrays.asList(edge_b_e,edge_b_g);
//        adjacencyList.put(node_b, list_b);
//
//        List list_c = Arrays.asList(edge_c_f);
//        adjacencyList.put(node_c, list_c);
//
//        List list_d = Arrays.asList(edge_d_f);
//        adjacencyList.put(node_d, list_d);
//
//        List list_e = Arrays.asList(edge_e_g);
//        adjacencyList.put(node_e, list_e);
//
//        List list_f = Arrays.asList(edge_f_g);
//        adjacencyList.put(node_f, list_f);
//        adjacencyList.put(node_g, new ArrayList<Edge>());
//        return adjacencyList;
//    }

//    public static Hashtable<Node, List<Edge>> makeHashTable2(){
//        Hashtable<Node, List<Edge>> adjacencyList = new Hashtable<Node, List<Edge>>();
//
//        Node tmp;
//        for(char alphabet = 'a'; alphabet <='z'; alphabet++ ) {
//            tmp = new Node(alphabet,(int)((Math.random()+1)*7) );
//            adjacencyList.put(tmp, new ArrayList<Edge>());
//        }
//
//        return adjacencyList;
//    }
}