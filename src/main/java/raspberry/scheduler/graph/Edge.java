package main.java.raspberry.scheduler.graph;

public class Edge implements IEdge{

    private Node childNode;
    private int weight;

    // Node : childNode
    // Weight : Remote communication cost when switching processors.
    public Edge(Node child, int communicatonCost){
        childNode = child;
        weight = communicatonCost;
    }


    @Override
    public int getWeight() {
        //@todo Need to implement
        return 0;
    }

    @Override
    public INode getChild() {
        //@todo Need to implement
        return null;
    }

    @Override
    public INode getParent() {
        //@todo Need to implement
        return null;
    }
}
