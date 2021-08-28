package raspberry.scheduler.algorithm.astar;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import raspberry.scheduler.algorithm.common.Schedule;
import raspberry.scheduler.algorithm.common.ScheduledTask;
import raspberry.scheduler.algorithm.util.TableGenerator;
import raspberry.scheduler.graph.INode;


/**
 * The Schedule class represents a partial schedule of the tasks
 * Schedule has a Linked list implementation connected to parents
 *
 * @author Takahiro
 */
public class ScheduleAStar extends Schedule implements Comparable<ScheduleAStar> {

    private int _h; // h: Heuristic weight
    private int _total; // t: Total weight


    private Hashtable<String, List<Integer>> _scheduling; // partial schedule. //TODO : Implement this idea with less memory intensive manner.
    private Hashtable<Integer, String> _lastForEachProcessor; //the last task schedule, for each processor.
    private int _maxPid; //The largest pid currently used to schedule
    public Hashtable<INode, Integer> _inDegreeTable;

    /**
     * Constructor for ScheduleAstar.
     * @param scheduledTask : scheduledtask
     * @param inDegreeTable : Indegree table ( Represents node and number of parent still not scheduled )
     */
    public ScheduleAStar(ScheduledTask scheduledTask, Hashtable<INode, Integer> inDegreeTable) {
        super(scheduledTask);
        _inDegreeTable = inDegreeTable;
        _h = 0;
        _total = super.getScheduledTask().getFinishTime() + _h;
        _scheduling = new Hashtable<String, List<Integer>>();
        _lastForEachProcessor = new Hashtable<Integer, String>();
        _maxPid = scheduledTask.getProcessorID();
        _scheduling.put(scheduledTask.getTask().getName(), Arrays.asList(scheduledTask.getProcessorID(),
                scheduledTask.getStartTime()));
        _lastForEachProcessor.put(scheduledTask.getProcessorID(), scheduledTask.getTask().getName());
    }

    /**
     * Constructor for partial schedule
     *
     * @param startTime      : the earliest start time a node can be scheduled.
     * @param parentSchedule : parent schedule
     * @param node           : the task this partial schedule is scheduling.
     * @param processorId    : id of a processor a node is being scheduled
     * @deprecated
     */
    public ScheduleAStar(int startTime, ScheduleAStar parentSchedule, INode node, int processorId, Hashtable<INode, Integer> inDegreeTable) {
        super(parentSchedule, new ScheduledTask(processorId, node, startTime));
        _h = 0;
        _total = super.getScheduledTask().getFinishTime() + _h;
        _inDegreeTable = inDegreeTable;

        if (processorId > super.getParent().getMaxPid()) {
            _maxPid = processorId;
        } else {
            _maxPid = super.getParent().getMaxPid();
        }

        _scheduling = (Hashtable<String, List<Integer>>) ((ScheduleAStar) super.getParent()).getScheduling().clone();
        _lastForEachProcessor = (Hashtable<Integer, String>) ((ScheduleAStar) super.getParent()).getLastForEachProcessor().clone();

        _scheduling.put(node.getName(), Arrays.asList(processorId, startTime));
        _lastForEachProcessor.put(processorId, node.getName());
    }

    /**
     * Constructor for partial schedule
     * @param parentSchedule
     * @param scheduledTask
     * @param inDegreeTable
     */
    public ScheduleAStar(ScheduleAStar parentSchedule, ScheduledTask scheduledTask, Hashtable<INode, Integer> inDegreeTable) {
        super(parentSchedule, scheduledTask);
        _h = 0;
        _total = super.getScheduledTask().getFinishTime() + _h;
        _inDegreeTable = inDegreeTable;

        if (scheduledTask.getProcessorID() > super.getParent().getMaxPid()) {
            _maxPid = scheduledTask.getProcessorID();
        } else {
            _maxPid = super.getParent().getMaxPid();
        }
        _scheduling = (Hashtable<String, List<Integer>>) ((ScheduleAStar) super.getParent()).getScheduling().clone();
        _lastForEachProcessor = (Hashtable<Integer, String>) ((ScheduleAStar) super.getParent()).getLastForEachProcessor().clone();

        _scheduling.put(scheduledTask.getTask().getName(), Arrays.asList(scheduledTask.getProcessorID(), scheduledTask.getStartTime()));
        _lastForEachProcessor.put(scheduledTask.getProcessorID(), scheduledTask.getTask().getName());
    }



    /**
     * Since we calculate heuristic after the creation of Schedule instance, this was added.
     * Adds heuristic value
     *
     * @param h : heuristic cost;
     */
    public void addHeuristic(int h) {
        _h = h;
        _total = super.getScheduledTask().getFinishTime() + _h;
    }

    /**
     * Weighted Heuristic for Weighted A*
     * @param h
     */
    public void addWeightedHeuristic(int h) {
        _h = h * h;
        _total = super.getScheduledTask().getFinishTime() + _h;
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
    public int compareTo(ScheduleAStar schedule) {
        return _total > schedule.getTotal() ? 1 : _total < schedule.getTotal() ? -1 : 0;
    }

    /**
     * Check if two Schedule instance is the same. (this is for detecting duplicate scheduling)
     *
     * @param otherSchedule : the other schedule instance we are comparing to.
     * @return Boolean : True : if its the same.
     * False: if its different.
     */
    public boolean equals2(Object otherSchedule) {
        if (otherSchedule == this) {
            return true;
        } else if (!(otherSchedule instanceof ScheduleAStar)) {
            return false;
        } else {
            ScheduleAStar schedule = (ScheduleAStar) otherSchedule;
            if (this.getSize() != schedule.getSize()) {
                return false;
            } else if (this.getMaxPid() != schedule.getMaxPid()) {
                return false;
            } else {
                // Group by pid. Compare match
                Hashtable<String, List<Integer>> scheduling2 = schedule.getScheduling();

                Hashtable<Integer, Hashtable<String, Integer>> hash4scheduling = new Hashtable<Integer, Hashtable<String, Integer>>();
                Hashtable<Integer, Hashtable<String, Integer>> hash4scheduling2 = new Hashtable<Integer, Hashtable<String, Integer>>();

                for (String s : _scheduling.keySet()) {
                    Hashtable<String, Integer> tmp = hash4scheduling.get(_scheduling.get(s).get(0)); //get(0) gets pid
                    if (tmp == null) {
                        tmp = new Hashtable<String, Integer>();
                    }
                    tmp.put(s, _scheduling.get(s).get(1));
                    hash4scheduling.put(_scheduling.get(s).get(0), tmp);
                }
                for (String s : scheduling2.keySet()) {
                    Hashtable<String, Integer> tmp = hash4scheduling2.get(scheduling2.get(s).get(0)); //get(0) gets pid
                    if (tmp == null) {
                        tmp = new Hashtable<String, Integer>();
                    }
                    tmp.put(s, scheduling2.get(s).get(1));
                    hash4scheduling2.put(scheduling2.get(s).get(0), tmp);
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
    /**
     * Check if two Schedule instance is the same. (this is for detecting duplicate scheduling)
     * @param otherSchedule : Schedule we are comparing against.
     * @return true if its the same. false if it is not the same schedule.
     */
    public boolean equals3(Object otherSchedule) {
        if (otherSchedule == this) {
            return true;
        } else if (!(otherSchedule instanceof ScheduleAStar)) {
            return false;
        } else {
            ScheduleAStar schedule = (ScheduleAStar) otherSchedule;
            if (this.getSize() != schedule.getSize()) {
                return false;
            } else if (this.getMaxPid() != schedule.getMaxPid()) {
                return false;
            } else {
                // Group by pid. Compare match
                Hashtable<String, List<Integer>> _scheduling2 = schedule.getScheduling();

                Set<HashSet<Integer>> hash4scheduling = new HashSet<HashSet<Integer>>();
                Set<HashSet<Integer>> hash4scheduling2 = new HashSet<HashSet<Integer>>();

                for (String s : _scheduling.keySet()) {
                    hash4scheduling.add(new HashSet<Integer>(Arrays.asList(s.hashCode(), _scheduling.get(s).get(1))));
                }
                for (String s : _scheduling2.keySet()) {
                    hash4scheduling2.add(new HashSet<Integer>(Arrays.asList(s.hashCode(), _scheduling2.get(s).get(1))));
                }
                return hash4scheduling.equals(hash4scheduling2);
            }
        }
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
        for (String i : _scheduling.keySet()) {
            value = prime * value + (_scheduling.get(i).get(1));
            value = prime * value + (i.hashCode());
        }
        value = prime * value + (super.getSize());
        return value;
    }

    /*
    Getter and Setters
     */
    /**
     * get Heuristic weight
     *
     * @return _h
     */
    public int getH() {
        return _h;
    }

    /**
     * get Total weight
     *
     * @return _total
     */
    public int getTotal() {
        return _total;
    }


    /**
     * get Start Time the time this node start running.
     *
     * @return _startTime the time this node start running.
     */
    public int getStartTime() {
        return super.getScheduledTask().getStartTime();
    }


    /**
     * get finish time the time at this node finish running
     *
     * @return _finishTime the time at this node finish running
     */
    public int getFinishTime() {
        return super.getScheduledTask().getFinishTime();
    }


    /**
     * get node being scheduled
     *
     * @return _node the node being  scheduled
     */
    public INode getNode() {
        return super.getScheduledTask().getTask();
    }


    /**
     * get pid the Processor Id
     *
     * @return _pid the Processor Id
     */
    public int getPid() {
        return super.getScheduledTask().getProcessorID();
    }


    /**
     * get parent schedule the Parent Schedule
     *
     * @return _parent the Parent Schedule
     */
    public ScheduleAStar getParent() {
        return (ScheduleAStar) super.getParent();
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
     * lastForEachProcessor the last task schedule, for each processor.
     *
     * @return _lastForEachProcessor the last task schedule, for each processor.
     */
    public Hashtable<Integer, String> getLastForEachProcessor() {
        return _lastForEachProcessor;
    }

    /**
     * get max pid The largest pid currently used to schedule
     *
     * @return _maxPid The largest pid currently used to schedule
     */
    public int getMaxPid() {
        return _maxPid;
    }

    /**
     * get size of the scheduled.
     * @return integer : representing the number of task that has already been scheduled
     */
    public int getSize() {
        return super.getSize();
    }


    public int getTaskStartTime(String taskName) {
        return _scheduling.get(taskName).get(1);
    }


    /**
     * Display the name and the path of the current mbSchedule
     * @return string
     */
    @Override
    public String toString() {
        ScheduleAStar cSchedule = this;
        Hashtable<Integer, ArrayList<ScheduledTask>> table = new Hashtable<>();
        Hashtable<Integer, ArrayList<String>> stringTable = new Hashtable<>();
        String result = "";
        while (cSchedule != null) {
            if (!table.containsKey(cSchedule.getScheduledTask().getProcessorID())) {
                ArrayList<ScheduledTask> s = new ArrayList<>();
                s.add(cSchedule.getScheduledTask());
                table.put(cSchedule.getScheduledTask().getProcessorID(), s);
            } else {
                table.get(cSchedule.getScheduledTask().getProcessorID()).add(cSchedule.getScheduledTask());
            }
            cSchedule = cSchedule.getParent();
        }
        AtomicInteger len = new AtomicInteger();
        table.forEach((k, v) -> len.set(Integer.max(len.intValue(), v.size())));
        for (int pid : table.keySet()) {
            ArrayList<ScheduledTask> stList = table.get(pid);
            stList.sort(Comparator.comparingInt(ScheduledTask::getStartTime));
            ArrayList<String> stringList = new ArrayList<String>();
            for (ScheduledTask s : stList) {
                int pad = 3 - String.valueOf(s.getStartTime()).length();
                String padding = new String(new char[pad]).replace("\0", " ");
                stringList.add("{Task:" + s.getTask().getName() + "-pid:"
                        + s.getProcessorID() + "-t:" + s.getStartTime() + "}" + padding);
            }
            int i = stList.size();
            while (i < len.intValue()) {
                stringList.add("  ");
                i++;
            }
            stringTable.put(pid, stringList);
        }

        List<String> headersList = new ArrayList<>();
        ArrayList<Integer> pids = Collections.list(table.keys());
        Collections.sort(pids);
        for (int pid : pids) {
            headersList.add(String.valueOf(pid));
        }
        List<List<String>> rowsList = new ArrayList<>();
        for (int j = 0; j < len.intValue(); j++) {
            List<String> row = new ArrayList<>();
            for (int pid : pids) {
                row.add(stringTable.get(pid).get(j));
            }
            rowsList.add(row);
        }
        TableGenerator tableGenerator = new TableGenerator();
        //System.out.format("%32s%10d%16s", string1, int1, string2);
        return tableGenerator.generateTable(headersList, rowsList);
    }
}