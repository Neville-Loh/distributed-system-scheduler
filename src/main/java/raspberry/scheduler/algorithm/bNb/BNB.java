package raspberry.scheduler.algorithm.bNb;

import raspberry.scheduler.algorithm.Algorithm;
import raspberry.scheduler.algorithm.astar.ScheduleAStar;
import raspberry.scheduler.algorithm.common.*;
import raspberry.scheduler.algorithm.util.Helper;
import raspberry.scheduler.app.visualisation.model.AlgoStats;
import raspberry.scheduler.graph.IEdge;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.INode;
import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;

import java.util.*;

public class BNB implements Algorithm {

    IGraph _graph;
    int _numP;
    int _bound;
    int _numNode;
    int _maxCriticalPath;
    protected ScheduleB shortestPath;

    Hashtable<String, Integer> _heuristicTable;
    Stack<ScheduleB> _scheduleStack;
    Hashtable<Integer, ArrayList<ScheduleB>> _visited;
    private AlgoStats _algoStats;
    private FixOrderChecker _fixOrderChecker;
    private EquivalenceChecker _equivalenceChecker;

    public BNB(IGraph graphToSolve){
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
        _graph = graphToSolve;
        _numP = numProcessors;
        _numNode = _graph.getNumNodes();
        _algoStats = AlgoStats.getInstance();
        _bound = bound;
        _fixOrderChecker = new FixOrderChecker(_graph);
        _equivalenceChecker = new EquivalenceChecker(_graph, numProcessors);
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
                newSchedule.addLowerBound( Math.max(lowerBound_1(newSchedule), _maxCriticalPath) );
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
            if ( canPrune( cSchedule, true )){
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
                    newSchedule.addLowerBound( Math.max( lowerBound_1(newSchedule), _maxCriticalPath ) );
                    _algoStats.setSolution(new Solution(newSchedule, _numP));

                    if ( canPrune( newSchedule , false)){
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
                        newSchedule.addLowerBound( Math.max( lowerBound_1(newSchedule), _maxCriticalPath ) );
                        _algoStats.setSolution(new Solution(newSchedule, _numP));

                        if ( canPrune( newSchedule , false)){
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
     * Computes the earliest time we can schedule a task in a specific processor.
     *
     * @param parentSchedule   : parent schedule of this partial schedule.
     * @param processorId      : the specific processor we want to scheude task into.
     * @param nodeToBeSchedule : node/task to be scheduled.
     * @return Integer : representing the earliest time. (start time)
     */
    public int calculateEarliestStartTime(Schedule parentSchedule, int processorId, INode nodeToBeSchedule) {
        // Find last finish parent node
        // Find last finish time for current processor id.
        ScheduleB last_processorId_use = null; //last time processor with "processorId" was used.
        ScheduleB cParentSchedule = (ScheduleB)parentSchedule;

        while (cParentSchedule != null) {
            if (cParentSchedule.getPid() == processorId) {
                last_processorId_use = cParentSchedule;
                break;
            }
            cParentSchedule = cParentSchedule.getParent();
        }

        //last time parent was used. Needs to check for all processor.
        int finished_time_of_last_parent = 0;
        if (last_processorId_use != null) {
            finished_time_of_last_parent = last_processorId_use.getFinishTime();
        }

        cParentSchedule =  (ScheduleB)parentSchedule;
        while (cParentSchedule != null) {
            // for edges in current parent scheduled node
            INode last_scheduled_node = cParentSchedule.getNode();
            for (IEdge edge : _graph.getOutgoingEdges(last_scheduled_node.getName())) {
                if (edge.getChild() == nodeToBeSchedule && cParentSchedule.getPid() != processorId) {
                    try {
                        int communicationWeight = _graph.getEdgeWeight(cParentSchedule.getNode(), nodeToBeSchedule);
                        //  finished_time_of_last_parent  <
                        if (finished_time_of_last_parent < (cParentSchedule.getFinishTime() + communicationWeight)) {
                            finished_time_of_last_parent = cParentSchedule.getFinishTime() + communicationWeight;
                        }
                    } catch (EdgeDoesNotExistException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            cParentSchedule = cParentSchedule.getParent();
        }
        return finished_time_of_last_parent;
    }

    /**
     * Finds lowerbound using the critical path heuristic table.
     * @param schedule : schedule we want to find the heuristic cost for.
     * @return Integer : representing the heuristic cost.
     */
    public int lowerBound_1(ScheduleB schedule){
        return _heuristicTable.get(schedule.getNode().getName()) + schedule.getFinishTime();
    }


    /**
     * Check if schedule can be pruned
     * @param cSchedule : schedule we want to check
     * @param visiting True : if the schedule is being visited.
     *                 False : otherwise
     * @return True : if it can be pruned
     *         False : if it cant be pruned
     */
    public boolean canPrune(ScheduleB cSchedule, Boolean visiting){
        if (cSchedule.getLowerBound() > _bound){ //I think we can do ">=" and not just ">"
            return true;
        }
        ArrayList<ScheduleB> listVisitedForSize = _visited.get(cSchedule.getHash());
        if (listVisitedForSize != null && isIrrelevantDuplicate(listVisitedForSize, cSchedule)) {
            return true;
        }else if( _equivalenceChecker.checkDuplicateBySwap(cSchedule)){
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
    public Boolean isIrrelevantDuplicate(ArrayList<ScheduleB> scheduleList, ScheduleB cSchedule) {
        for (ScheduleB s : scheduleList) {
            if ( s.equals2(cSchedule) ){
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

    /**
     * Creates intial outDegree table for the graph.
     *
     * @return : Hashtable : Key : Node
     * Value : Integer representing number of outDegree edges.
     */
    public Hashtable<INode, Integer> getRootTable() {
        Hashtable<INode, Integer> tmp = new Hashtable<INode, Integer>();
        for (INode i : _graph.getAllNodes()) {
            tmp.put(i, 0);
        }
        for (INode i : _graph.getAllNodes()) {
            for (IEdge j : _graph.getOutgoingEdges(i.getName())) {
                tmp.put(j.getChild(), tmp.get(j.getChild()) + 1);
            }
        }
        return tmp;
    }

    /**
     * Creates outDegree table for child node.
     *
     * @param parentTable : parent schedule's outDegree table.
     * @param x           : Node :that was just scheduled
     * @return : Hashtable : Key : Node
     * Value : Integer representing number of outDegree edges.
     */
    public Hashtable<INode, Integer> getChildTable(Hashtable<INode, Integer> parentTable, INode x) {
        Hashtable<INode, Integer> tmp = new Hashtable<INode, Integer>(parentTable);
        tmp.remove(x);
        for (IEdge i : _graph.getOutgoingEdges(x.getName())) {
            tmp.put(i.getChild(), tmp.get(i.getChild()) - 1);
        }
        return tmp;
    }

    /**
     * Creates a maximum dependency path table.
     * Also find the maximum critical path cost of the graph.
     * where key : String <- task's name.
     * value : int <- maximum path cost.
     */
    public void getH() {
        _heuristicTable = new Hashtable<String, Integer>();
        for (INode i : _graph.getAllNodes()) {
            _heuristicTable.put(i.getName(), getHRecursive(i));
        }
        _maxCriticalPath = Collections.max(_heuristicTable.values());
    }

    /**
     * Recursive call child node to get the cost. Get the maximum cost path and return.
     *
     * @param n : INode : task that we are trying to find the heuristic weight.
     * @return integer : Maximum value of n's child path.
     */
    public int getHRecursive(INode n) {
        List<IEdge> e = _graph.getOutgoingEdges(n.getName());
        if (e.size() == 0) {
            return 0;
        } else if (e.size() == 1) {
            return getHRecursive(e.get(0).getChild()) + e.get(0).getChild().getValue();
        }
        int max = 0;
        for (IEdge i : e) {
            int justCost = getHRecursive(i.getChild()) + i.getChild().getValue();
            if (max < justCost) {
                max = justCost;
            }
        }
        return max;
    }
}
