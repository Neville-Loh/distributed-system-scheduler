package main.java.raspberry.scheduler.algorithm;

import java.util.PriorityQueue;

import main.java.raspberry.scheduler.graph.Graph;
import main.java.raspberry.scheduler.graph.INode;

import java.util.ArrayList;
import java.util.List;

public class Astar implements Algorithm{

    Graph graph;
    PriorityQueue pq;
    List<INode> visted;

    public Astar(Graph graphToSolve){
        graph = graphToSolve;
        pq = new PriorityQueue();
        visted = new ArrayList<INode>();
    }


    @Override
    public void findPath() {
        // find the path


        // Put start node in "visted"
        // currentNode = start node

        // while loop {
        // - for all child node of currentNode {
        // - - make new "Schedule" for every combination of processor assignment.
        // - - compute weight
        // - - put it into priorityQueue
        // - }
        // - currentNode = ( pop from priority queue ).child
        //
        // - If currentNode is endNode {
        // - - BREAK
        // - }
        // }
    }
}
