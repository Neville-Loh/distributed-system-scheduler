package main.java.raspberry.scheduler.graph;

public class Edge {

    public Node childNode;

    //Since we added "parentCommunicationWeight" in Node-Class, this is unnecessary.
    //private int weight;


    // Node : childNode
    // Weight : Remote communication cost when switching processors.
    public Edge(Node child, int communicatonCost){
        childNode = child;
//        weight = communicatonCost;
    }

    //  ***  Temporary, we will only be using this constructor. ***
    public Edge(Node parent, Node child, int communicatonCost){
        childNode = child;
        child.addParent(parent, communicatonCost);
    }
}
