package raspberry.scheduler.algorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import raspberry.scheduler.graph.INode;

public class Schedule2 implements Comparable<Schedule2>{

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
    public Schedule2 parent;
    public int size;
    public Hashtable<String, List<Integer>> scheduling;
    public int maxPid; //The largest pid currently used to schedule

    public Schedule2(int cost, int heuristic, Schedule2 parentSchedule, INode childNode, int processorId){
        node = childNode;
        p_id = processorId;

        startTime = cost;
        finishTime = cost + childNode.getValue();

        h = heuristic;
        t = finishTime + heuristic;

        parent = parentSchedule;
        if (parentSchedule == null){
            size = 1;
            scheduling = new Hashtable<String, List<Integer>>();
            maxPid = processorId;
        }else{
            if (processorId > parentSchedule.maxPid){
                maxPid = processorId;
            }else{
                maxPid = parentSchedule.maxPid;
            }
            size = parentSchedule.size + 1;
            scheduling = (Hashtable<String, List<Integer>>) parentSchedule.scheduling.clone();
        }

        List<Integer> thisTask = Arrays.asList(processorId,startTime);
        scheduling.put(childNode.getName(), thisTask);
    }

    public Schedule2(int cost,Schedule2 parentSchedule,  INode childNode,int processorId){
        scheduling = (Hashtable<String, List<Integer>>) parentSchedule.scheduling.clone();
        List<Integer> thisTask = Arrays.asList(processorId,cost);
        scheduling.put(childNode.getName(), thisTask);
    }

    @Override
    public int compareTo(Schedule2 s){
        return this.t > s.t ? 1 : this.t < s.t ? -1 : 0;
    }

    @Override
    public boolean equals(Object otherSchedule) {
        if (otherSchedule == this){
            return true;
        }else if ( !(otherSchedule instanceof Schedule2) ){
            return false;
        }else{
            Schedule2 schedule = (Schedule2)otherSchedule;
            if (schedule.size != schedule.size){
                return false;
            }else if(schedule.maxPid != schedule.maxPid){
                return false;
            }
            return this.scheduling.equals(schedule.scheduling);
        }
    }

//    public int getHash() {
//        final int prime = 7;
//        int value = 0;
//        for (String i: scheduling.keySet()){
//            value = prime * value + ( scheduling.get(i).hashCode() );
//            value = prime * value + ( i.hashCode() );
//        }
//        return value;
//    }
    public int getHash() {
        final int prime = 31;
        int value = 0;
        for (String i: scheduling.keySet()){
            value = prime * value + ( scheduling.get(i).hashCode() );
            value = prime * value + ( i.hashCode() );
            value = prime * value + ( size );
        }
        return value;
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