package raspberry.scheduler.algorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Hashtable;

import raspberry.scheduler.graph.INode;

public class Schedule implements Comparable<Schedule>{

    public int _h; // h: Heuristic weight
    public int _t; // t: Total weight
    public int _startTime; //the time this node start running.
    public int _finishTime; //the time at this node finish running
    public INode _node;
    public int _pid;  //Processor Id
    public Schedule _parent; // Parent Schedule
    public int _size; // Size of the partial schedule. # of tasks scheduled.
    public Hashtable<String, List<Integer>> scheduling; // partial schedule. //TODO : Implement this idea with less memory intensive manner.
    public Hashtable<Integer, String> lastForEachProcessor; //the last task schedule, for each processor.
    public int maxPid; //The largest pid currently used to schedule

    /**
     * Constructor for partial schedule
     * @param startTime : the earliest start time a node can be scheudled.
     * @param parentSchedule : parent schedule
     * @param node : the task this partial schedule is scheduling.
     * @param processorId : id of a processor a node is being scheduled
     */
    public Schedule(int startTime,  Schedule parentSchedule, INode node, int processorId){
        _node = node;
        _pid = processorId;
        _startTime = startTime;
        _finishTime = startTime + node.getValue();
        _h = 0;
        _t = _finishTime + _h;
        _parent = parentSchedule;

        if (parentSchedule == null){
            _size = 1;
            scheduling = new Hashtable<String, List<Integer>>();
            lastForEachProcessor = new Hashtable<Integer, String>();
            maxPid = processorId;
        }else{
            if (processorId > parentSchedule.maxPid){
                maxPid = processorId;
            }else{
                maxPid = parentSchedule.maxPid;
            }
            _size = parentSchedule._size + 1;
            scheduling = (Hashtable<String, List<Integer>>) parentSchedule.scheduling.clone();
            lastForEachProcessor = (Hashtable<Integer, String>) parentSchedule.lastForEachProcessor.clone();
        }
        scheduling.put(node.getName(), Arrays.asList(processorId,startTime));
        lastForEachProcessor.put(processorId, node.getName());
    }

    /**
     * Since we calculate heuristic after the creation of Schedule instance, this was added.
     * Adds heuristic value
     * @param h : heuristic cost;
     */
    public void addHeuristic(int h){
        _h = h;
        _t = _finishTime + h;
    }

    /**
     * Compare two Schedule instance. Uses to put Schedule in priority Queue
     * @param s : A schedule to compare to
     * @return : 1 : if s's total weight is smaller,
     *          -1 : if s's total weight is bigger,
     *           0 : Two scheudle has same total weight.
     */
    @Override
    public int compareTo(Schedule s){
        return this._t > s._t ? 1 : this._t < s._t ? -1 : 0;
    }

    /**
     * Check if two Schedule instance is the same. (this is for detecting duplicate scheduling)
     * @param otherSchedule : the other schedule instance we are comparing to.
     * @return Boolean : True : if its the same.
     *                   False: if its different.
     */
    @Override
    public boolean equals(Object otherSchedule) {
        if (otherSchedule == this){
            return true;
        }else if ( !(otherSchedule instanceof Schedule) ){
            return false;
        }else{
            Schedule schedule = (Schedule)otherSchedule;
            if (schedule._size != schedule._size){
                return false;
            }else if(schedule.maxPid != schedule.maxPid){
                return false;
            }
            return this.scheduling.equals(schedule.scheduling);
        }
    }

    /**
     * Gets the full path of the partial schedule.
     * (as Schedule instance is linked with parents like linked list)
     * @return : Hashtable :  key : task (INode)
     *                      Value : List of Integers. ( size of 3 )
     *                              index 0 : start time of the task
     *                              index 1 : finsih time of the task
     *                              index 2 : processor id of the task.
     */
    public Hashtable<INode, int[]> getPath(){
        Hashtable<INode, int[]> tmp;
        if (_parent == null){
            tmp = new Hashtable<INode, int[]>();
        }else{
            tmp = _parent.getPath();
        }
        tmp.put(_node, new int[]{_startTime, _finishTime, _pid});
        return tmp;
    }

    /**
     * Get a hash value.
     * (I didnt override the ACTUAL hash function, I needed two kinds of hash function.
     *   v1 : perfect hash function that gurantees almost no collision & it is calculated using all variable contained in this class.
     *   v2 : hash function that produce some what unique hash value depending on the "scheduling" hashtable.
     *  )
     *  This function is v2.
     * @return int : representing the hash value of "scheduling" hashtable.
     */
    public int getHash() {
        final int prime = 31;
        int value = 0;
        for (String i: scheduling.keySet()){
            value = prime * value + ( scheduling.get(i).hashCode() );
            value = prime * value + ( i.hashCode() );
            value = prime * value + ( _size );
        }
        return value;
    }
}