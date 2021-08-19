package raspberry.scheduler.algorithm;

import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import raspberry.scheduler.app.visualisation.model.AlgoObservable;
import raspberry.scheduler.graph.*;

import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;

/**
 * Implementation of A star with parallelization
 *
 * @author Alan, Young
 */
public class AstarParallel extends Astar {
    // the total number of threads to use
    private static int _numCores;

    // thread pool that will deal with all the threads
    private static ThreadPoolExecutor _threadPool = null;

    // concurrentlist of subschedule for threadpool to run
    private ConcurrentLinkedQueue<Hashtable<Schedule,Hashtable<INode, Integer>>> _subSchedules;



    /**
     * Constructor for A*
     *
     * @param graphToSolve  : graph to solve (graph represents the task and dependencies)
     * @param numProcessors : number of processor we can used to scheudle tasks.
     */
    public AstarParallel(IGraph graphToSolve, int numProcessors, int numCores) {
        super(graphToSolve, numProcessors);
        initialiseThreadPool(numCores);
        _subSchedules = new ConcurrentLinkedQueue<Hashtable<Schedule,Hashtable<INode, Integer>>>();
    }

    /**
     * Initialises the ThreadPool. Called in VariableScheduler
     *
     * @param numCores
     */
    public void initialiseThreadPool(int numCores) {
        _numCores = numCores;
        // Allow numParallelCores - 1 extra threads to be made
        _threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(numCores - 1);
    }


    /**
     * Compute the optimal scheduling
     *
     * @return OutputSchedule : the optimal path/scheduling.
     */
    @Override
    public OutputSchedule findPath() {
        /*
         * find the path
         * "master" stores, schedule and its counterTable.
         * "rootTable" is the table all counterTable is based of off.
         * --> stores a node and number of incoming edges.
         */
        getH();

        Hashtable<Schedule, Hashtable<INode, Integer>> master = new Hashtable<Schedule, Hashtable<INode, Integer>>();
        Hashtable<INode, Integer> rootTable = this.getRootTable();

        for (INode i : rootTable.keySet()) {
            if (rootTable.get(i) == 0) {
                Schedule newSchedule = new Schedule(0, null, i, 1);
                newSchedule.addHeuristic(
                        Collections.max(Arrays.asList(
                                h(newSchedule),
                                h1(getChildTable(rootTable, i), newSchedule)
                        )));
                master.put(newSchedule, getChildTable(rootTable, i));
                _pq.add(newSchedule);
            }
        }

        Schedule cSchedule;
        int duplicate = 0; // Duplicate counter, Used for debugging purposes.

        _observable.setIterations(0);
        _observable.setIsFinish(false);
        //  System.out.println(_observable.getIterations());
        while (true) {
            _observable.increment();
            //System.out.println(_observable.getIterations());
            cSchedule = _pq.poll();
            Solution cScheduleSolution = new Solution(cSchedule, _numP);
            _observable.setSolution(cScheduleSolution);
            ArrayList<Schedule> listVisitedForSize = _visited.get(cSchedule.getHash());
            if (listVisitedForSize != null && isIrrelevantDuplicate(listVisitedForSize, cSchedule)) {
                duplicate++;
                continue;
            } else {
                if (listVisitedForSize == null) {
                    listVisitedForSize = new ArrayList<Schedule>();
                    _visited.put(cSchedule.getHash(), listVisitedForSize);
                }
                listVisitedForSize.add(cSchedule);
            }

            // Return if all task is scheduled
            if (cSchedule.getSize() == _numNode) {
                break;
            }
            Hashtable<INode, Integer> cTable = master.get(cSchedule);
            master.remove(cSchedule);

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
                        createSubSchedules(cSchedule, j, node, cTable);
                    }
                }
            }
            System.out.println(_subSchedules.size());
            for (Hashtable<Schedule,Hashtable<INode, Integer>> subScheduleTables: _subSchedules) {
                for (Schedule subSchedule : subScheduleTables.keySet()) {
                    master.put(subSchedule, subScheduleTables.get(subSchedule));
                    _pq.add(subSchedule);
                }
            }
        }
        _observable.setIsFinish(true);
        _observable.setSolution(new Solution(cSchedule, _numP));
        return new Solution(cSchedule, _numP);
    }

    public void createSubSchedules(Schedule cSchedule, int j, INode node,  Hashtable<INode, Integer> cTable) {
        _threadPool.submit(() -> {
            int start = calculateCost(cSchedule, j, node);
            Hashtable<INode, Integer> newTable = getChildTable(cTable, node);
            Schedule newSchedule = new Schedule(start, cSchedule, node, j);
            newSchedule.addHeuristic(
                    Collections.max(Arrays.asList(
                            h(newSchedule),
                            h1(newTable, newSchedule)
                    )));
            Hashtable<Schedule,Hashtable<INode, Integer>> subSchedule = new Hashtable<Schedule,Hashtable<INode, Integer>>();
            subSchedule.put(newSchedule, newTable);
            _subSchedules.add(subSchedule);
        });
    }



}
