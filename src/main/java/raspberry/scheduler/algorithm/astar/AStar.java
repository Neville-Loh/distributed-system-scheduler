package raspberry.scheduler.algorithm.astar;

import raspberry.scheduler.algorithm.common.*;
import raspberry.scheduler.app.visualisation.model.AlgoStats;
import raspberry.scheduler.algorithm.Algorithm;
import raspberry.scheduler.graph.*;

import java.util.*;

/**
 * Implementation of A star algorithm.
 *
 * @author Takahiro
 */
public class AStar extends Algorithm {

    private IGraph _graph;
    int _numP;
    int _numNode;
    PriorityQueue<ScheduleAStar> _pq;
    Hashtable<Integer, ArrayList<ScheduleAStar>> _visited;
    private AlgoStats _algoStats;
    int _upperBound;

    private EquivalenceChecker _equivalenceChecker;
    private FixOrderChecker _fixOrderChecker;

    // debug and optimization
    private int duplicate = 0; // Duplicate counter, Used for debugging purposes.
    private int duplicateBySwap = 0; // Duplicate counter, Used for debugging purposes.
    private int fixOrderCount = 0; // Duplicate counter, Used for debugging purposes.

    // configuration 
    private final boolean DUPLICATE_ENABLE = true;
    private final boolean UPPERBOUND_ENABLE = true;
    private final boolean FIX_ORDER_ENABLE = true;

    /**
     * Constructor for A*
     *
     * @param graphToSolve  : graph to solve (graph represents the task and dependencies)
     * @param numProcessors : number of processor we can use to schedule tasks.
     */
    public AStar(IGraph graphToSolve, int numProcessors, int upperBound) {
        super(graphToSolve);
        _graph = graphToSolve;
        _pq = new PriorityQueue<ScheduleAStar>();
        _visited = new Hashtable<Integer, ArrayList<ScheduleAStar>>();
        _numP = numProcessors;
        _numNode = _graph.getNumNodes();
        _algoStats = AlgoStats.getInstance();
        _upperBound = upperBound;

        // checker
        _equivalenceChecker = new EquivalenceChecker(_graph, numProcessors);
        _fixOrderChecker = new FixOrderChecker(_graph);
    }

    public AStar(IGraph graphToSolve) {
        super(graphToSolve);
        _graph = graphToSolve;
    }

    /**
     * Compute the optimal scheduling
     * @return OutputSchedule : the optimal path/scheduling.
     */
    @Override
    public OutputSchedule findPath() {

        getH(); //Computes critical path

        Hashtable<INode, Integer> rootTable = getRootTable();

        for (INode node : rootTable.keySet()) {
            if (rootTable.get(node) == 0) {

                ScheduleAStar newSchedule = new ScheduleAStar(
                        new ScheduledTask(1,node, 0),
                        getChildTable(rootTable, node)
                );

                // note can't use drt here
                newSchedule.addHeuristic(
                        Collections.max(Arrays.asList(
                                h(newSchedule),
                                h1(getChildTable(rootTable, node), newSchedule)
                        ))
                );

                _pq.add(newSchedule);
            }
        }

        ScheduleAStar cSchedule;
        _algoStats.setIterations(0);
        _algoStats.setIsFinish(false);
      //  System.out.println(_observable.getIterations());
        while (true) {
            //System.out.printf("PQ SIZE: %d\n", _pq.size());
            _algoStats.increment();
            //System.out.println(_observable.getIterations());

            if (_pq.isEmpty()){
                System.out.println("Schedule is not found");
                return null;
            }

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

            Hashtable<INode, Integer> cTable = cSchedule._inDegreeTable;
            // Find the next empty processor. (
            int currentMaxPid = cSchedule.getMaxPid();
            int pidBound;
            if (currentMaxPid + 1 > _numP) {
                pidBound = _numP;
            } else {
                pidBound = currentMaxPid + 1;
            }

            ArrayList<INode> freeNodes = new ArrayList<INode>();
            for (INode node : cTable.keySet()) {
                if (cTable.get(node) == 0) {
                    freeNodes.add(node);
                }
            }

            if (FIX_ORDER_ENABLE && _fixOrderChecker.check(freeNodes, cSchedule) &&
                    _fixOrderChecker.getFixOrder(freeNodes,cSchedule) != null){
                INode node = _fixOrderChecker.getFixOrder(freeNodes,cSchedule).get(0);

                fixOrderCount++;
                for (int pid = 1; pid <= pidBound; pid++) {
                    int start = calculateEarliestStartTime(cSchedule, pid, node);
                    Hashtable<INode, Integer> newTable = getChildTable(cTable, node);
                    ScheduleAStar newSchedule = new ScheduleAStar(
                            cSchedule,
                            new ScheduledTask(pid, node, start),
                            newTable);

                    newSchedule.addHeuristic(
                            Collections.max(Arrays.asList(
                                    dataReadyTimeHeuristic(newSchedule),
                                    h(newSchedule),
                                    h1(newTable, newSchedule)
                            ))
                    );
                    _pq.add(newSchedule);
                    if (!UPPERBOUND_ENABLE || newSchedule.getTotal() <= _upperBound) {
                        ArrayList<ScheduleAStar> listVisitedForSizeV2 = _visited.get(newSchedule.getHash());
                        if (listVisitedForSizeV2 != null && isIrrelevantDuplicate(listVisitedForSizeV2, newSchedule)) {
                            duplicate++;
                        } else {
                            _pq.add(newSchedule);
                        }
                    }
                }
            } else {
                for (INode node : freeNodes) {
                    for (int pid = 1; pid <= pidBound; pid++) {
                        int start = calculateEarliestStartTime(cSchedule, pid, node);
                        Hashtable<INode, Integer> newTable = getChildTable(cTable, node);
                        ScheduleAStar newSchedule = new ScheduleAStar(
                                cSchedule,
                                new ScheduledTask(pid, node, start),
                                newTable);

                        newSchedule.addHeuristic(
                                Collections.max(Arrays.asList(
                                        dataReadyTimeHeuristic(newSchedule),
                                        h(newSchedule),
                                        h1(newTable, newSchedule)
                                ))
                        );

                        if (!UPPERBOUND_ENABLE || newSchedule.getTotal() <= _upperBound) {
                            ArrayList<ScheduleAStar> listVisitedForSizeV2 = _visited.get(newSchedule.getHash());
                            if (listVisitedForSizeV2 != null && isIrrelevantDuplicate(listVisitedForSizeV2, newSchedule)) {
                                duplicate++;
                            } else if (DUPLICATE_ENABLE && _equivalenceChecker.checkDuplicateBySwap(newSchedule)) {
                                duplicateBySwap++;
                            } else {
                                _pq.add(newSchedule);
                            }
                        }
                    }
                }
            }
        }

        System.out.printf("PQ SIZE: %d\n", _pq.size());
        System.out.printf("\nDUPLCIATE : %d\n", duplicate);
        System.out.printf("\nNEW DUPLCIATE : %d\n", duplicateBySwap);
        System.out.printf("FIX TASK ORDER Count: %d\n", fixOrderCount);
        _algoStats.setIsFinish(true);
        _algoStats.setSolution(new Solution(cSchedule,_numP));

        //System.out.println(cSchedule);
        return new Solution(cSchedule, _numP);
    }

    /**
     * This is the heuristic that uses the data ready time of free task
     * the heuristic is the earliest star time in all processor plus to compute time of the task  + the
     * critical path weight.
     * The final return value is adjusted for finish time of the last task.
     * @author Neville
     * @param cSchedule current schedule
     * @return heuristic value h(schedule)
     */
    public int dataReadyTimeHeuristic(ScheduleAStar cSchedule){
        Hashtable<INode, Integer> finishTime = new Hashtable<>();
        Hashtable<INode, Integer> criticalPathTable = _graph.getCriticalPathWeightTable();

        // get all free task
        Hashtable<INode, Integer> cTable = cSchedule.getInDegreeTable();
        ArrayList<INode> freeNodes = new ArrayList<INode>();
        for (INode node : cTable.keySet()) {
            if (cTable.get(node) == 0) {
                freeNodes.add(node);
            }
        }
        if (freeNodes.size() == 0){
            return 0;
        }

        // finish time = min DRT for every processor + critical path weight
        freeNodes.forEach( (node) ->{
            int minStartTime = Integer.MAX_VALUE;
            for (int pid = 1; pid <= _numP; pid++) {
                minStartTime = Math.min(minStartTime, calculateEarliestStartTime(cSchedule, pid, node));
            }
            finishTime.put(node, minStartTime + node.getValue() + criticalPathTable.get(node));
        });
        return Math.max(0, Collections.max(finishTime.values()) - cSchedule.getFinishTime());

    }


    /**
     * For each task that was scheduled last in the processor.
     * -> find the largest cost
     * --> where cost = finish time of the task + heuristic of the task
     *
     * @param cSchedule : schedule of which we are trying to find heuristic cost for.
     * @return integer : represeting the heuristic cost
     */
    public int h(ScheduleAStar cSchedule) {
        int max = 0;
        for (String s : cSchedule.getLastForEachProcessor().values()) {
            int tmp = super.getHeuristicTable().get(s) + cSchedule.getScheduling().get(s).get(1) +
                    _graph.getNode(s).getValue();
            if (tmp > max) {
                max = tmp;
            }
        }
        return max - cSchedule.getFinishTime();
    }

    /**
     * Find the best case scheduling where all task are evenly spread out throughout the different processors.
     *
     * @param x         : Hashtable representing the outDegree table. (All the tasks in the table has not been scheduled yet)
     * @param cSchedule : current schedule . Used to find the last task which was scheduled for each processor.
     * @return Integer : Representing the best case scheduling.
     */
    public int h1(Hashtable<INode, Integer> x, ScheduleAStar cSchedule) {
        int sum = 0;
        for (String s : cSchedule.getLastForEachProcessor().values()) {
            sum += cSchedule.getScheduling().get(s).get(1) +
                    _graph.getNode(s).getValue();
        }
        for (INode i : x.keySet()) {
            sum += i.getValue();
        }
        return sum / _numP - cSchedule.getFinishTime();
    }


    /**
     * TODO : FIND OUT IF WE ACTUALLY NEED THIS FUNCTION
     * OR THIS -> "listVisitedForSize.contains(cSchedule)" JUST WORKS FINE.
     * Find out if the duplicate schedule exists.
     * -> if we find one, check if the heuristic is larger or smaller.
     * --> if its larger then we dont need to reopen.
     * --> if its smaller we need to reopen.
     *
     * @param scheduleList : list of visited schedule. (with same getHash() value)
     * @param cSchedule    : schedule that we are trying to find if duplicate exists of not.
     * @return True : if reopening is not needed
     * False : if reopening needs to happend for this schedule.
     */
    public Boolean isIrrelevantDuplicate(ArrayList<ScheduleAStar> scheduleList, ScheduleAStar cSchedule) {
        if (!DUPLICATE_ENABLE) return false;
        for (ScheduleAStar s : scheduleList) {
            if ( s.equals2(cSchedule) ){
                if ( s.getTotal() > cSchedule.getTotal()) {
//                    System.out.printf("%d -> %d\n", s.getTotal(), cSchedule.getTotal());
                    return false;
                }else{
                    return true;
                }
            }
        }
        return false;
    }
}
