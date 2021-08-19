package raspberry.scheduler.algorithm.sma;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import raspberry.scheduler.algorithm.common.ScheduledTask;
import raspberry.scheduler.algorithm.Schedule;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.INode;

/**
 * Linked list implementation of schedule. Memory heavy and compute optimized version
 * of the normal variant.
 *
 * @author Neville
 */
public class MBSchedule implements Comparable<MBSchedule>, Iterable<MBSchedule>{
    public MBSchedule parent;
    private ScheduledTask _scheduledTask;
    private int _size;
    private int _fScore;
    private int _hScore;
    private Hashtable<INode, Integer> _parentsLeftOfSchedulableTask; //
    private int _overallFinishTime;

    // Manhattan heuristic specific attribute
    private int _earliestFinishProcessorID;
    private int _earliestFinishTimeOfAllProcessors;
    private int _remainingComputeTime;


    // SMA specific attribute
    private Hashtable<ScheduledTask,Integer> _forgotten;
    private int _minForgottenFScore;

    //NEW STUFF
    private Hashtable<String, List<Integer>> _scheduling; // partial schedule. /
    private int _maxPid; //The largest pid currently used to schedule
    private Hashtable<Integer, String> _lastForEachProcessor; //the last task schedule, for each processor.


    public MBSchedule(){
    }

    /**
     * Class constructor
     * @param parentSchedule
     * @param remainingComputeTime remaining compute time after input task has been scheduled
     */
    public MBSchedule(MBSchedule parentSchedule, int remainingComputeTime, ScheduledTask scheduledTask)  {
        // scheduled task value
        _scheduledTask = scheduledTask;
        _forgotten = new Hashtable<ScheduledTask, Integer>();

        // linked-list attribute
        parent = parentSchedule;
        _remainingComputeTime = remainingComputeTime;
        if (parentSchedule == null){
            _size = 1;
            _overallFinishTime = scheduledTask.getFinishTime();
            
            // new stuff
            _maxPid = scheduledTask.getProcessorID();
            _scheduling = new Hashtable<String, List<Integer>>();
            _lastForEachProcessor = new Hashtable<Integer, String>();
        }else{
            _size = parentSchedule.getSize() + 1;
            _overallFinishTime = Math.max(parent.getOverallFinishTime(), _scheduledTask.getFinishTime());

            // new stuff
            if (scheduledTask.getProcessorID() > parentSchedule.getMaxPid()) {
                _maxPid = scheduledTask.getProcessorID();
            } else {
                _maxPid = parentSchedule.getMaxPid();
            }
            _scheduling = (Hashtable<String, List<Integer>>) parentSchedule.getScheduling().clone();
            _lastForEachProcessor = (Hashtable<Integer, String>) parentSchedule.getLastForEachProcessor().clone();
        }

        // new stuff
        _scheduling.put(scheduledTask.getTask().getName(), Arrays.asList(scheduledTask.getProcessorID(), scheduledTask.getStartTime()));
        _lastForEachProcessor.put(scheduledTask.getProcessorID(), scheduledTask.getTask().getName());
    }

    //-----------------------------------------------------------------------------------------------


    
    public int getMaxPid(){
        return _maxPid;
    }

    /**
     * get _scheduling the partial schedule
     *
     * @return _scheduling the partial schedule
     */
    public Hashtable<String, List<Integer>> getScheduling() {
        return _scheduling;
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
        } else if (!(otherSchedule instanceof MBSchedule)) {
            return false;
        } else {
            MBSchedule schedule = (MBSchedule) otherSchedule;
            if ( _size != schedule.getSize()) {
                return false;
            } else if ( _maxPid != schedule.getMaxPid()) {
                return false;
            } else {
                // Group by pid. Compare match
                Hashtable<String, List<Integer>> _scheduling2 = schedule.getScheduling();

                Hashtable<Integer, Hashtable<String, Integer>> hash4scheduling = new Hashtable<Integer, Hashtable<String, Integer>>();
                Hashtable<Integer, Hashtable<String, Integer>> hash4scheduling2 = new Hashtable<Integer, Hashtable<String, Integer>>();

                for (String s : _scheduling.keySet()) {
                    Hashtable<String, Integer> tmp = hash4scheduling.get(_scheduling.get(s).get(0)); //get(0) gets pid
                    if (tmp == null) {
                        tmp = new Hashtable<String, Integer>();
                    }
                    tmp.put(s, _scheduling.get(s).get(1));
                    hash4scheduling.put( _scheduling.get(s).get(0), tmp );
                }
                for (String s : _scheduling2.keySet()) {
                    Hashtable<String, Integer> tmp = hash4scheduling2.get(_scheduling2.get(s).get(0)); //get(0) gets pid
                    if (tmp == null) {
                        tmp = new Hashtable<String, Integer>();
                    }
                    tmp.put(s, _scheduling2.get(s).get(1));
                    hash4scheduling2.put( _scheduling2.get(s).get(0), tmp );
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
            return true;
        }
    }

    //Risky version of equals. Dont know if this actually outputs optimal path.
    public boolean equals3(Object otherSchedule) {
        if (otherSchedule == this) {
            return true;
        } else if (!(otherSchedule instanceof Schedule)) {
            return false;
        } else {
            Schedule schedule = (Schedule) otherSchedule;
            if (this._size != schedule.getSize()) {
                return false;
            } else if (this._maxPid != schedule.getMaxPid()) {
                return false;
            } else {
                // Group by pid. Compare match
                Hashtable<String, List<Integer>> _scheduling2 = schedule.getScheduling();

                Set<HashSet<Integer>> hash4scheduling = new HashSet<HashSet<Integer>>();
                Set<HashSet<Integer>> hash4scheduling2 = new HashSet<HashSet<Integer>>();

                for (String s : _scheduling.keySet()) {
                    hash4scheduling.add(new HashSet<Integer>(Arrays.asList(s.hashCode(),_scheduling.get(s).get(1))));
                }
                for (String s : _scheduling2.keySet()) {
                    hash4scheduling2.add(new HashSet<Integer>(Arrays.asList(s.hashCode(),_scheduling2.get(s).get(1))));
                }
                return hash4scheduling.equals(hash4scheduling2);
            }
        }
    }


    //-----------------------------------------------------------------------------------------------



    /**
     * Create sub-schedule using the calling class as parent
     * @deprecated
     * @param scheduledTask task to be schedule
     * @return sub-schedule
     */
    public MBSchedule createSubSchedule(ScheduledTask scheduledTask){
        int remainingComputeTime = _remainingComputeTime - scheduledTask.getTask().getValue();
        return new MBSchedule(this, remainingComputeTime, scheduledTask);
    }

    /**
     * Create sub-schedule using the calling class as parent
     * @param scheduledTask scheduled task with start time
     * @param dependencyGraph dependencyGraph
     * @return sub-schedule
     */
    public MBSchedule createSubSchedule(ScheduledTask scheduledTask, IGraph dependencyGraph){
        int remainingComputeTime = _remainingComputeTime - scheduledTask.getTask().getValue();
        MBSchedule subSchedule = new MBSchedule(this, remainingComputeTime, scheduledTask);
        subSchedule.setParentsLeftOfSchedulableTask(
                subSchedule.parentsLeftsWithoutTask(scheduledTask.getTask(),dependencyGraph));
        return subSchedule;
    }

    /**
     * pop the child x, and recalculate dependency
     * @param dependencyGraph graph which contain the task dependency
     * @param task task to be scheduled
     * @return table after popping the child
     */
    public Hashtable<INode, Integer> parentsLeftsWithoutTask(INode task, IGraph dependencyGraph){
        Hashtable<INode, Integer> temp;
        if (parent!= null){
            temp = new Hashtable<INode, Integer>(parent.getParentsLeftOfSchedulableTask());
        } else {
            temp = dependencyGraph.getInDegreeCountOfAllNodes();
        }
        temp.remove(task);
        dependencyGraph.getOutgoingEdges(task.getName()).forEach( edge ->
                temp.put( edge.getChild(),  temp.get(edge.getChild()) - 1 ));
        return temp;
    }

    /**
     * After computing the scheduling, call this method to get List of paths
     * @return path
     */
    public Hashtable<INode, int[]> getPath(){
        Hashtable<INode, int[]> temp;
        if (this.parent == null){
            temp = new Hashtable<INode, int[]>();
        }else{
            temp = this.parent.getPath();
        }
        temp.put(_scheduledTask.getTask(),
                new int[]{_scheduledTask.getStartTime(),
                        _scheduledTask.getFinishTime(),
                        _scheduledTask.getProcessorID()});
        return temp;
    }


    /**
     * Forgets and back up sub-schedule in to the parents
     * @param subSchedule schedule to be forgotten
     */
    public void forget(MBSchedule subSchedule){
//        if (_forgotten == null){
//            _forgotten = new Hashtable<ScheduledTask, Integer>();
//            _minForgottenFScore = subSchedule.getFScore();
//        } else {
//            _minForgottenFScore = Math.min(_minForgottenFScore,subSchedule.getFScore());
//        }

        //subSchedule.setForgottenTableToNull();

        // compare if the schedule f score is lower than the forgotten f score in table
        _forgotten.put(subSchedule.getScheduledTask(), subSchedule.getFScore());
        int result = Integer.MAX_VALUE;
        for (ScheduledTask st : _forgotten.keySet()){
            result = Math.min(result, _forgotten.get(st));
        }
        _fScore = result;

//        if (_forgotten.containsKey(subSchedule.getScheduledTask())){
//            int minFScore = Math.min(_forgotten.get(subSchedule.getScheduledTask()), subSchedule.getFScore());
//            _forgotten.put(subSchedule, minFScore);
//        } else {
//            _forgotten.put(subSchedule, subSchedule.getFScore());
//        }

        //System.out.println("table: !!!!!!!!" + _forgotten);

    }



    /*
     * ============================================================
     * Comparator and Iterator method
     *
     * ============================================================
     */

    @Override
    public int compareTo(MBSchedule s){
        return Integer.compare(this._fScore, s.getFScore());
    }

    @Override
    public Iterator<MBSchedule> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super MBSchedule> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<MBSchedule> spliterator() {
        return Iterable.super.spliterator();
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//        if (obj.getClass() != this.getClass()) {
//            return false;
//        }
//        final MBSchedule other = (MBSchedule) obj;
//        if (this.parent != null && other.parent != null){
//            if (this.parent.equals(other.parent)){
//                return false;
//            }
//        } else if (this.parent == null && other.parent != null) {
//            return false;
//        }
//        return (this.getScheduledTask().equals(other.getScheduledTask()));
//    }



    /*
     * ============================================================
     *  To String and debug method
     *
     * ============================================================
     */

    /**
     * Print all attribute of MBSchedule
     */
    public void print(){
        System.out.println(  "f: " + _fScore +
             " processorId: " + _scheduledTask.getProcessorID()
             + " Task = " + _scheduledTask.getTask()
             + " startTime: " + _scheduledTask.getStartTime()
             + " finishTime: " + _scheduledTask.getFinishTime()
             + "   ||||| overall finish time: " +_overallFinishTime
             + "\nforgotten: " + _forgotten +"\n");
    }

    /**
     * Display the name and the path of the current mbSchedule
     * @return string
     */
    @Override
    public String toString() {
        String result = "f: " + _fScore + "   ";
        MBSchedule cSchedule = this;
        ArrayList<String> temp = new ArrayList<String>();

        while (cSchedule != null) {
            temp.add(cSchedule.getScheduledTask().getTask().getName() +
                    cSchedule.getScheduledTask().getProcessorID());
            cSchedule = cSchedule.parent;
        }
        Collections.reverse(temp);
        for (String s : temp) {
            result += s + " ";
        }
        return  "(" + result + ")";
    }

    public String getName(){
        return "";
    }


    /*
     *  ============================================================
     * Getter and Setter Method
     * Currently store all method, optimizing version will store less
     *
     * ============================================================
     */

    public Hashtable<INode, Integer> getParentsLeftOfSchedulableTask() {
        return _parentsLeftOfSchedulableTask;
    }

    public void setParentsLeftOfSchedulableTask(Hashtable<INode, Integer> parentsLeftOfSchedulableTask) {
        _parentsLeftOfSchedulableTask = parentsLeftOfSchedulableTask;
    }

    public int getOverallFinishTime() {
        return _overallFinishTime;
    }

    public int getEarliestFinishProcessorID() {
        return _earliestFinishProcessorID;
    }

    public int getEarliestFinishTimeOfAllProcessorsProcessors() {
        return _earliestFinishTimeOfAllProcessors;
    }

    public int getRemainingComputeTime() {
        return _remainingComputeTime;
    }

    public void setOverallFinishTime(int overallFinishTime) {
        _overallFinishTime = overallFinishTime;
    }

    public void setEarliestFinishProcessorID(int earliestFinishProcessorID) {
        _earliestFinishProcessorID = earliestFinishProcessorID;
    }

    public void setEarliestFinishTimeOfAllProcessor(int earliestFinishTimeOfAllProcessors) {
        _earliestFinishTimeOfAllProcessors = earliestFinishTimeOfAllProcessors;
    }

    public void setRemainingComputeTime(int remainingComputeTime) {
        _remainingComputeTime = remainingComputeTime;
    }

    public int getHScore() {
        return _hScore;
    }

    public Hashtable<ScheduledTask, Integer> getForgottenTable() {
        return _forgotten;
    }

    public void setForgottenTableToNull(){
        _minForgottenFScore = Integer.MAX_VALUE;
        _forgotten.clear();
    }

    public void setHScore(int hScore) {
        _fScore = _scheduledTask.getFinishTime() + hScore;
        _hScore = hScore;
    }

    public ScheduledTask getScheduledTask() {
        return _scheduledTask;
    }

    public int getFScore() {
        return _fScore;
    }

    public void setFScore(int fScore) {
        _fScore = fScore;
    }

    public int getSize(){
        return _size;
    }
    public void setSize(int size){
        _size = size;
    }


    // New stuff
    public int getHash() {
        final int prime = 17;
        int value = 0;
        for (String i : _scheduling.keySet()) {
            value = prime * value + (_scheduling.get(i).get(1));
            value = prime * value + (i.hashCode());
        }
        value = prime * value + (_size);
        return value;
    }

    /**
     * lastForEachProcessor the last task schedule, for each processor.
     *
     * @return _lastForEachProcessor the last task schedule, for each processor.
     */
    public Hashtable<Integer, String> getLastForEachProcessor() {
        return _lastForEachProcessor;
    }

}


