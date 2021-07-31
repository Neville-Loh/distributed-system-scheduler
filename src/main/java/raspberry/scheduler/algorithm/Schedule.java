package main.java.raspberry.scheduler.algorithm;

import java.util.ArrayList;
import java.util.Arrays;

import main.java.raspberry.scheduler.graph.INode;

public class Schedule implements Comparable<Schedule>{

    // h: Heuristic weight
    // t: Total weight
    public int h;
    public int t;

    public int startTime; //the time this node start running.
    public int fisnishTime; //the time at this node finish running

//    public Schedule parent;
    public INode node;
    public int p_id;
//    public ArrayList<Schedule> path;
    public Schedule parent;
    public int size;

    public Schedule(int cost, int heuristic, Schedule parentSchedule, INode childNode, int processorId){
        node = childNode;
        p_id = processorId;

        startTime = cost;
        fisnishTime = cost + childNode.getValue();

        h = heuristic;
        t = fisnishTime + heuristic;

        parent = parentSchedule;
        if (parentSchedule == null){
            size = 1;
        }else{
            size = parentSchedule.size + 1;
        }
    }
//    public Schedule(char id){
//        char _id = id;
//    }

    @Override
    public int compareTo(Schedule s){
        return this.t > s.t ? 1 : this.t < s.t ? -1 : 0;
    }

    //After computing the scheduling, call this method to get List of paths
    public ArrayList<Schedule> getPath(){
        // TODO: implement
        if (this.parent == null){
            return new ArrayList<Schedule>( Arrays.asList(this) );
        }
        ArrayList<Schedule> p = this.parent.getPath();
        p.add(this);
        return p;
    }
}