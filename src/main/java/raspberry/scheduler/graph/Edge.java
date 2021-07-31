package main.java.raspberry.scheduler.graph;

public class Edge implements IEdge{

    private INode parentNode;
    private INode childNode;
    private int weight;

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
    
    @Override
    public String toString() {
    	return String.format("(pointsto=%s, weight=%d)", childNode.getName(), weight);
//=======
//    //  ***  Temporary, we will only be using this constructor. ***???????????????
//    public Edge(Node parent, Node child, int communicatonCost){
//        childNode = child;
//        child.addParent(parent, communicatonCost);
//>>>>>>> master
    }
}