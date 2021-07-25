package main.java.raspberry.scheduler.graph;

import java.util.Hashtable;
import java.util.List;

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
        nodes.put(id,node);
    }

    @Override
    public void addEdge(String parentNodeID, String childNodeID, int weight) {
        INode p = nodes.get(parentNodeID);
        INode c = nodes.get(childNodeID);
        IEdge e = new Edge(nodes.get(parentNodeID), nodes.get(childNodeID), weight);
        InDegreeAdjacencyList.get(parentNodeID).add(e);
        OutDegreeAdjacencyList.get(childNodeID).add(e);
    }


    @Override
    public String toString(){
        return this.name;
    }


}
