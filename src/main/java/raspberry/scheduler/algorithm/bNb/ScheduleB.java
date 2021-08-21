package raspberry.scheduler.algorithm.bNb;

import java.util.*;

import raspberry.scheduler.algorithm.common.ScheduledTask;
import raspberry.scheduler.graph.INode;


/**
 * The Schedule class represents a partial schedule of the tasks
 * Schedule has a Linked list implementation connected to parents
 *
 * @author Takahiro
 */
public class ScheduleB implements Comparable<ScheduleB> {

    private ScheduleB _parent; // Parent Schedule
    private int _size; // Size of the partial schedule. # of tasks scheduled.

    private ScheduledTask _scheduleTask;


    private int _overallFinishTime; // t: Total weight
    private int _maxPid; //The largest pid currently used to schedule. This ranges from 1 ~ n. (not 0 ~ n-1)
    private Hashtable<INode, Integer> _inDegreeTable;

    private int _upperBound;    // For BNB. Represents the worst case. <- Bad schedling.
    private int _lowerBound;   // For BNB. Represents the base case. <- perfect schedling.


    /**
     * Constructor for partial schedule
     *
     */
    public ScheduleB(ScheduleB parent ,ScheduledTask scheduleTask, Hashtable<INode, Integer> inDegreeTable) {
        _parent = parent;
        _scheduleTask = scheduleTask;
        _inDegreeTable = inDegreeTable;
        if (parent == null) {
            _size = 1;
            _maxPid = scheduleTask.getProcessorID();
            _overallFinishTime = scheduleTask.getFinishTime();
        } else {
            if (scheduleTask.getProcessorID() > parent.getMaxPid()) {
                _maxPid = scheduleTask.getProcessorID();
            } else {
                _maxPid = parent.getMaxPid();
            }
            _size = parent.getSize() + 1;
            _overallFinishTime = Math.max( parent._overallFinishTime, scheduleTask.getFinishTime() );
        }
    }

    public int getPid(){
        return _scheduleTask.getProcessorID();
    }

    public void addLowerBound(int l) {
        if ( _parent == null){
            _lowerBound = _scheduleTask.getFinishTime();
        }else{
            _lowerBound = Math.max( _parent.getLowerBound(), l);
        }
    }

    /**
     * Compare two Schedule instance. Uses to put Schedule in priority Queue
     *
     * @param schedule : A schedule to compare to
     * @return : 1 : if s's total weight is smaller,
     * -1 : if s's total weight is bigger,
     * 0 : Two scheudle has same total weight.
     */
    @Override
    public int compareTo(ScheduleB schedule) {
        return _lowerBound > schedule.getLowerBound() ? 1 : _lowerBound < schedule.getLowerBound() ? -1 : 0;
    }


    /**
     * Check if two Schedule instance is the same. (this is for detecting duplicate scheduling)
     *
     * @param otherSchedule : the other schedule instance we are comparing to.
     * @return Boolean : True : if its the same.
     * False: if its different.
     */
//    @Override
    public boolean equals2(Object otherSchedule) {
        if (otherSchedule == this) {
            return true;
        } else if (!(otherSchedule instanceof ScheduleB)) {
            return false;
        } else {
            ScheduleB oSchedule = (ScheduleB) otherSchedule;
            if ( getSize() != oSchedule.getSize()) {
                return false;
            } else if ( getMaxPid() != oSchedule.getMaxPid()) {
                return false;
            } else {
                // Group by pid. Compare match
                Hashtable<INode, int[]> scheduling = getPath();
                Hashtable<INode, int[]> scheduling2 = oSchedule.getPath();

                Hashtable<Integer, Hashtable<String, Integer>> hash4scheduling = new Hashtable<Integer, Hashtable<String, Integer>>();
                Hashtable<Integer, Hashtable<String, Integer>> hash4scheduling2 = new Hashtable<Integer, Hashtable<String, Integer>>();

                for (INode s : scheduling.keySet()) {
                    Hashtable<String, Integer> tmp = hash4scheduling.get(scheduling.get(s)[2]); //get(0) gets pid
                    if (tmp == null) {
                        tmp = new Hashtable<String, Integer>();
                    }
                    tmp.put(s.getName(), scheduling.get(s)[0]);
                    hash4scheduling.put( scheduling.get(s)[2], tmp );
                }
                for (INode s : scheduling2.keySet()) {
                    Hashtable<String, Integer> tmp = hash4scheduling2.get(scheduling2.get(s)[2]); //get(0) gets pid
                    if (tmp == null) {
                        tmp = new Hashtable<String, Integer>();
                    }
                    tmp.put(s.getName(), scheduling2.get(s)[0]);
                    hash4scheduling2.put( scheduling2.get(s)[2], tmp );
                }

                for (Hashtable<String, Integer> i : hash4scheduling.values()) {
                    Boolean foundMatch = false;
                    for (Hashtable<String, Integer> j : hash4scheduling2.values()) {
                        if (i.equals(j)) {
                            foundMatch = true;
                            break;
                        }
                    }
                    if (!foundMatch) {
                        return false;
                    }
                }
            }
//            printPath( this.getPath() );
//            printPath( oSchedule.getPath());
            return true;
        }
    }

    public void printPath( Hashtable<INode, int[]> x){
        String r ="";
        for (INode i : x.keySet()){
            r +=  "{Task:"+  i.getName() + "-pid:" + x.get(i)[2] + "-t:" + x.get(i)[0] + "}";
        }
        System.out.println(r);
    }

    //Risky version of equals. Dont know if this actually outputs optimal path.
    public boolean equals3(Object otherSchedule) {
        if (otherSchedule == this) {
            return true;
        } else if (!(otherSchedule instanceof ScheduleB)) {
            return false;
        } else {
            ScheduleB oSchedule = (ScheduleB) otherSchedule;
            if (oSchedule.getSize()!= getSize()) {
                return false;
            } else if (oSchedule.getMaxPid() != getMaxPid()) {
                return false;
            } else {
                // Group by pid. Compare match
                Set<int[]> setSchedule = getTaskForEqual();
                Set<int[]> setOtherSchedule = oSchedule.getTaskForEqual();
                if ( setSchedule.equals(setOtherSchedule) ){
                    System.out.println( setOtherSchedule );
                    System.out.println( setSchedule );
                }
                return setSchedule.equals(setOtherSchedule);
            }
        }
    }

    public Set<int[]> getTaskForEqual(){
        Set<int[]> tmp;
        if (_parent == null) {
            tmp = new HashSet< int[] >();
        } else {
            tmp = _parent.getTaskForEqual();
        }
        tmp.add( new int[]{_scheduleTask.getStartTime(), _scheduleTask.getTask().getName().hashCode()});
        return tmp;
    }

    /**
     * Gets the full path of the partial schedule.
     * (as Schedule instance is linked with parents like linked list)
     *
     * @return : Hashtable :  key : task (INode)
     * Value : List of Integers. ( size of 3 )
     * index 0 : start time of the task
     * index 1 : finsih time of the task
     * index 2 : processor id of the task.
     */
    public Hashtable<INode, int[]> getPath() {
        Hashtable<INode, int[]> tmp;
        if (_parent == null) {
            tmp = new Hashtable<INode, int[]>();
        } else {
            tmp = _parent.getPath();
        }
        tmp.put(_scheduleTask.getTask(),
                new int[]{_scheduleTask.getStartTime(), _scheduleTask.getFinishTime(), _scheduleTask.getProcessorID()});
        return tmp;
    }

    /**
     * Get a hash value.
     * (I didnt override the ACTUAL hash function, I needed two kinds of hash function.
     * v1 : perfect hash function that gurantees almost no collision & it is calculated using all variable contained in this class.
     * v2 : hash function that produce some what unique hash value depending on the "scheduling" hashtable.
     * )
     * This function is v2.
     *
     * @return int : representing the hash value of "scheduling" hashtable.
     */
    public int getHash() {
        final int prime = 17;
        int value = 0;
        Hashtable<INode, int[]> scheduling = getPath();
        for (INode i : scheduling.keySet()) {
            value = prime * value + (scheduling.get(i)[0]);
            value = prime * value + (i.getName().hashCode()); //Might be fine just doing i.hasCode()
        }
        value = prime * value + (_size);
        return value;
    }

    /*
    Getter and Setters
     */

    /**
     * get Total weight
     *
     * @return _total
     */
    public int getOverallFinishTime() {
        return _overallFinishTime;
    }

//    /**
//     * get Start Time the time this node start running.
//     *
//     * @return _startTime the time this node start running.
//     */
//    public int getStartTime() {
//        return _scheduleTask.getStartTime();
//    }

    /**
     * get finish time the time at this node finish running
     *
     * @return _finishTime the time at this node finish running
     */
    public int getFinishTime() {
        return _scheduleTask.getFinishTime();
    }

    /**
     * get node being scheduled
     *
     * @return _node the node being  scheduled
     */
    public INode getNode() {
        return _scheduleTask.getTask();
    }

    /**
     * get parent schedule the Parent Schedule
     *
     * @return _parent the Parent Schedule
     */
    public ScheduleB getParent() {
        return _parent;
    }

    /**
     * get size Size of the partial schedule. # of tasks scheduled.
     *
     * @return _size Size of the partial schedule. # of tasks scheduled.
     */
    public int getSize() {
        return _size;
    }

    public int getLowerBound(){
        return _lowerBound;
    }
    /**
     * get max pid The largest pid currently used to schedule
     *
     * @return _maxPid The largest pid currently used to schedule
     */
    public int getMaxPid() {
        return _maxPid;
    }

    public Hashtable<INode, Integer> getIndegreeTable(){
        return _inDegreeTable;
    }

}