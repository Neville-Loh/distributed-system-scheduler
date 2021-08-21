package raspberry.scheduler.algorithm.common;

import raspberry.scheduler.algorithm.astar.ScheduleAStar;
import raspberry.scheduler.algorithm.bNb.ScheduleB;
import raspberry.scheduler.algorithm.sma.MBSchedule;
import raspberry.scheduler.graph.INode;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Solution class that represent a scheduling algorithm output
 * @author Takahiro
 */
public class Solution implements OutputSchedule {
    private Hashtable<INode, int[]> _table;
    private int _finshTime;
    private int _totalProcessorNum;


    /**
     * Class constructor
     * @param schedule a linked list that contain a valid solution
     * @param numP the specified number of processors that algorithm takes in as input
     */
    public Solution(ScheduleAStar schedule, int numP) {
        _table = schedule.getPath();
        for (INode node : _table.keySet()) {
            _finshTime = Math.max(getStartTime(node) + node.getValue(), _finshTime);
        }
        _totalProcessorNum = numP;
    }

    public Solution(ScheduleB schedule, int numP) {
        _table = schedule.getPath();
        _finshTime = schedule.getOverallFinishTime();
        _totalProcessorNum = numP;
    }

    /**
     * Class constructor
     * duplicated method, create interface and refactor in next release
     * @param schedule a linked list that contain a valid solution
     * @param numP the specified number of processors that algorithm takes in as input
     */
    public Solution(MBSchedule schedule, int numP) {
        _table = schedule.getPath();
        for (INode node : _table.keySet()) {
            _finshTime = Math.max(getStartTime(node) + node.getValue(), _finshTime);
        }
        _totalProcessorNum = numP;
    }


    @Override
    public int getTotalProcessorNum() {
        return _totalProcessorNum;
    }

    @Override
    public int getProcessorNum(INode node) {
        return _table.get(node)[2];
    }

    @Override
    public int getStartTime(INode node) {
        return _table.get(node)[0];
    }

    @Override
    public int getFinishTime() {
        return _finshTime;
    }

    @Override
    public int getNumTasks() {
        return _table.size();
    }

    @Override
    public List<INode> getNodes(int pid) {
        List<INode> nodesList = new ArrayList<INode>();

        for (INode node : _table.keySet()) {
            if (_table.get(node)[2] == pid) {
                nodesList.add(node);
            }
        }
        return nodesList;
    }
}
