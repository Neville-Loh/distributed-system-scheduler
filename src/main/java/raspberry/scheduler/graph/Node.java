package main.java.raspberry.scheduler.graph;

import java.util.List;
import java.util.Hashtable;

public class Node implements INode{

    private String name;
    private int weight;
    public char _id;

    public Node(String name, int executionTime){
        this.name = name;
        this.weight = executionTime;
    }

    @Override
    public int getValue() {
        return weight;
    }

    public void setValue(int val){
        this.weight = val;
    }

    @Override
    public String toString(){
        return this.name;
    }

    public String getName(){
        return this.name;
    }
}