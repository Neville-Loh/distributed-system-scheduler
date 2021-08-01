package raspberry.scheduler.algorithm;
import raspberry.scheduler.graph.INode;

public class Solution implements OutputSchedule{

    @Override
    public int getTotalProcessorNum() {
        return 0;
    }

    @Override
    public int getProcessorNum(INode node) {
        return 0;
    }

    @Override
    public int getStartTime(INode node) {
        return 0;
    }

    @Override
    public int getFinishTime() {
        return 0;
    }
}
