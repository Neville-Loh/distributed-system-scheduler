package main.java.raspberry.scheduler.graph;

public class Node {

    private int weight;

    public Node (int executionTime){
        weight = executionTime;
    }

    public int getWeight(){
        return weight;
    }
}
