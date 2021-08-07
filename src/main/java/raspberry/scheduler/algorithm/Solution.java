package raspberry.scheduler.algorithm;
import raspberry.scheduler.graph.INode;

import java.util.ArrayList;
import java.util.Hashtable;

public class Solution implements OutputSchedule{

//    private Schedule _solution;
    private Hashtable<INode, int[]> _table;
    private int _finshTime;
    private int _totalProcessorNum;

    public Solution(Schedule schedule, int numP){
        _table = schedule.getPath();
        _finshTime = schedule._finishTime;
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
}
