package main.java.raspberry.scheduler.graph;

public class Edge {

    private Node childNode;
    private int weight;

    // Node : childNode
    // Weight : Remote communication cost when switching processors.
    public Edge(Node child, int communicatonCost){
        childNode = child;
        weight = communicatonCost;
    }


}
