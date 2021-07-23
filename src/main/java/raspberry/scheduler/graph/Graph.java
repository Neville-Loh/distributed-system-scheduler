package main.java.raspberry.scheduler.graph;

import java.util.Hashtable;
import java.util.List;

public class Graph implements IGraph{

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

    @Override
    public INode getNode(String id) {
        //@todo Need to implement
        return null;
    }

    @Override
    public void addNode(INode node) {
        //@todo Need to implement
    }

    @Override
    public void addEdge(String parentNodeID, String childNodeID, int Weight) {
        //@todo Need to implement
    }


}
