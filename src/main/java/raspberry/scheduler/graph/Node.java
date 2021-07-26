package main.java.raspberry.scheduler.graph;

import java.util.Hashtable;

public class Node {

    private int weight;
    public char _id;

    // This is an temporary solution. As our adjacencyList is one directional.
    // Stores lists of parent nodes and its communicationWeight.
    public Hashtable<Node, Integer> parentCommunicationWeight;

    public Node (int executionTime){
        weight = executionTime;
        parentCommunicationWeight = new Hashtable<Node, Integer>();
    }

    // "_id" represents the node character. eg."a"
    public Node (char id, int executionTime){
        _id = id;
        weight = executionTime;
        parentCommunicationWeight = new Hashtable<Node, Integer>();
    }

    public int getWeight(){
        return weight;
    }

    // As stated in line 10-12, this is temporary solution.
    public void addParent(Node parentNode, int communicationWeight){
        parentCommunicationWeight.put(parentNode, communicationWeight);
    }
}
