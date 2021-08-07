package raspberry.scheduler.algorithm;

import raspberry.scheduler.graph.EdgeDoesNotExistException;
import raspberry.scheduler.graph.IEdge;
import raspberry.scheduler.graph.INode;
import raspberry.scheduler.graph.IGraph;

import java.util.*;

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
     * @param graphToSolve : graph to solve. (graph represents the tasks and dependencies)
     * @param numProcessors : number of processors allowed to use for scheduling.
     */
    public BNB(IGraph graphToSolve, int numProcessors){
        _graph = graphToSolve;
        _numP = numProcessors;
        _numNode = _graph.getNumNodes();
    }

    /**
     * Compute the optimal scheduling
     * @return OutputSchedule : the optimal path/scheduling.
     */
    @Override
    public OutputSchedule findPath() {
        _bound = Integer.MAX_VALUE; /// Set init bound to infinity.
        BNB_DFS();
        return null;
    }

    /**
     * computes the path using DFS as a search algorithm.
     * and uses branch and bound to narrow down the search space.
     */
    public void BNB_DFS(){
        //Compute topological order and return it.
        Schedule shortestPath = null;
        // Stack - Keeps track of all available/scheduable tasks.
        _scheduleStack = new Stack<Schedule>();

        Hashtable<INode, Integer> rootTable = getRootTable();
        Hashtable<Schedule, Hashtable<INode, Integer>> master = new Hashtable<Schedule, Hashtable<INode, Integer>>();
        _heuristicTable = new Heuristic().getHeuristicTable(_graph);
        _maxCriticalPath = Collections.max( _heuristicTable.values() );

        for (INode i : rootTable.keySet()){
            if (rootTable.get(i) == 0 ) {
                Schedule newSchedule = new Schedule(0, null, i, 0);
                master.put(newSchedule, getChildTable(rootTable, i));
                _scheduleStack.push(newSchedule);
                int cBound = getUpperBound();
                if ( cBound < _bound){
                    _bound = cBound;
                }
            }
        }

        Schedule cSchedule;
        Hashtable<INode, Integer> cTable;
        while (true){
//            System.out.printf("\n Stack SIZE :  %d", _scheduleStack.size());
            cSchedule = _scheduleStack.pop();
            cTable = master.get(cSchedule);
            master.remove(cSchedule);

            int cBound = getUpperBound();
            if ( cBound < _bound){
                _bound = cBound;
            }

            if (cSchedule._lowerBound >= _bound) {
                // Bounded. Meaning, this current schedule is too slow.
                // We already know better schedule so ignore.
            }else if( getLowerBound( cSchedule.node, cTable) >= _bound ){
                // REASON TO SEPRATE the first "IF" and second "else if" statement is for optimization.
                // We also already know better schedle.
            }else{
                if (cSchedule.size == _numNode ){
                    if (cSchedule._lowerBound < _bound){
                        _bound = cSchedule._lowerBound;
                        shortestPath = cSchedule;
                        System.out.printf("BOUND : %d\n",_bound);
                    }
                }else {
                    for (INode i : cTable.keySet()) {
                        if (cTable.get(i) == 0) {
                            // this node "i" has outDegree of 0, so add childs to the stack.
                            for (int j = 0; j < _numP; j++) {
                                int start = calculateCost(cSchedule, j, i);
                                Schedule newSchedule = new Schedule(start, cSchedule, i, j);
                                Hashtable<INode, Integer> newTable = getChildTable(cTable,i);
                                master.put(newSchedule, newTable);
                                _scheduleStack.push(newSchedule);
                            }
                        }
                    }
                }
            }
            if (_scheduleStack.isEmpty()){
                System.out.println("-- BOUND_DFS FINISHED --");
                break;
            }
        }
        printPath(shortestPath);
    }

    /**
     * Computes the earliest time we can schedule a task in a specific processor.
     * @param parentSchedule : parent schedule of this partial schedule.
     * @param processorId : the specific processor we want to scheude task into.
     * @param nodeToBeSchedule : node/task to be scheduled.
     * @return Integer : representing the earliest time. (start time)
     */
    public int calculateCost(Schedule parentSchedule, int processorId, INode nodeToBeSchedule) {
        // Find last finish parent node
        // Find last finish time for current processor id.
        Schedule last_processorId_use = null; //last time processor with "processorId" was used.
        Schedule cParentSchedule = parentSchedule;

        while ( cParentSchedule != null){
            if ( cParentSchedule.p_id == processorId ){
                last_processorId_use = cParentSchedule;
                break;
            }
            cParentSchedule = cParentSchedule.parent;
        }

        //last time parent was used. Needs to check for all processor.
        int finished_time_of_last_parent=0;
        if (last_processorId_use != null){
            finished_time_of_last_parent = last_processorId_use.finishTime;
        }

        cParentSchedule = parentSchedule;
        while ( cParentSchedule != null){
            // for edges in current parent scheduled node
            INode last_scheduled_node = cParentSchedule.node;
            for ( IEdge edge: _graph.getOutgoingEdges(last_scheduled_node.getName())){
                if (edge.getChild() == nodeToBeSchedule && cParentSchedule.p_id != processorId){
                    try {
                        int communicationWeight = _graph.getEdgeWeight(cParentSchedule.node,nodeToBeSchedule);
                        //  finished_time_of_last_parent  <
                        if (finished_time_of_last_parent < (cParentSchedule.finishTime + communicationWeight)){
                            finished_time_of_last_parent = cParentSchedule.finishTime + communicationWeight;
                        }
                    } catch (EdgeDoesNotExistException e){
                        System.out.println(e.getMessage());
                    }
                }
            }
            cParentSchedule = cParentSchedule.parent;
        }
        return finished_time_of_last_parent;
    }

    /**
     * Print hashtable : Used for debugging purposes.
     * @param table : table to print. ( i think this was mainly used for printing outDegreeEdge table)
     */
    public void printHashTable(Hashtable<INode, Integer> table){
        System.out.printf("{ ");
        for (INode i: table.keySet()){
            System.out.printf("%s_%d, ", i.getName(), table.get(i));
        }
        System.out.printf(" }\n");
    }

    /**
     * Creates intial outDegree table for the graph.
     * @return : Hashtable : Key : Node
     *                      Value : Integer representing number of outDegree edges.
     */
    public Hashtable<INode, Integer> getRootTable(){
        Hashtable<INode, Integer> tmp = new Hashtable<INode, Integer>();
        for (INode i : _graph.getAllNodes()){
            tmp.put(i,0);
        }
        for (INode i : _graph.getAllNodes()){
            for (IEdge j : _graph.getOutgoingEdges(i.getName())){
                tmp.put( j.getChild(), tmp.get(j.getChild()) + 1);
            }
        }
        return tmp;
    }

    /**
     * Creates outDegree table for child node.
     * @param parentTable : parent schedule's outDegree table.
     * @param x : Node :that was just scheduled
     * @return : Hashtable : Key : Node
     *                      Value : Integer representing number of outDegree edges.
     */
    public Hashtable<INode, Integer> getChildTable(Hashtable<INode, Integer> parentTable, INode x){
        Hashtable<INode, Integer> tmp = new Hashtable<INode, Integer>(parentTable);
        tmp.remove(x);
        for (IEdge i : _graph.getOutgoingEdges(x.getName())){
            tmp.put( i.getChild(),  tmp.get(i.getChild()) - 1 );
        }
        return tmp;
    }

    /**
     * Print the path to command line/ terminal.
     * @param x : Partial schedule to print.
     */
    public void printPath(Schedule x){
        System.out.println("");
        Hashtable<INode, int[]> path = x.getPath();

        //path.sort((o1, o2) -> o1.node.getName().compareTo(o2.node.getName()));
        for (INode i: path.keySet()){
            System.out.printf("%s : {start:%d}, {finish:%d}, {p_id:%d} \n",
                    i.getName(), path.get(i)[0], path.get(i)[1], path.get(i)[2]);
        }
    }

    /**
     * Compute the upperBound of this partial schedule.
     * @return
     */
    public int getUpperBound(){
        return Integer.MAX_VALUE;
    }

    /**
     * Compute the lower bound of this partial schedule.
     * @param i : Node that we are going to be scheduling.
     * @param outDegreeTable : table representing the outDegree edge of each nodes.
     * @return : Integer representing the lower bound of this partial schedule
     */
    public int getLowerBound( INode i, Hashtable<INode, Integer> outDegreeTable){
        return new Heuristic().getH( _heuristicTable,i, outDegreeTable, _maxCriticalPath, _numP);
    }
}
