
package raspberry.scheduler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import raspberry.scheduler.algorithm.Astar;
import raspberry.scheduler.algorithm.OutputSchedule;
import raspberry.scheduler.graph.Edge;
import raspberry.scheduler.graph.Graph;
import raspberry.scheduler.graph.Node;
import raspberry.scheduler.io.Reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;


public class Main {

    public static int NUM_NODE;
//
    public static void main(String[] args) {

        // This is unit test. (I will make proper Junit test later)
        test();
    }
//
    public static void test(){
        System.out.println("======== RUNNING TEST ========");


        // with h()=0, -> 692 node in pq.
        // with h( return sum(unscheduled node) ) -> 17
        //Hashtable<Node, List<Edge>> table = makeHashTable();
        Graph g = new Graph("test graph");
        makeGraph2(g);
        Astar a = new Astar(g,2);

        a.findPath();
//        OutputSchedule x = a.findPath();
//        System.out.printf("FinishTime: %d",x.getFinishTime());
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
    public static void makeGraph2(Graph graph){
        Hashtable<Node, List<Edge>> adjacencyList = new Hashtable<Node, List<Edge>>();

        graph.addNode("1", 57);
        graph.addNode("2", 114);
        graph.addNode("3", 143);
        graph.addNode("4", 143);
        graph.addNode("5", 114);
        graph.addNode("6", 128);
        graph.addNode("7", 43);
        graph.addNode("8", 43);
        graph.addNode("9", 29);
        graph.addNode("10", 57);
        graph.addNode("11", 43);
        graph.addNode("12", 81);
        graph.addNode("13", 67);
        graph.addNode("14", 77);
        graph.addNode("15", 17);
        graph.addNode("16", 47);

        graph.addEdge("1", "2",10);
        graph.addEdge("1", "3",5);
        graph.addEdge("1", "5",7);
        graph.addEdge("1", "6",9);
        graph.addEdge("1", "7",7);
        graph.addEdge("1", "8",2);
        graph.addEdge("1", "9",5);
        graph.addEdge("1", "10",11);
        graph.addEdge("10", "11",12);
        graph.addEdge("10", "12",15);
        graph.addEdge("10", "13",8);
        graph.addEdge("10", "14",12);
        graph.addEdge("13", "14",3);
        graph.addEdge("12", "14",4);
        graph.addEdge("14", "15",3);
        graph.addEdge("11", "15",5);
        graph.addEdge("15", "16",8);
        graph.addEdge("2", "10",6);
        graph.addEdge("2", "10",5);
        graph.addEdge("4", "10",3);
        graph.addEdge("6", "10",2);
        graph.addEdge("8", "10",6);
        graph.addEdge("9", "10",7);
    }

    public static void makeGraph3(Graph graph){
        graph.addNode("0", 10);
        graph.addNode("1", 7);
        graph.addNode("2", 6);
        graph.addNode("3", 7);
        graph.addNode("4", 5);
        graph.addNode("5", 9);
        graph.addNode("6", 2);
        graph.addNode("7", 2);
        graph.addNode("8", 7);

        graph.addEdge("0", "2",6);
        graph.addEdge("0", "3",7);
        graph.addEdge("0", "4",44);
        graph.addEdge("2", "6",59);
        graph.addEdge("2", "7",15);
        graph.addEdge("2", "8",59);
        graph.addEdge("3", "1",59);
        graph.addEdge("4", "1",66);
        graph.addEdge("5", "1",37);
        graph.addEdge("6", "5",22);

        graph.addEdge("7", "5",59);
        graph.addEdge("8", "5",59);
    }

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