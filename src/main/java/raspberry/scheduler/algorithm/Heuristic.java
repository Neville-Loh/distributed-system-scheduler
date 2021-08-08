package raspberry.scheduler.algorithm;

import raspberry.scheduler.graph.IEdge;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.INode;

import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class Heuristic {

    /**
     * Empty constructor.
     */
    public Heuristic(){ }

    /**
     * Computes all heuristic function and return the best heuristic. (largest value)
     * @param heuristicTable : table containing a node name as key,
     *                       and heuristic cost based on dependencie as value.
     * @param i : Node that we are going to be scheduling.
     * @param rootTable : table representing the outDegree edge of each nodes.
     * @param maxCriticalPath : maximum critical path cost based on dependencies.
     * @param numP : number of processors allowed to use for scheduling.
     * @return Integer representing the lower bound of this partial schedule
     */
    public int getH(Hashtable<String, Integer> heuristicTable, INode i,
                    Hashtable<INode, Integer> rootTable,
                    int maxCriticalPath,
                    int numP){
        return Collections.max(Arrays.asList(
                heuristicTable.get( i.getName() ),
                h1(rootTable, i.getValue(), numP),
                maxCriticalPath-i.getValue(),
                h2(rootTable, 0,i.getValue(), null, numP)));
    }

    /**
     * Find the best case scheduling where all task are evenly spread out throughtout the different processors.
     * @param x : Hashtable represting the outDegree table. (All the tasks in the table has not been scheduled yet)
     * @param finishTime : finish time of task that was previously just scheduled.
     * @param numP : number of processors allowed to use for scheduling.
     * @return Integer : Representing the heuristics of best case scheduling.
     */
    public int h1(Hashtable<INode, Integer> x , int finishTime, int numP){
        int sum = finishTime;
        for (INode i: x.keySet()){
            sum += i.getValue();
        }
        return sum/numP - finishTime;
    }

    /**
     * Find the best case scheduling where all task are evenly spread out throughtout the different processors.
     * Different to h1(), this function also take into account the already scheduled tasks.
     * Better heuristic but requires more computation.
     * @param x : Hashtable represting the outDegree table. (All the tasks in the table has not been scheduled yet)
     * @param start : start time of task that was previously just scheduled.
     * @param cost : cost of task that was previously just scheduled.
     * @param parent : parent schedule.
     * @param numP : number of processors allowed to use for scheduling.
     * @return Integer : Representing the heuristics of best case scheduling.
     */
    public int h2(Hashtable<INode, Integer> x, int start, int cost, Schedule parent, int numP){
        int sum = cost;
        for ( int i=0; i<numP; i++){
            sum += getLastPTime(parent, i);
        }
        for (INode i: x.keySet()){
            sum += i.getValue();
        }
        int spreadOutTime =  sum/numP;
        return spreadOutTime-start-cost;
    }

    /**
     * Finds the last time a specific processor was used.
     * @param cParentSchedule : parent schedule.
     * @param processorId : processor id.
     * @return Integer : Representing the time of last time a specific processor was used.
     */
    public int getLastPTime(Schedule cParentSchedule, int processorId){
        while ( cParentSchedule != null){
            if ( cParentSchedule.p_id == processorId ){
                return cParentSchedule.finishTime;
            }
            cParentSchedule = cParentSchedule.parent;
        }
        return 0;
    }

    /**
     * Computes the heuristic table based on dependencies.
     * @param graph : graph representing the tasks and its dependencies.
     * @return Hashtable : where
     *                      key : name of the task.
     *                      value : heuristic cost based on dependencies.
     */
    public Hashtable<String, Integer> getHeuristicTable(IGraph graph){
        Hashtable<String, Integer> heuristic = new Hashtable<String, Integer>();
        for ( INode i : graph.getAllNodes()){
            heuristic.put(i.getName(), 0);
        }
        for ( INode i: graph.getAllNodes() ){
            heuristic.put(i.getName(), getHRecursive( i , graph));
        }
        return heuristic;
    }

    /**
     * Recursive function used to calculate the heuristics based on dependencies of the task.
     * @param n : Node to find heuristic cost for.
     * @param graph : graph representing the tasks and its dependencies.
     * @return : Integer representiing the heuristic cost of node n.
     */
    public int getHRecursive( INode n, IGraph graph ){
        List<IEdge> e = graph.getOutgoingEdges(n.getName());
        if ( e.size() == 0){
            return 0;
        } else if (e.size() == 1){
            return getHRecursive(e.get(0).getChild(), graph) + n.getValue();
        }
        int max = 0;
        for ( IEdge i : e){
            int justCost = getHRecursive(i.getChild(), graph) + n.getValue();
            if ( max < justCost ){
                max = justCost;
            }
        }
        return max;
    }
}
