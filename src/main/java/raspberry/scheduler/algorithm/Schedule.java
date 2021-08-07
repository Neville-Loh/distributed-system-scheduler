package raspberry.scheduler.algorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import raspberry.scheduler.graph.INode;

public class Schedule implements Comparable<Schedule>{

    // h: Heuristic weight
    // t: Total weight
    public int h;
    public int t;

    public int startTime; //the time this node start running.
    public int finishTime; //the time at this node finish running

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
        finishTime = cost + childNode.getValue();

        h = heuristic;
        t = finishTime + heuristic;

        parent = parentSchedule;
        if (parentSchedule == null){
            size = 1;
        }else{
            size = parentSchedule.size + 1;
        }
    }

    @Override
    public int compareTo(Schedule s){
        return this.t > s.t ? 1 : this.t < s.t ? -1 : 0;
    }


    //After computing the scheduling, call this method to get List of paths
    public Hashtable<INode, int[]> getPath(){
        Hashtable<INode, int[]> tmp;
        if (this.parent == null){
            tmp = new Hashtable<INode, int[]>();
        }else{
            tmp = this.parent.getPath();
        }
        tmp.put(this.node, new int[]{this.startTime,this.finishTime,this.p_id});
        return tmp;
    }
}