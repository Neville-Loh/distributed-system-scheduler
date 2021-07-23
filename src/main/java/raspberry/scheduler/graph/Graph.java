package main.java.raspberry.scheduler.graph;

import java.util.Hashtable;
import java.util.List;

public class Graph {

    public Node startNode;
    public Node endNode;
    public Hashtable<Node, List<Edge>> adjacencyList;

    public Graph(){
        adjacencyList = new Hashtable<Node, List<Edge>>();
    }

    // Add element to hashtable. Call when creating the adjacency list.
    public void addChild (Node parentNode, List<Edge> edges){
        adjacencyList.put(parentNode, edges);
    }


    // Returns child nodes
    // x : the parent node.
    public List<Edge> getChild(Node x){
        return adjacencyList.get(x);
    }
}
