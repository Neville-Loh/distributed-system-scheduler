package main.java.raspberry.scheduler.algorithm;

import java.util.PriorityQueue;

import main.java.raspberry.scheduler.graph.Graph;

import java.util.ArrayList;
import java.util.List;

public class Astar implements Algorithm{

    Graph graph;
    PriorityQueue pq;
    List<Node> visted;
    int numP;
    List<Schedule> visited;

    public Astar(Graph graphToSolve, int numProcessors){
        graph = graphToSolve;
        pq = new PriorityQueue();
        visited = new ArrayList<Schedulle>();
        numP = numProcessors;
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

        List<Node> topologicalOrder = new ArrayList<node>(graph.getTopologicalOrder_DFS());


    }

    // Compute heuristic weight
    // Currently our heurstic function is undecided. --> just returns 0.
    public int h(){
        return 0;
    }

}
