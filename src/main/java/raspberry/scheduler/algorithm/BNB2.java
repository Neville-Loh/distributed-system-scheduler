package raspberry.scheduler.algorithm;

import raspberry.scheduler.graph.IEdge;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.INode;
import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Stack;

public class BNB2 implements Algorithm{

    IGraph _graph;
    int _numP;
    int _bound;
    int _numNode;
    int _maxCriticalPath;
    Hashtable<String, Integer> _heuristicTable;
    Stack<Schedule> _scheduleStack;
    Hashtable<Integer, ArrayList<Schedule>> _visited;

    /**
     * BNB algorithm constructor. with bound
     *
     * @param graphToSolve  : graph to solve. (graph represents the tasks and dependencies)
     * @param numProcessors : number of processors allowed to use for scheduling.
     * @param bound : value representing the upperbound
     */
    public BNB2(IGraph graphToSolve, int numProcessors, int bound) {
        _graph = graphToSolve;
        _numP = numProcessors;
        _numNode = _graph.getNumNodes();
        _bound = bound;
    }

    @Override
    public OutputSchedule findPath(){
        //Compute topological order and return it.
        Schedule shortestPath = null;
        // Stack - Keeps track of all available/scheduable tasks.
        _scheduleStack = new Stack<Schedule>();
        _visited = new Hashtable<Integer, ArrayList<Schedule>>();
        Hashtable<INode, Integer> rootTable = getRootTable();
        _heuristicTable = Heuristic.getHeuristicTable(_graph);
        _maxCriticalPath = Collections.max(_heuristicTable.values());


        for (INode i : rootTable.keySet()) {
            if (rootTable.get(i) == 0) {
                Schedule newSchedule = new Schedule(0, null, i, 0, getChildTable(rootTable, i));
//                System.out.printf( "\nTask: %s, H_val: %d", i.getName(), getHeuristicVal());
//                newSchedule.addHeuristic(  );
//                master.put(newSchedule, getChildTable(rootTable, i));
                _scheduleStack.push(newSchedule);

                if (getUpperBound() < _bound) {
                    _bound = getUpperBound();
                }
            }
        }
        return null;
    }


    public int getUpperBound(){
        return Integer.MAX_VALUE;
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
}
