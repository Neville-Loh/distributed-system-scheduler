package raspberry.scheduler.algorithm.astar;

import java.util.*;
import java.util.concurrent.*;

import raspberry.scheduler.algorithm.common.OutputSchedule;
import raspberry.scheduler.algorithm.common.ScheduledTask;
import raspberry.scheduler.algorithm.common.Solution;
import raspberry.scheduler.app.visualisation.model.AlgoStats;
import raspberry.scheduler.graph.*;

/**
 * Implementation of A star with parallelization
 *
 * @author Alan, Young
 */
public class AStarParallel extends AStar {
    // thread pool that will deal with all the threads
    private ThreadPoolExecutor _threadPool = null;
    private AlgoStats _algoStats;
    // concurren tlist of subschedule for threadpool to run
    private ConcurrentLinkedQueue<Hashtable<ScheduleAStar, Hashtable<INode, Integer>>> _subSchedules;


    /**
     * Constructor for A*
     *
     * @param graphToSolve  : graph to solve (graph represents the task and dependencies)
     * @param numProcessors : number of processor we can use to schedule tasks.
     * @param numCores : number of cores / threads
     */
    public AStarParallel(IGraph graphToSolve, int numProcessors, int numCores) {
        super(graphToSolve, numProcessors, Integer.MAX_VALUE);
        initialiseThreadPool(numCores);
        _subSchedules = new ConcurrentLinkedQueue<Hashtable<ScheduleAStar, Hashtable<INode, Integer>>>();
        _algoStats = AlgoStats.getInstance();
    }

    /**
     *  Constructor for A* with upper bound
     * @param graphToSolve : graph to solve (graph represents the task and dependencies)
     * @param numProcessors : number of processor we can use to schedule tasks.
     * @param upperbound : upper bound. (found from creating a valid solution)
     * @param numCores : number of cores / threads
     */
    public AStarParallel(IGraph graphToSolve, int numProcessors, int upperbound, int numCores) {
        super(graphToSolve, numProcessors,upperbound);
        initialiseThreadPool(numCores);
        _subSchedules = new ConcurrentLinkedQueue<Hashtable<ScheduleAStar, Hashtable<INode, Integer>>>();
        _algoStats = AlgoStats.getInstance();
    }


    /**
     * Initialises the ThreadPool. Called in VariableScheduler
     *
     * @param numCores
     */
    public void initialiseThreadPool(int numCores) {
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

        getH();
        Hashtable<ScheduleAStar, Hashtable<INode, Integer>> master = new Hashtable<ScheduleAStar, Hashtable<INode, Integer>>();
        Hashtable<INode, Integer> rootTable = this.getRootTable();

        for (INode node : rootTable.keySet()) {
            if (rootTable.get(node) == 0) {
                ScheduleAStar newSchedule = new ScheduleAStar(
                        new ScheduledTask(1,node, 0),
                        getChildTable(rootTable, node)
                );
                newSchedule.addHeuristic(
                        Collections.max(Arrays.asList(
                                h(newSchedule),
                                h1(getChildTable(rootTable, node), newSchedule)
                        )));
                master.put(newSchedule, getChildTable(rootTable, node));
                _pq.add(newSchedule);
            }
        }

        ScheduleAStar cSchedule;
        int duplicate = 0; // Duplicate counter, Used for debugging purposes.

        _algoStats.setIterations(0);
        _algoStats.setIsFinish(false);

        while (true) {
            _algoStats.increment();
            cSchedule = _pq.poll();
            Solution cScheduleSolution = new Solution(cSchedule, _numP);
            _algoStats.setSolution(cScheduleSolution);
            ArrayList<ScheduleAStar> listVisitedForSize = _visited.get(cSchedule.getHash());
            if (listVisitedForSize != null && isIrrelevantDuplicate(listVisitedForSize, cSchedule)) {
                duplicate++;
                continue;
            } else {
                if (listVisitedForSize == null) {
                    listVisitedForSize = new ArrayList<ScheduleAStar>();
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
            int count = 0;
            for (INode node : cTable.keySet()) {
                if (cTable.get(node) == 0) {
                    for (int j = 1; j <= pidBound; j++) {
                        count++;
                    }
                }
            }

            CountDownLatch latch = new CountDownLatch(count);

            for (INode node : cTable.keySet()) {
                if (cTable.get(node) == 0) {
                    for (int pid = 1; pid <= pidBound; pid++) {
                        createSubSchedules(cSchedule, pid, node, cTable, latch);
                    }
                }
            }
            try {
                latch.await();
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }

            for (Hashtable<ScheduleAStar, Hashtable<INode, Integer>> subScheduleTables : _subSchedules) {
                for (ScheduleAStar subSchedule : subScheduleTables.keySet()) {
                    master.put(subSchedule, subScheduleTables.get(subSchedule));
                    _pq.add(subSchedule);
                }
            }
            _subSchedules.clear();
        }
        _algoStats.setIsFinish(true);
        _algoStats.setSolution(new Solution(cSchedule, _numP));
        return new Solution(cSchedule, _numP);
    }

    /**
     * Submits a new job to a thread pool.
     * @param cSchedule : parent schedule
     * @param pid : processor id
     * @param node : node/task to be scheduled
     * @param cTable : indegree table
     * @param latch : latch. (to check if the thread has finished its job)
     */
    public void createSubSchedules(ScheduleAStar cSchedule, int pid, INode node, Hashtable<INode, Integer> cTable, CountDownLatch latch) {
        _threadPool.submit(() -> {
            int start = calculateEarliestStartTime(cSchedule, pid, node);
            Hashtable<INode, Integer> newTable = getChildTable(cTable, node);

            ScheduleAStar newSchedule = new ScheduleAStar(
                    cSchedule,
                    new ScheduledTask(pid, node, start),
                    newTable);

            newSchedule.addHeuristic(
                    Collections.max(Arrays.asList(
                            h(newSchedule),
                            h1(newTable, newSchedule)
                    )));
            if (newSchedule.getTotal() <= _upperBound){
                ArrayList<ScheduleAStar> listVisitedForSizeV2 = _visited.get(newSchedule.getHash());
                if (listVisitedForSizeV2 != null && isIrrelevantDuplicate(listVisitedForSizeV2, newSchedule)) {
                    //Duplicate
                }else{
                    Hashtable<ScheduleAStar, Hashtable<INode, Integer>> subSchedule = new Hashtable<ScheduleAStar, Hashtable<INode, Integer>>();
                    subSchedule.put(newSchedule, newTable);
                    _subSchedules.add(subSchedule);
                }
            }
            latch.countDown();
        });
    }


}
