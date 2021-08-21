package raspberry.scheduler.algorithm.astar;

import java.util.*;
import java.util.List;

import raspberry.scheduler.algorithm.Algorithm;
import raspberry.scheduler.algorithm.common.OutputSchedule;
import raspberry.scheduler.algorithm.common.ScheduledTask;
import raspberry.scheduler.algorithm.common.Solution;
import raspberry.scheduler.graph.*;

import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;

/**
 * Implementation of A star algorithm.
 *
 * @author Takahiro
 */
public class WeightedAstar extends Astar implements Algorithm {

    /**
     * Constructor for A*
     *
     * @param graphToSolve  : graph to solve (graph represents the task and dependencies)
     * @param numProcessors : number of processor we can used to scheudle tasks.
     */
    public WeightedAstar(IGraph graphToSolve, int numProcessors) {
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

                //ScheduleAStar newSchedule = new ScheduleAStar(0, null, i, 1, getChildTable(rootTable, i));
                newSchedule.addWeightedHeuristic(
                        Collections.max(Arrays.asList(
                                h(newSchedule),
                                h1(getChildTable(rootTable, node), newSchedule)
                        )));
//                master.put(newSchedule, getChildTable(rootTable, i));
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
//            Hashtable<INode, Integer> cTable = master.get(cSchedule);
//            master.remove(cSchedule);
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
                    //TODO : Make it so that if there is multiple empty processor, use the lowest value p_id.
                    for (int j = 1; j <= pidBound; j++) {
                        int start = calculateEarliestStartTime(cSchedule, j, node);
                        Hashtable<INode, Integer> newTable = getChildTable(cTable, node);
                        ScheduleAStar newSchedule = new ScheduleAStar(start, cSchedule, node, j, newTable);
                        newSchedule.addWeightedHeuristic(
                                Collections.max(Arrays.asList(
                                        h(newSchedule),
                                        h1(newTable, newSchedule)
                                )));
//                        master.put(newSchedule, newTable);
                        _pq.add(newSchedule);
                    }
                }
            }

            if (_pq.size() > 100000){
                PriorityQueue<ScheduleAStar> newPQ = new PriorityQueue<ScheduleAStar>();
                for (int i = 0 ; i< 100; i++){
                    newPQ.add( _pq.poll() );
                }
                System.out.println("TRIMMED");
                _pq = newPQ;
            }
        }

        return new Solution(cSchedule, _numP);
    }
}
