package raspberry.scheduler.algorithm.sma;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import raspberry.scheduler.algorithm.Schedule;
import raspberry.scheduler.graph.IEdge;
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
    public int size;
    public int fScore;


    private int _hScore;

    private ScheduledTask _scheduledTask;
    private Hashtable<INode, Integer> _parentsLeftOfSchedulableTask;


    private int _overallFinishTime;
    private int _earliestFinishProcessorID;
    private int _earliestFinishTimeOfAllProcessors;
    private int _remainingComputeTime;

    /**
     * Class constructor
     * @param parentSchedule
     * @param remainingComputeTime remaining compute time after input task has been scheduled
     */
    public MBSchedule(MBSchedule parentSchedule, int remainingComputeTime, ScheduledTask scheduledTask)  {
        // scheduled task value
        _scheduledTask = scheduledTask;

        // linked-list attribute
        parent = parentSchedule;
        _remainingComputeTime = remainingComputeTime;
        if (parentSchedule == null){
            size = 1;

            // Manhattan distance heuristic
            //_earliestFinishProcessorID = 1;
        }else{
            size = parentSchedule.size + 1;
            _overallFinishTime = Math.max(parent.getOverallFinishTime(), _scheduledTask.getFinishTime());


//            // Manhattan distance heuristic
//            if (_earliestFinishProcessorID == this.parent.getEarliestFinishProcessorID()){
//                _earliestFinishTimeOfAllProcessors = _scheduledTask.getFinishTime();
//            } else if (_earliestFinishProcessorID){
//
//            }

        }

        // setup new scheduled



    }


    /**
     * Create sub-schedule using the calling class as parent
     * @param scheduledTask task to be schedule
     * @return sub-schedule
     */
    public MBSchedule createSubSchedule(ScheduledTask scheduledTask){
        int remainingComputeTime = _remainingComputeTime - scheduledTask.getTask().getValue();
        return new MBSchedule(this, remainingComputeTime, scheduledTask);
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




    /*
     * Comparator and Iterator method
     *
     */


    @Override
    public int compareTo(MBSchedule s){
        return Integer.compare(this.fScore, s.fScore);
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

    /*
     * Getter and Setter Method
     * Currently store all method, optimizing version will store less
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

    public void setHScore(int hScore) {
        fScore = _overallFinishTime + hScore;
        _hScore = hScore;
    }

    public ScheduledTask getScheduledTask() {
        return _scheduledTask;
    }

    @Override
    public String toString(){
     return  "f: " + fScore +
             " processorId: " + _scheduledTask.getProcessorID()
             + " Task = " + _scheduledTask.getTask()
             + " startTime: " + _scheduledTask.getStartTime()
             + " finishTime: " + _scheduledTask.getFinishTime()
             + "   ||||| overall finish time: " +_overallFinishTime;
    }
}


