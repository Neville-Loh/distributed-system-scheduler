package main.java.raspberry.scheduler.graph;

import java.util.List;

public class Node implements INode{

    private int weight;

    public Node (int executionTime){
        weight = executionTime;
    }

    @Override
    public boolean hasParent() {
        //@todo Need to implement
        return false;
    }

    @Override
    public List<IEdge> getOutGoingEdges() {
        //@todo Need to implement
        return null;
    }

    @Override
    public int getValue() {
        return weight;
    }

    public void setValue(int val){
        this.weight = val;
    }
}
