package raspberry.scheduler.algorithm.common;

import raspberry.scheduler.graph.INode;

import java.util.List;

/**
 * Interface that represent a scheduling algorithm output
 * @author Neivlle
 */
public interface OutputSchedule {
    /**
     * Ge the total processor number of given input
     * @return total the processor total number
     */
    int getTotalProcessorNum();
    /**
     * Get the processor number of the scheduled node
     * @param node node of the task dependency graph
     * @return processorNum the processor that input node is assigned to
     */
    int getProcessorNum(INode node);

    /**
     * Get start time of the scheduled input node
     * @param node node of the task dependency graph
     * @return time time in absolute scale
     */
    int getStartTime(INode node);

    /**
     * Get the final finishing time of the output schedule
     * @return time the finishing time of the schedule
     */
    int getFinishTime();

    /**
     * Get the total number of task in the output schedule
     * @return total number of task in the schedule
     */
    int getNumTasks();

    /**
     * get list of nodes on a given pid
     * @param pid
     * @return
     */
    List<INode> getNodes(int pid);
}
