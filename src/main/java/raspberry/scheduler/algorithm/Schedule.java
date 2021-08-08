package raspberry.scheduler.algorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Hashtable;

import raspberry.scheduler.graph.INode;

public class Schedule implements Comparable<Schedule>{

    private int _h; // h: Heuristic weight
    private int _total; // t: Total weight
    private int _startTime; //the time this node start running.
    private int _finishTime; //the time at this node finish running
    private INode _node;
    private int _pid;  //Processor Id
    private Schedule _parent; // Parent Schedule
    private int _size; // Size of the partial schedule. # of tasks scheduled.
    private Hashtable<String, List<Integer>> _scheduling; // partial schedule. //TODO : Implement this idea with less memory intensive manner.
    private Hashtable<Integer, String> _lastForEachProcessor; //the last task schedule, for each processor.
    private int _maxPid; //The largest pid currently used to schedule


    private int _upperBound;    // For BNB. Represents the worst case. <- Bad schedling.
    private int _lowerBound;   // For BNB. Represents the base case. <- perfect schedling.


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
        _total = _finishTime + _h;
        _parent = parentSchedule;

        if (parentSchedule == null){
            _size = 1;
            _scheduling = new Hashtable<String, List<Integer>>();
            _lastForEachProcessor = new Hashtable<Integer, String>();
            _maxPid = processorId;
        }else{
            if (processorId > parentSchedule.getMaxPid()){
                _maxPid = processorId;
            }else{
                _maxPid = parentSchedule.getMaxPid();
            }
            _size = parentSchedule._size + 1;
            _scheduling = (Hashtable<String, List<Integer>>) parentSchedule.getScheduling().clone();
            _lastForEachProcessor = (Hashtable<Integer, String>) parentSchedule.getLastForEachProcessor().clone();
        }
        _scheduling.put(node.getName(), Arrays.asList(processorId,startTime));
        _lastForEachProcessor.put(processorId, node.getName());
    }

    /**
     * Since we calculate heuristic after the creation of Schedule instance, this was added.
     * Adds heuristic value
     * @param h : heuristic cost;
     */
    public void addHeuristic(int h){
        _h = h;
        _total = _finishTime + h;
    }

    /**
     * Compare two Schedule instance. Uses to put Schedule in priority Queue
     * @param schedule : A schedule to compare to
     * @return : 1 : if s's total weight is smaller,
     *          -1 : if s's total weight is bigger,
     *           0 : Two scheudle has same total weight.
     */
    @Override
    public int compareTo(Schedule schedule){
        return _total > schedule.getTotal() ? 1 : _total < schedule.getTotal() ? -1 : 0;
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
            }else if(schedule.getMaxPid() != schedule.getMaxPid()){
                return false;
            }
            return _scheduling.equals(schedule.getScheduling());
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
        for (String i: _scheduling.keySet()){
            value = prime * value + ( _scheduling.get(i).hashCode() );
            value = prime * value + ( i.hashCode() );
            value = prime * value + ( _size );
        }
        return value;
    }

    /*
    Getter and Setters
     */

    public int getH() {
        return _h;
    }

    public void setH(int h) {
        _h = h;
    }

    public int getTotal() {
        return _total;
    }

    public void setTotal(int total) {
        _total = total;
    }

    public int getStartTime() {
        return _startTime;
    }

    public void setStartTime(int startTime) {
        _startTime = startTime;
    }

    public int getFinishTime() {
        return _finishTime;
    }

    public void setFinishTime(int finishTime) {
        _finishTime = finishTime;
    }

    public INode getNode() {
        return _node;
    }

    public void setNode(INode node) {
        _node = node;
    }

    public int getPid() {
        return _pid;
    }

    public void setPid(int pid) {
        _pid = pid;
    }

    public Schedule getParent() {
        return _parent;
    }

    public void setParent(Schedule parent) {
        _parent = parent;
    }

    public int getSize() {
        return _size;
    }

    public void setSize(int size) {
        _size = size;
    }

    public Hashtable<String, List<Integer>> getScheduling() {
        return _scheduling;
    }

    public void setScheduling(Hashtable<String, List<Integer>> scheduling) {
        _scheduling = scheduling;
    }

    public Hashtable<Integer, String> getLastForEachProcessor() {
        return _lastForEachProcessor;
    }

    public void setLastForEachProcessor(Hashtable<Integer, String> lastForEachProcessor) {
        _lastForEachProcessor = lastForEachProcessor;
    }

    public int getMaxPid() {
        return _maxPid;
    }

    public void setMaxPid(int maxPid) {
        _maxPid = maxPid;
    }

    public int getUpperBound() {
        return _upperBound;
    }

    public void setUpperBound(int upperBound) {
        _upperBound = upperBound;
    }

    public int getLowerBound() {
        return _lowerBound;
    }

    public void setLowerBound(int lowerBound) {
        _lowerBound = lowerBound;
    }
}