package main.java.raspberry.scheduler.algorithm;

import java.util.ArrayList;

import main.java.raspberry.scheduler.graph.Node;

public class Schedule implements Comparable<Schedule>{

    // g: cost of the path from the start node to n.
    // h: Heuristic weight
    // t: Total weight
    public int g;
    public int h;
    public int t;

    public Schedule parent;
    public Node child;
    public int p_id;

    public Schedule(int cost, int heuristic, int total, Schedule parentSchedule, Node childNode, int processorId){
        g = cost;
        h = heuristic;
        t = total;

        parent = parentSchedule;
        child = childNode;
        p_id = processorId;
    }

    @Override
    public int compareTo(Schedule s){
        return this.t > s.t ? 1 : this.t < s.t ? -1 : 0;
    }
}
