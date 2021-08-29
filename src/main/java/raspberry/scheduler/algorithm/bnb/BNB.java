package raspberry.scheduler.algorithm.bnb;

import raspberry.scheduler.algorithm.Algorithm;
import raspberry.scheduler.algorithm.common.*;
import raspberry.scheduler.app.visualisation.model.AlgoStats;
import raspberry.scheduler.graph.IEdge;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.INode;

import java.util.*;

public class BNB extends Algorithm {

    IGraph _graph;
    int _numP;
    int _bound;
    int _numNode;
    protected ScheduleB shortestPath;

    Stack<ScheduleB> _scheduleStack;
    Hashtable<Integer, ArrayList<ScheduleB>> _visited;
    private AlgoStats _algoStats;
    private FixOrderChecker _fixOrderChecker;
    private EquivalenceChecker _equivalenceChecker;

    public BNB(IGraph graphToSolve){
        super(graphToSolve);
        _graph = graphToSolve;
    }

    /**
     * BNB algorithm constructor. with bound
     *
     * @param graphToSolve  : graph to solve. (graph represents the tasks and dependencies)
     * @param numProcessors : number of processors allowed to use for scheduling.
     * @param bound : value representing the upperbound
     */
    public BNB(IGraph graphToSolve, int numProcessors, int bound) {
        super(graphToSolve);
        _graph = graphToSolve;
        _numP = numProcessors;
        _numNode = _graph.getNumNodes();
        _algoStats = AlgoStats.getInstance();
        _bound = bound;
        _fixOrderChecker = new FixOrderChecker(_graph);
        _equivalenceChecker = new EquivalenceChecker(_graph, numProcessors, this);
    }

    @Override
    public OutputSchedule findPath(){
        //Compute topological order and return it.
        shortestPath = null;

        // Stack - Keeps track of all available/scheduable tasks.
        _scheduleStack = new Stack<ScheduleB>();

        _visited = new Hashtable<Integer, ArrayList<ScheduleB>>();
        Hashtable<INode, Integer> rootTable = getRootTable();
        getH();

        for (INode i : rootTable.keySet()) {
            if (rootTable.get(i) == 0) {
                ScheduleB newSchedule = new ScheduleB(new ScheduledTask(1, i,0),
                        getChildTable(rootTable, i));
                newSchedule.addLowerBound( Math.max(lowerBound_1(newSchedule), super.getMaxCriticalPath()) );
                if ( newSchedule.getLowerBound() > _bound ){
                    continue;
                }
                _scheduleStack.push(newSchedule);
            }
        }

        ScheduleB cSchedule;
        Hashtable<INode, Integer> cTable;
        _algoStats.setIterations(0);
        _algoStats.setIsFinish(false);
        while (true) {
            if (_visited.size() > 5000000){
                _visited.clear();
            }
//            System.out.printf("Stack SIZE: %d\n", _scheduleStack.size());
            _algoStats.increment();
            if (_scheduleStack.isEmpty()) {
//                System.out.println("-- BOUND_DFS FINISHED --");
                break;
            }

            cSchedule = _scheduleStack.pop();
            if ( canPrune( cSchedule, true , false)){
                continue;
            }

            cTable = cSchedule.getIndegreeTable();

            if ( cSchedule.getSize() == _numNode ) {
                int totalFinishTime = cSchedule.getOverallFinishTime();
                if (totalFinishTime <= _bound) {
                    _bound = totalFinishTime;
                    shortestPath = cSchedule;
                    if( totalFinishTime < _bound){
//                        System.out.printf("\nBOUND : %d", _bound);
                    }
                }
                continue;
            }

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

            if ( _fixOrderChecker.check(freeNodes, cSchedule) &&
                    _fixOrderChecker.getFixOrder(freeNodes,cSchedule) != null){

                INode node = _fixOrderChecker.getFixOrder(freeNodes,cSchedule).get(0);
                for (int pid = 1; pid <= pidBound; pid++) {
                    int start = calculateEarliestStartTime(cSchedule, pid, node);
                    ScheduleB newSchedule = new ScheduleB(cSchedule,
                            new ScheduledTask(pid,node,start),
                            getChildTable(cTable,node));
                    newSchedule.addLowerBound( Math.max( lowerBound_1(newSchedule), super.getMaxCriticalPath() ) );
                    _algoStats.setSolution(new Solution(newSchedule, _numP));

                    if ( canPrune( newSchedule , false,false)){
                        continue;
                    }
                    _scheduleStack.push(newSchedule);
                }
            } else {
                for (INode node : freeNodes) {
                    for (int pid = 1; pid <= pidBound; pid++) {
                        int start = calculateEarliestStartTime(cSchedule, pid, node);
                        ScheduleB newSchedule = new ScheduleB(cSchedule,
                                new ScheduledTask(pid,node,start),
                                getChildTable(cTable,node));
                        newSchedule.addLowerBound( Math.max( lowerBound_1(newSchedule), super.getMaxCriticalPath() ) );
                        _algoStats.setSolution(new Solution(newSchedule, _numP));

                        if ( canPrune( newSchedule , false, true)){
                            continue;
                        }
                        _scheduleStack.push(newSchedule);
                    }
                }
            }
        }
        if (shortestPath == null){
//            System.out.println("FAILED TO FIND THE SHORTEST PATH");
        }
//        Helper.printPath(shortestPath);
        _algoStats.setIsFinish(true);
        _algoStats.setSolution(new Solution(shortestPath,_numP));
        return new Solution(shortestPath, _numP);
    }

    /**
     * Finds lowerbound using the critical path heuristic table.
     * @param schedule : schedule we want to find the heuristic cost for.
     * @return Integer : representing the heuristic cost.
     */
    public int lowerBound_1(ScheduleB schedule){
        return super.getHeuristicTable().get(schedule.getNode().getName()) + schedule.getFinishTime();
    }


    /**
     * Check if schedule can be pruned
     * @param cSchedule : schedule we want to check
     * @param visiting True : if the schedule is being visited.
     *                 False : otherwise
     * @return True : if it can be pruned
     *         False : if it cant be pruned
     */
    public boolean canPrune(ScheduleB cSchedule, Boolean visiting, Boolean checkEquivalence){
        if (cSchedule.getLowerBound() > _bound){ //I think we can do ">=" and not just ">"
            return true;
        }
        ArrayList<ScheduleB> listVisitedForSize = _visited.get(cSchedule.getHash());
        if (listVisitedForSize != null && isIrrelevantDuplicate(listVisitedForSize, cSchedule)) {
            return true;
        }else if( checkEquivalence && _equivalenceChecker.checkDuplicateBySwap(cSchedule)){
            return true;
        } else {
            if (visiting){
                if (listVisitedForSize == null) {
                    listVisitedForSize = new ArrayList<ScheduleB>();
                    _visited.put(cSchedule.getHash(), listVisitedForSize);
                }
                listVisitedForSize.add(cSchedule);
            }
            return false;
        }
    }


    /**
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
    public Boolean isIrrelevantDuplicate(ArrayList<ScheduleB> scheduleList, ScheduleB cSchedule) {
        for (ScheduleB s : scheduleList) {
            if ( s.equals3(cSchedule) ){
                if ( s.getLowerBound() > cSchedule.getLowerBound() ) {
//                    System.out.println("Re-opening node: Should not happen if heuristic is consistant");
                    return false;
                }else{
                    return true;
                }
            }
        }
        return false;
    }
}
