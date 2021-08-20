package raspberry.scheduler.algorithm.common;


import raspberry.scheduler.graph.INode;

/**
 * Group information of scheduledTask for readability
 */
public class ScheduledTask {

    private int _processorID;
    private INode _task;
    private int _startTime;
    private int _originalStartTime;


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
        _originalStartTime = startTime;
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

    public void setStartTime(int startTime) {
//        if (_originalStartTime != -1){
//            _originalStartTime = _startTime;
//        }
        _startTime = startTime;
    }

    public int getFinishTime() {
        return _startTime + _task.getValue();
    }


    public int getOriginalStartTime() {
        return _originalStartTime;
    }

//    public void setOriginalStartTime(int originStartTime) {
//        _originalStartTime = originStartTime;
//    }

    @Override
    public String toString(){
        return _task + "" + _processorID;
    }

//    /**
//     * //TODO check, might be the bug
//     * @param obj
//     * @return
//     */
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//        if (obj.getClass() != this.getClass()) {
//            return false;
//        }
//        final ScheduledTask other = (ScheduledTask) obj;
//        return (this.getTask().getName()+ this.getProcessorID()).equals(
//                other.getTask().getName() + other.getProcessorID()
//        );
//    }

    @Override
    public int hashCode() {
        return (_task.getName()+_processorID).hashCode();
    }


}
