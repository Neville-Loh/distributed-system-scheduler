package raspberry.scheduler.algorithm.common;


import raspberry.scheduler.algorithm.sma.MBSchedule;
import raspberry.scheduler.graph.INode;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Linked list implementation of schedule
 */
public class Schedule {

    // Attribute of linked list
    private Schedule _parent; // Parent Schedule
    private int _size; // Size of the partial schedule. # of tasks scheduled.

    // item that stored in linked list
    private ScheduledTask _scheduledTask;


    private int _maxPid;


    public Schedule(ScheduledTask scheduledTask){
        _size = 1;
        _scheduledTask = scheduledTask;
    }


    public Schedule(Schedule parentSchedule, ScheduledTask scheduledTask){
        _parent = parentSchedule;
        _scheduledTask = scheduledTask;
        _size = parentSchedule.getSize() + 1;
    }


    public Schedule createSubschedule(ScheduledTask scheduledTask){
        return null;
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
        tmp.put(_scheduledTask.getTask(), new int[]{
                _scheduledTask.getStartTime(),
                _scheduledTask.getFinishTime(),
                _scheduledTask.getProcessorID()});
        return tmp;
    }


    /* ------------------------------
     *  Getter and Setter
     *
     * ------------------------------
     */
    public Schedule getParent() {
        return _parent;
    }

    public void setParent(Schedule _parent) {
        this._parent = _parent;
    }

    public int getSize() {
        return _size;
    }

    public void setSize(int _size) {
        this._size = _size;
    }

    public void setScheduledTask(ScheduledTask _scheduledTask) {
        this._scheduledTask = _scheduledTask;
    }

    public int getMaxPid() {
        return _maxPid;
    }

    public void setMaxPid(int _maxPid) {
        this._maxPid = _maxPid;
    }

    public int getPid(){return _scheduledTask.getProcessorID();}
    public int getFinishTime(){return _scheduledTask.getFinishTime();}

    /**
     * get node being scheduled
     *
     * @return _node the node being  scheduled
     */
    public INode getNode() {
        return _scheduledTask.getTask();
    }

    /* ============================================================
     *  Duplicate schedule detection
     * 
     * ============================================================
     */
    /**
     * Return the last scheduled task in schedule
     * @return last scheduled task
     */
    public ScheduledTask getScheduledTask() {
        return _scheduledTask;
    }

    /**
     *  Return the scheduled task if the task is scheduled
     *  else return null
     * @param task the task that is scheduled
     * @return the scheduled task
     */
    public ScheduledTask getScheduledTask(INode task ){
        Schedule cSchedule = this;
        while (cSchedule != null){
            if (cSchedule.getScheduledTask().getTask() == task){
                return cSchedule.getScheduledTask();
            }
            cSchedule = cSchedule.getParent();
        }
        return null;
    }


    /**
     * todo: new method, please test @see dup-detection
     * @param processorID
     * @return
     */
    public ArrayList<ScheduledTask> getAllTaskInProcessor(int processorID) {
        Schedule cSchedule = this;
        ArrayList<ScheduledTask> result = new ArrayList<>();
        while (cSchedule != null){
            if (cSchedule.getScheduledTask().getProcessorID() == processorID){
                result.add(cSchedule.getScheduledTask());
            }
            cSchedule = cSchedule.getParent();
        }

        return result;
    }


}
