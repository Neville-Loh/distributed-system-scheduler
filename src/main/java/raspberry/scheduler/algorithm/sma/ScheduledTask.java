package raspberry.scheduler.algorithm.sma;


import raspberry.scheduler.graph.INode;

/**
 * Group information of scheduledTask for readability
 * @Author Neville
 */
public class ScheduledTask {
    private int _processorID;
    private INode _task;
    private int _startTime;

    /**
     * Class Constructor
     * A scheduled task represent a task that is scheduled in a processor
     * it store a start time, start time of the task
     * processor id, the processor that the task is ran on,
     * and the task itself. Which is an INode with computation cost.
     * @param processorID the processor that the
     * @param task task to scheduled
     * @param startTime start time of the task
     */
    public ScheduledTask(int processorID, INode task, int startTime){
        _processorID = processorID;
        _task = task;
        _startTime = startTime;
    }

    /**
     * Get Method
     * @return processor id of task that it is scheduled in
     */
    public int getProcessorID() {
        return _processorID;
    }

    /**
     * Get Method
     * @return Task, the task that is scheduled
     */
    public INode getTask() {
        return _task;
    }

    /**
     * Get Method
     * @return StartTime the time that the task starts computing
     */
    public int getStartTime() {
        return _startTime;
    }

    /**
     * Get Method
     * Computes and return the finish time of the task
     * @return finishTime the time the task finish computing
     */
    public int getFinishTime() {
        return _startTime + _task.getValue();
    }
}
