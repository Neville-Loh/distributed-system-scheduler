package main.java.raspberry.scheduler.graph;

import java.util.Hashtable;
import java.util.List;
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
    public String toString(){
    	String output = "Graph: " +this.name +"\n";
    	for (String name: InDegreeAdjacencyList.keySet()) {
    	    String key = name.toString();
    	    String value = InDegreeAdjacencyList.get(name).toString();
    	    output += "Node:" + key + " cost=" + nodes.get(key).getValue()  + " " + value + "\n";
    	}
    	return output;
    }


}
