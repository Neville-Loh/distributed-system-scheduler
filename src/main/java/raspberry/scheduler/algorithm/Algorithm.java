package raspberry.scheduler.algorithm;

import raspberry.scheduler.algorithm.astar.ScheduleAStar;
import raspberry.scheduler.algorithm.common.OutputSchedule;
import raspberry.scheduler.algorithm.common.Schedule;
import raspberry.scheduler.graph.INode;

/**
 * Represent the scheduling algorithm
 * @Author Takahiro
 */
public interface Algorithm {
    /**
     * Finds a valid and optimal solution given with specified parameters
     * in constructor
     * @return outputSchedule a schedule that represent the result
     */
    OutputSchedule findPath();

    int calculateEarliestStartTime(Schedule parentSchedule, int processorId, INode nodeToBeSchedule);

}
