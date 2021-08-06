package raspberry.scheduler.algorithm.sma;


import raspberry.scheduler.graph.INode;

/**
 * Group information of scheduledTask for readability
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


    public int getProcessorID() {
        return _processorID;
    }

    public INode getTask() {
        return _task;
    }

    public int getStartTime() {
        return _startTime;
    }

    public int getFinishTime() {
        return _startTime + _task.getValue();
    }


    @Override
    public String toString(){
        return _task + "" + _processorID;
    }
}
