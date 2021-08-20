package raspberry.scheduler.algorithm;

import raspberry.scheduler.algorithm.bNb.Heuristic;
import raspberry.scheduler.algorithm.util.Helper;
import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;
import raspberry.scheduler.graph.IEdge;
import raspberry.scheduler.graph.INode;
import raspberry.scheduler.graph.IGraph;

import java.util.*;

/**
 * Implementation of Branch and Bound algorithm with DFS.
 *
 * @author Takahiro
 */
public class BNB implements Algorithm {

    IGraph _graph;
    int _numP;
    int _bound;
    int _numNode;
    int _maxCriticalPath;
    Hashtable<String, Integer> _heuristicTable;
    Stack<Schedule> _scheduleStack;

    /**
     * BNB algorithm constructor.
     *
     * @param graphToSolve  : graph to solve. (graph represents the tasks and dependencies)
     * @param numProcessors : number of processors allowed to use for scheduling.
     */
    public BNB(IGraph graphToSolve, int numProcessors) {
        _graph = graphToSolve;
        _numP = numProcessors;
        _numNode = _graph.getNumNodes();
        _bound = Integer.MAX_VALUE;
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
        _bound = bound;
    }

    /**
     * Compute the optimal scheduling
     *
     * @return OutputSchedule : the optimal path/scheduling.
     */
    @Override
    public OutputSchedule findPath() {
//        _bound = Integer.MAX_VALUE; /// Set init bound to infinity.
        return BNB_DFS();
    }

    /**
     * computes the path using DFS as a search algorithm.
     * and uses branch and bound to narrow down the search space.
     */
    public OutputSchedule BNB_DFS() {
        //Compute topological order and return it.
        Schedule shortestPath = null;
        // Stack - Keeps track of all available/scheduable tasks.
        _scheduleStack = new Stack<Schedule>();

        Hashtable<INode, Integer> rootTable = getRootTable();
//        Hashtable<Schedule, Hashtable<INode, Integer>> master = new Hashtable<Schedule, Hashtable<INode, Integer>>();
        _heuristicTable = Heuristic.getHeuristicTable(_graph);
        _maxCriticalPath = Collections.max(_heuristicTable.values());

        for (INode i : rootTable.keySet()) {
            if (rootTable.get(i) == 0) {
                Schedule newSchedule = new Schedule(0, null, i, 0, getChildTable(rootTable, i));
                newSchedule.addHeuristic(
                        new Heuristic().getH(_heuristicTable, i, newSchedule._inDegreeTable, _maxCriticalPath, _numP));
//                master.put(newSchedule, getChildTable(rootTable, i));
                _scheduleStack.push(newSchedule);

                if (getUpperBound() < _bound) {
                    _bound = getUpperBound();
                }
            }
        }

        Schedule cSchedule;
        Hashtable<INode, Integer> cTable;
        while (true) {
//            System.out.printf("\n Stack SIZE :  %d", _scheduleStack.size());
            cSchedule = _scheduleStack.pop();
//            cTable = master.get(cSchedule);
//            master.remove(cSchedule);
            cTable = cSchedule._inDegreeTable;

            if (getUpperBound() < _bound) {
                _bound = getUpperBound();
            }

            if (cSchedule.getLowerBound() >= _bound) {
                // Bounded. Meaning, this current schedule is too slow.
                // We already know better schedule so ignore.
            } else {
                if (cSchedule.getSize() == _numNode) {
                    int totalFinishTime = getMaxFinishTime(cSchedule);
                    if ( totalFinishTime < _bound) {
                        _bound = totalFinishTime;
                        shortestPath = cSchedule;
                        System.out.printf("BOUND : %d\n", _bound);
                    }
                } else {
                    for (INode i : cTable.keySet()) {
                        if (cTable.get(i) == 0) {
                            // this node "i" has outDegree of 0, so add childs to the stack.
                            for (int j = 0; j < _numP; j++) {
                                int start = calculateCost(cSchedule, j, i);
                                Hashtable<INode, Integer> newTable = getChildTable(cTable, i);
                                Schedule newSchedule = new Schedule(start, cSchedule, i, j, newTable);
//                                master.put(newSchedule, newTable);
                                _scheduleStack.push(newSchedule);
                            }
                        }
                    }
                }
            }
            if (_scheduleStack.isEmpty()) {
                System.out.println("-- BOUND_DFS FINISHED --");
                break;
            }
        }
        Helper.printPath(shortestPath);
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
    public int calculateCost(Schedule parentSchedule, int processorId, INode nodeToBeSchedule) {
        // Find last finish parent node
        // Find last finish time for current processor id.
        Schedule last_processorId_use = null; //last time processor with "processorId" was used.
        Schedule cParentSchedule = parentSchedule;

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

        cParentSchedule = parentSchedule;
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
     * Compute the upperBound of this partial schedule.
     *
     * @return
     */
    public int getUpperBound() {
        return Integer.MAX_VALUE;
    }

    /**
     * Compute the lower bound of this partial schedule.
     *
     * @param i              : Node that we are going to be scheduling.
     * @param outDegreeTable : table representing the outDegree edge of each nodes.
     * @return : Integer representing the lower bound of this partial schedule
     */
    public int getLowerBound(INode i, Hashtable<INode, Integer> outDegreeTable) {
        return new Heuristic().getH(_heuristicTable, i, outDegreeTable, _maxCriticalPath, _numP);
    }

    public int getMaxFinishTime(Schedule cSchedule){
        int max = 0;
        for ( String s: cSchedule.getLastForEachProcessor().values() ){
            max = Math.max( max, cSchedule.getTaskStartTime(s) + _graph.getNode(s).getValue());
        }
        return max;
    }
}
