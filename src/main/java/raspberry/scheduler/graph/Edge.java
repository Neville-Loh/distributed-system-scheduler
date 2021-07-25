package main.java.raspberry.scheduler.graph;

public class Edge implements IEdge{

    private INode parentNode;
    private INode childNode;
    private int weight;

    // Node : childNode
    // Weight : Remote communication cost when switching processors.
    public Edge(INode parent, INode child, int communicatonCost){
        parentNode = parent;
        childNode = child;
        weight = communicatonCost;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public INode getChild() {
        return childNode;
    }

    @Override
    public INode getParent() {
        return parentNode;
    }
}
