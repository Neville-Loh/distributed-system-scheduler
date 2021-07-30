package main.java.raspberry.scheduler.graph;

import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.ArrayList;

public class Graph implements IGraph{
    private String name;
    public Hashtable<String, INode> nodes;
    public Hashtable<String, List<IEdge>> InDegreeAdjacencyList;
    public Hashtable<String, List<IEdge>> OutDegreeAdjacencyList;

    /**
     * Constructor
     * @param name name of the graph
     */
    public Graph(String name){
        this.name = name;
        nodes = new Hashtable<String, INode>();
        InDegreeAdjacencyList = new Hashtable<String, List<IEdge>>();
        OutDegreeAdjacencyList = new Hashtable<String, List<IEdge>>();
    }

    @Override
    public INode getNode(String id) {
        return nodes.get(id);
    }

    @Override
    public void addNode(String id, int value) {
        INode node = new Node(id, value);
        InDegreeAdjacencyList.put(id, new ArrayList<IEdge>());
        OutDegreeAdjacencyList.put(id, new ArrayList<IEdge>());
        nodes.put(id,node);
    }

    @Override
    public void addEdge(String parentNodeID, String childNodeID, int weight) {
        INode p = nodes.get(parentNodeID);
        INode c = nodes.get(childNodeID);
        IEdge e = new Edge(p, c, weight);
        InDegreeAdjacencyList.get(parentNodeID).add(e);
        OutDegreeAdjacencyList.get(childNodeID).add(e);
    }

    @Override
    public List<IEdge> getOutgoingEdges(String id) {
        return OutDegreeAdjacencyList.get(id);
    }

    @Override
    public int getEdgeWeight(INode parent, INode child) throws EdgeDoesNotExistException {
        for (IEdge edge : OutDegreeAdjacencyList.get(parent.getName())){
            if (edge.getChild() == child){
                return edge.getWeight();
            }
        }
        throw new EdgeDoesNotExistException("Edge does not exists");
    }


    @Override
    public String toString(){
    	String output = "Graph: " +this.name +"\n";
    	for (String name: InDegreeAdjacencyList.keySet()) {
    	    String key = name.toString();
    	    String value = InDegreeAdjacencyList.get(name).toString();
    	    output += "Node:" + key + " cost=" + nodes.get(key).getValue()  + " " + value + "\n";
    	}
    	return output;
    }

//    // This path would be optimal solution for 1 processor scheduling.
//    public Stack getTopologicalOrder_DFS(){
//        //Compute topological order and return it.
//        toVisit = new ArrayList<Node>( adjacencyList.keySet() );
//        topologicalOrder = new Stack();
//        while ( ! toVisit.isEmpty() ){
//            recursiveTopological( toVisit.get(0) );
//        }
//        return topologicalOrder;
//    }
//
//    // Recursive function to compute topological order.
//    public void recursiveTopological(Node x){
//        if ( ! adjacencyList.get(x).isEmpty() ){
//            for ( Edge i : adjacencyList.get(x) ){
//                if ( toVisit.contains(i.childNode)) {
//                    recursiveTopological(i.childNode);
//                }
//            }
//        }
//        toVisit.remove(x);
//        topologicalOrder.push(x);
//    }
//
//    public void getTopologicalOrder_BFS(){
//        //Yet to be implemented.
//        return;
//    }

}
