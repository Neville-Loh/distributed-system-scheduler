package main.java.raspberry.scheduler.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import main.java.raspberry.scheduler.graph.Node;

public class Schedule implements Comparable<Schedule>{

    // h: Heuristic weight
    // t: Total weight
    public int h;
    public int t;

    public int s; //the time this node start running.
    public int f; //the time at this node finish running

//    public Schedule parent;
    public Node child;
    public int p_id;
    public ArrayList<Schedule> path;

    public Schedule(int cost, int heuristic, ArrayList<Schedule> parentSchedule, Node childNode, int processorId){
        child = childNode;
        p_id = processorId;

        s = cost;
        f = cost + childNode.getWeight();

        h = heuristic;
        t = f + heuristic;
        if (parentSchedule == null){
            path = new ArrayList<Schedule>();
        }else{
            path = new ArrayList<Schedule>(parentSchedule);
        }
        path.add(this);
    }

    @Override
    public int compareTo(Schedule s){
        return this.t > s.t ? 1 : this.t < s.t ? -1 : 0;
    }

    //After computing the scheduling, call this method to get List of paths
    public List<Schedule> getPath(){
        return this.path;
    }
}
