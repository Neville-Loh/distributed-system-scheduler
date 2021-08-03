package raspberry.scheduler.algorithm.sma;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

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
    public int size;
    public int fScore;


    private int _hScore;

    public int p_id;
    public INode node;
    public int startTime; //the time this node start running.
    public int finishTime; //the time at this node finish running

    private int _overallFinishTime;
    private int _earliestFinishProcessorID;
    private int _earliestFinishTimeOfAllProcessors;
    private int _remainingComputeTime;

    /**
     *
     * @param taskStartTime
     * @param parentSchedule
     * @param taskToSchedule
     * @param processorId
     * @param remainingComputeTime remaining compute time after input task has been scheduled
     */
    public MBSchedule(MBSchedule parentSchedule, int remainingComputeTime, int processorId, INode taskToSchedule, int taskStartTime)  {
        // scheduled task value
        node = taskToSchedule;
        p_id = processorId;
        startTime = taskStartTime;
        finishTime = taskStartTime + taskToSchedule.getValue();

        // linked-list attribute
        parent = parentSchedule;
        _remainingComputeTime = remainingComputeTime;
        if (parentSchedule == null){
            size = 1;
            _overallFinishTime = finishTime;
        }else{
            size = parentSchedule.size + 1;
            _overallFinishTime = Math.max(parent.getOverallFinishTime(), finishTime);
        }

        // a* attributes
        //fScore = _overallFinishTime + heuristic;

    }


    /**
     *
     * @param processorId
     * @param taskToSchedule
     * @param taskStartTime
     * @return
     */
    public MBSchedule createSubSchedule(int processorId, INode taskToSchedule, int taskStartTime){
        int remainingComputeTime = _remainingComputeTime - taskToSchedule.getValue();
        return new MBSchedule(this, remainingComputeTime, processorId, taskToSchedule,taskStartTime);
    }





    /**
     * After computing the scheduling, call this method to get List of paths
     * @return path
     */
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
     */

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

    public void set_earliestFinishTimeOfAllProcessor(int earliestFinishTimeOfAllProcessors) {
        _earliestFinishTimeOfAllProcessors = earliestFinishTimeOfAllProcessors;
    }

    public void set_remainingComputeTime(int remainingComputeTime) {
        _remainingComputeTime = remainingComputeTime;
    }

    public int getHScore() {
        return _hScore;
    }

    public void setHScore(int hScore) {
        fScore = _overallFinishTime + hScore;
        _hScore = hScore;
    }

    @Override
    public String toString(){

     return  "f: " + fScore +
             " processorId: " + p_id
             + " Task = " + node
             + " startTime: " + startTime
             + " finishTime: " + finishTime;
    }
}


