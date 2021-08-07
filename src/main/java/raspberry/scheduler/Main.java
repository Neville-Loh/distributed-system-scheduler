
package raspberry.scheduler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import raspberry.scheduler.algorithm.*;
import raspberry.scheduler.graph.Edge;
import raspberry.scheduler.graph.Graph;
import raspberry.scheduler.graph.Node;
import raspberry.scheduler.io.Reader;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;


public class Main {

    public static int NUM_NODE;
//
    public static void main(String[] args) {

        // This is unit test. (I will make proper Junit test later)
        long startTime = System.nanoTime();
        test_Astar2();
        long endTime = System.nanoTime();
        System.out.println(endTime-startTime);
        //A2:10977208735
        //A3:3813091995
    }


    public static void test_Astar(){
        System.out.println("======== RUNNING Astar ========");

        Graph g = new Graph("test graph");
//        makeGraph(g);
        Astar a = new Astar(g,4);

        a.findPath();

    }

    public static void test_Astar2(){
        System.out.println("======== RUNNING Astar 2 ========");

        Graph g = new Graph("test graph");
        makeGraph(g);
        Astar2 a = new Astar2(g,4);

        a.findPath();
    }

    private static void makeGraph(Graph graph) {
        graph.addNode("0" , 50);
        graph.addNode("1" , 70);
        graph.addEdge("0" , "1", 9);
        graph.addNode("2" , 90);
        graph.addEdge("0" , "2", 7);
        graph.addNode("3" , 100);
        graph.addEdge("0" , "3", 4);
        graph.addNode("4" , 40);
        graph.addEdge("1" , "4", 10);
        graph.addNode("5" , 20);
        graph.addEdge("1" , "5", 7);
        graph.addNode("6" , 100);
        graph.addEdge("1" , "6", 5);
        graph.addNode("7" , 80);
        graph.addEdge("2" , "7", 5);
        graph.addNode("8" , 50);
        graph.addEdge("2" , "8", 3);
        graph.addNode("9" , 20);
        graph.addEdge("2" , "9", 10);
        graph.addNode("10" , 20);
        graph.addEdge("3" , "10", 4);
    }
    public static void makeGraph2(Graph graph){
        graph.addNode("1" , 57);
        graph.addNode("2" , 114);
        graph.addEdge("1" , "2", 10);
        graph.addNode("3" , 143);
        graph.addEdge("1" , "3", 5);
        graph.addNode("4" , 143);
        graph.addNode("5" , 114);
        graph.addEdge("1" , "5", 7);
        graph.addNode("6" , 128);
        graph.addEdge("1" , "6", 9);
        graph.addNode("7" , 43);
        graph.addEdge("1" , "7", 7);
        graph.addNode("8" , 43);
        graph.addEdge("1" , "8", 2);
        graph.addNode("9" , 29);
        graph.addEdge("1" , "9", 5);
        graph.addNode("10" , 57);
        graph.addEdge("1" , "10", 11);
        graph.addNode("11" , 43);
        graph.addEdge("10" , "11", 12);
        graph.addNode("12" , 81);
        graph.addEdge("10" , "12", 15);
        graph.addNode("13" , 67);
        graph.addEdge("10" , "13", 8);
        graph.addNode("14" , 77);
        graph.addEdge("10" , "14", 12);
        graph.addEdge("13" , "14", 3);
        graph.addEdge("12" , "14", 4);
        graph.addNode("15" , 17);
        graph.addEdge("14" , "15", 3);
        graph.addEdge("11" , "15", 5);
        graph.addNode("16" , 47);
        graph.addEdge("15" , "16", 8);
        graph.addEdge("2" , "10", 6);
        graph.addEdge("4" , "10", 5);
        graph.addEdge("5" , "10", 3);
        graph.addEdge("6" , "10", 2);
        graph.addEdge("8" , "10", 6);
        graph.addEdge("9" , "10", 7);
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