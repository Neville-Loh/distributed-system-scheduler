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
//    public ArrayList<Schedule> path;
    public Schedule parent;
    public int size;

    public int latest;

    public Schedule(int cost, Schedule parentSchedule, Node childNode, int processorId){
        child = childNode;
        p_id = processorId;

        s = cost;
        f = cost + childNode.getWeight();
        t = f;

        parent = parentSchedule;
        if (parentSchedule == null){
            size = 1;
            this.latest = f;
        }else{
            size = parentSchedule.size + 1;
            if ( f > parentSchedule.latest){
                this.latest = f;
            }else{
                this.latest = parentSchedule.latest;
            }
        }
    }

    // Used for A* algo
    public Schedule(int cost, int heuristic, Schedule parentSchedule, Node childNode, int processorId){
        child = childNode;
        p_id = processorId;

        s = cost;
        f = cost + childNode.getWeight();

        h = heuristic;
        t = f + heuristic;

        parent = parentSchedule;
        if (parentSchedule == null){
            size = 1;
        }else{
            size = parentSchedule.size + 1;
        }
    }

    public Schedule(char id){
        char _id = id;
    }

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
