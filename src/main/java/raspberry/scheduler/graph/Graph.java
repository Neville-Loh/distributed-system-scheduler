package main.java.raspberry.scheduler.graph;

import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.ArrayList;

public class Graph {

    public Node startNode;
    public Node endNode;
    public Hashtable<Node, List<Edge>> adjacencyList;

    List<Node> toVisit;
    Stack topologicalOrder;

    public Graph(){
        adjacencyList = new Hashtable<Node, List<Edge>>();
    }

    // **** This constructor is used in Main.test() ****
    public Graph( Hashtable adjacencyList1){
        adjacencyList = adjacencyList1;
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


    // This path would be optimal solution for 1 processor scheduling.
    public Stack getTopologicalOrder_DFS(){
        //Compute topological order and return it.
        toVisit = new ArrayList<Node>( adjacencyList.keySet() );
        topologicalOrder = new Stack();
        while ( ! toVisit.isEmpty() ){
            recursiveTopological( toVisit.get(0) );
        }
        return topologicalOrder;
    }

    // Recursive function to compute topological order.
    public void recursiveTopological(Node x){
        if ( ! adjacencyList.get(x).isEmpty() ){
            for ( Edge i : adjacencyList.get(x) ){
                if ( toVisit.contains(i.childNode)) {
                    recursiveTopological(i.childNode);
                }
            }
        }
        toVisit.remove(x);
        topologicalOrder.push(x);
    }

    public void getTopologicalOrder_BFS(){
        //Yet to be implemented.
        return;
    }

}
