package raspberry.scheduler.algorithm.common;

import raspberry.scheduler.graph.INode;

/**
 * Group information of scheduledTask for readability
 * @author Takahiro, Neville
 */
public class ScheduledTask {

    private int _processorID;
    private INode _task;
    private int _startTime;


    /**
     * Class Constructor
     * @param processorID
     * @param task
     * @param startTime
     */
    public ScheduledTask(int processorID, INode task, int startTime){
        _processorID = processorID;
        _task = task;
        _startTime = startTime;
    }

    /* ------------------------------
     *  Getter and Setters
     *
     * ------------------------------
     */

    /**
     * Get processor id of task
     * @return processor id
     */
    public int getProcessorID() {
        return _processorID;
    }

    /**
     * Get task
     * @return task
     */
    public INode getTask() {
        return _task;
    }

    /**
     * Get start time of task
     * @return start time
     */
    public int getStartTime() {
        return _startTime;
    }

    /**
     * Get finish time of task
     * @return finish time
     */
    public int getFinishTime() {
        return _startTime + _task.getValue();
    }

    /**
     * Get name of task
     * @return name of task
     */
    public String getName() {
        return _task.getName();
    }

    @Override
    public String toString(){
        return _task + "" + _processorID;
    }

}
