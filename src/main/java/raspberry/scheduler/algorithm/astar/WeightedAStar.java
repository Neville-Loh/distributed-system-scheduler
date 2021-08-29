package raspberry.scheduler.algorithm.astar;

import raspberry.scheduler.algorithm.common.OutputSchedule;
import raspberry.scheduler.algorithm.common.ScheduledTask;
import raspberry.scheduler.algorithm.common.Solution;
import raspberry.scheduler.graph.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.PriorityQueue;

/**
 * Implementation of A star algorithm.
 *
 * @author Takahiro
 */
public class WeightedAStar extends AStar {

    /**
     * Constructor for A*
     *
     * @param graphToSolve  : graph to solve (graph represents the task and dependencies)
     * @param numProcessors : number of processor we can used to scheudle tasks.
     */
    public WeightedAStar(IGraph graphToSolve, int numProcessors) {
        super(graphToSolve, numProcessors, Integer.MAX_VALUE);
    }

    /**
     * Compute the optimal scheduling
     *
     * @return OutputSchedule : the optimal path/scheduling.
     */
    @Override
    public OutputSchedule findPath() {
        getH();
        Hashtable<INode, Integer> rootTable = this.getRootTable();
        for (INode node : rootTable.keySet()) {
            if (rootTable.get(node) == 0) {

                ScheduleAStar newSchedule = new ScheduleAStar(
                        new ScheduledTask(1,node, 0),
                        getChildTable(rootTable, node)
                        );

                newSchedule.addWeightedHeuristic(
                        Collections.max(Arrays.asList(
                                h(newSchedule),
                                h1(getChildTable(rootTable, node), newSchedule)
                        )));
                _pq.add(newSchedule);
            }
        }

        ScheduleAStar cSchedule;

        while (true) {
//            System.out.printf("PQ SIZE: %d\n", _pq.size());
            cSchedule = _pq.poll();

            // Return if all task is scheduled
            if (cSchedule.getSize() == _numNode) {
                break;
            }

            Hashtable<INode, Integer> cTable = cSchedule._inDegreeTable;
            // Find the next empty processor. (
            int currentMaxPid = cSchedule.getMaxPid();
            int pidBound;
            if (currentMaxPid + 1 > _numP) {
                pidBound = _numP;
            } else {
                pidBound = currentMaxPid + 1;
            }
            for (INode node : cTable.keySet()) {
                if (cTable.get(node) == 0) {
                    for (int j = 1; j <= pidBound; j++) {
                        int start = calculateEarliestStartTime(cSchedule, j, node);
                        Hashtable<INode, Integer> newTable = getChildTable(cTable, node);
                        ScheduleAStar newSchedule = new ScheduleAStar(start, cSchedule, node, j, newTable);
                        newSchedule.addWeightedHeuristic(
                                Collections.max(Arrays.asList(
                                        h(newSchedule),
                                        h1(newTable, newSchedule)
                                )));
                        _pq.add(newSchedule);
                    }
                }
            }

            // Trim PQ to fixed size.
            if (_pq.size() > 100000){
                PriorityQueue<ScheduleAStar> newPQ = new PriorityQueue<ScheduleAStar>();
                for (int i = 0 ; i< 100; i++){
                    newPQ.add( _pq.poll() );
                }
                _pq = newPQ;
            }
        }
        return new Solution(cSchedule, _numP);
    }
}
