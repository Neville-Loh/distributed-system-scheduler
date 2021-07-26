package main.java.raspberry.scheduler;

import java.util.*;

import main.java.raspberry.scheduler.algorithm.Schedule;
import main.java.raspberry.scheduler.graph.Graph;
import main.java.raspberry.scheduler.graph.Node;
import main.java.raspberry.scheduler.graph.Edge;
import main.java.raspberry.scheduler.algorithm.Astar;

public class Main {

    public static int NUM_NODE;

    public static void main(String[] args) {

        // This is unit test. (I will make proper Junit test later)
        test();
    }

    public static void test(){
        System.out.println("======== RUNNING TEST ========");

        Hashtable<Node, List<Edge>> table = makeHashTable2();
        Graph g = new Graph(table);
        Astar a = new Astar(g,7);
        NUM_NODE = table.size();
        System.out.printf("\n Number of NODES : %d \n", NUM_NODE);
        a.findPath();
    }

    public static Hashtable<Node, List<Edge>> makeHashTable(){
        Hashtable<Node, List<Edge>> adjacencyList = new Hashtable<Node, List<Edge>>();

        Node node_a = new Node('a', 2);
        Node node_b = new Node('b', 2);
        Node node_c = new Node('c', 2);
        Node node_d = new Node('d', 3);
        Node node_e = new Node('e', 2);
        Node node_f = new Node('f', 3);
        Node node_g = new Node('g', 2);

        Edge edge_a_b = new Edge(node_a,node_b, 1);
        Edge edge_a_c = new Edge(node_a,node_c, 3);
        Edge edge_a_d = new Edge(node_a,node_d, 1);

        Edge edge_b_e = new Edge(node_b,node_e, 3);
        Edge edge_b_g = new Edge(node_b,node_g, 4);

        Edge edge_c_f = new Edge(node_c,node_f, 1);
        Edge edge_d_f = new Edge(node_d,node_f, 1);
        Edge edge_e_g = new Edge(node_e,node_g, 2);
        Edge edge_f_g = new Edge(node_f,node_g, 2);

        List list_a = Arrays.asList(edge_a_b,edge_a_c,edge_a_d);
        adjacencyList.put(node_a, list_a);

        List list_b = Arrays.asList(edge_b_e,edge_b_g);
        adjacencyList.put(node_b, list_b);

        List list_c = Arrays.asList(edge_c_f);
        adjacencyList.put(node_c, list_c);

        List list_d = Arrays.asList(edge_d_f);
        adjacencyList.put(node_d, list_d);

        List list_e = Arrays.asList(edge_e_g);
        adjacencyList.put(node_e, list_e);

        List list_f = Arrays.asList(edge_f_g);
        adjacencyList.put(node_f, list_f);
        adjacencyList.put(node_g, new ArrayList<Edge>());
        return adjacencyList;
    }

    public static Hashtable<Node, List<Edge>> makeHashTable2(){
        Hashtable<Node, List<Edge>> adjacencyList = new Hashtable<Node, List<Edge>>();

        Node node_a = new Node('a', 2);
        Node node_b = new Node('b', 2);
        Node node_c = new Node('c', 2);
        Node node_d = new Node('d', 3);
        Node node_e = new Node('e', 2);
        Node node_f = new Node('f', 3);
        Node node_g = new Node('g', 2);
//        Node node_h = new Node('h', 2);
//        Node node_i = new Node('i', 2);
//        Node node_j = new Node('j', 2);
//        Node node_k = new Node('k', 3);
//        Node node_l = new Node('l', 2);
//        Node node_m = new Node('m', 3);
//        Node node_n = new Node('n', 2);

        adjacencyList.put(node_a, new ArrayList<Edge>());
        adjacencyList.put(node_b, new ArrayList<Edge>());
        adjacencyList.put(node_c, new ArrayList<Edge>());
        adjacencyList.put(node_d, new ArrayList<Edge>());
        adjacencyList.put(node_e, new ArrayList<Edge>());
        adjacencyList.put(node_f, new ArrayList<Edge>());
        adjacencyList.put(node_g, new ArrayList<Edge>());
//        adjacencyList.put(node_h, new ArrayList<Edge>());
//        adjacencyList.put(node_i, new ArrayList<Edge>());
//        adjacencyList.put(node_j, new ArrayList<Edge>());
//        adjacencyList.put(node_k, new ArrayList<Edge>());
//        adjacencyList.put(node_l, new ArrayList<Edge>());
//        adjacencyList.put(node_m, new ArrayList<Edge>());
//        adjacencyList.put(node_n, new ArrayList<Edge>());
        return adjacencyList;
    }
}
