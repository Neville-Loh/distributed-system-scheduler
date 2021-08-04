package raspberry.scheduler.algorithm.sma;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import raspberry.scheduler.algorithm.Algorithm;
import raspberry.scheduler.algorithm.OutputSchedule;
import raspberry.scheduler.algorithm.Solution;
import raspberry.scheduler.algorithm.util.Helper;
import raspberry.scheduler.graph.*;


/**
 * Rough implementation of famous algorithm SMA
 * @author Neville L.
 */
public class MemoryBoundAStar implements Algorithm {
    private IGraph _graph;
    private TwoWayPriorityQueue _pq;
    private final int TOTAL_NUM_PROCESSOR;
    private int numNode;
    private final int MAX_NUMBER_NODE = 2000;
    private Hashtable<INode, Integer> _criticalPathWeightTable;


    /**
     * Class constructor
     * @param taskDependencyGraph dependency digraph of the task
     * @param totalProcessorNumber the total number of processor available for schedule
     */
    public MemoryBoundAStar(IGraph taskDependencyGraph, int totalProcessorNumber){
        _graph = taskDependencyGraph;
        _pq = new TwoWayPriorityQueue();
        TOTAL_NUM_PROCESSOR = totalProcessorNumber;
        numNode = _graph.getNumNodes();
        _criticalPathWeightTable = _graph.getCriticalPathWeightTable();
    }

    /**
     * Get the total compute time require to compute all tasks
     * in the dependency graph when given one resource
     * @return computeTime
     */
    public int getTotalComputeTime(){
        System.out.println("total compute time: " + _graph.getAllNodes().stream().mapToInt(INode::getValue).sum());
        return _graph.getAllNodes().stream().mapToInt(INode::getValue).sum();
    }

//    private static class ForgottenSchedule{
//        Collection<INode> nodes;
//        int fCost;
//        ForgottenSchedule(int fCost){
//            this.nodes = new ArrayList<INode>();
//            this.fCost = fCost;
//        }
//        public void addNode(INode node){
//            nodes.add(node);
//        }
//
//        public void update(int newFCost){
//            fCost = Math.min(fCost,newFCost);
//        }
//        public Collection<INode> getNodes(){
//            return nodes;
//        }
//
//    }



    @Override
    public OutputSchedule findPath() {
        Hashtable<MBSchedule, Hashtable<INode, Integer>> master = new Hashtable<MBSchedule, Hashtable<INode, Integer>>();
        Hashtable<INode, Integer> parentsLeft = this.getRootTable();
        int totalComputeTime = getTotalComputeTime();
        //Hashtable<MBSchedule, ForgottenSchedule> forgotten =  new Hashtable<MBSchedule, ForgottenSchedule>();

        // get all the no degree node, and create a schedule
        for (INode task: parentsLeft.keySet()){
            if (parentsLeft.get(task) == 0 ){
                int remainingComputeTimeAfterTask = totalComputeTime - task.getValue();
                MBSchedule newSchedule = new MBSchedule(
                        null ,
                        remainingComputeTimeAfterTask,
                        0,
                        task,
                        0);
                newSchedule.setHScore(h(newSchedule));

                //TODO remove
                System.out.println(newSchedule);

                master.put(newSchedule,getChildTable(parentsLeft,task));
                _pq.add(newSchedule);
            }
        }
        System.out.print("\n=== WHILE LOOP ===");
        MBSchedule cSchedule;
        while (true){
            System.out.printf("\nPQ SIZE :  %d", _pq.size());
            // pull current node
            cSchedule = _pq.pollMin();
            parentsLeft = master.get(cSchedule);
            master.remove(cSchedule);
            //System.out.println(" --------Pulled scheduled = " + cSchedule);

            // if all task is scheduled
            if (cSchedule.size == numNode){
                break;
            }

            Collection<INode> nodes;
//            if (forgotten.containsKey(cSchedule)){
//                nodes = forgotten.get(cSchedule).getNodes();
//            } else {
//                nodes = parentsLeft.keySet();
//            }

            nodes = parentsLeft.keySet();

            for (INode node: nodes){
                if (parentsLeft.get(node) == 0 ){
                    for (int numProcessor=0; numProcessor < TOTAL_NUM_PROCESSOR; numProcessor++){
                        int earliestStartTime = calculateEarliestStartTime(cSchedule, numProcessor, node);
                        Hashtable<INode, Integer> parentsLeftAfterSchedule = getChildTable(parentsLeft,node);

                        MBSchedule newSchedule = cSchedule.createSubSchedule(numProcessor, node, earliestStartTime);
                        newSchedule.setHScore(h(newSchedule));

                        //TODO remove
                        //System.out.println("\n" + newSchedule);


                        master.put(newSchedule,parentsLeftAfterSchedule);
                        _pq.add(newSchedule);

                    }
                }
            }

//            while (_pq.size() > MAX_NUMBER_NODE){
//                ForgottenSchedule forgottenSchedule;
//                MBSchedule badSchedule = _pq.pollMax();
//                MBSchedule badScheduleParent = badSchedule.parent;
//
//                // if badNode parent already forget some other node
//                if (forgotten.containsKey(badScheduleParent)){
//                    forgottenSchedule = forgotten.get(badScheduleParent);
//
//                } else {
//                    forgottenSchedule = new ForgottenSchedule(badSchedule.fScore);
//                    forgotten.put(badScheduleParent,forgottenSchedule);
//                }
//                forgottenSchedule.addNode(badSchedule.node);
//            }
        }

        System.out.print("\n === THE FINAL ANSWER ===");
        Helper.printPath(cSchedule);
        return new Solution(cSchedule,TOTAL_NUM_PROCESSOR);
    }


    /**
     * Heuristic function
     * @return
     */
    public int h(MBSchedule schedule){
//        int emptyGaps = schedule.getOverallFinishTime() - schedule.getEarliestFinishTimeOfAllProcessorsProcessors();
//        int perfectScheduling = schedule.getRemainingComputeTime() / TOTAL_NUM_PROCESSOR;
//        return Math.max(schedule.getOverallFinishTime(), perfectScheduling - emptyGaps);

        return schedule.finishTime + _criticalPathWeightTable.get(schedule.node);
    }


    /**
     * Calculate the earliest start time with given schedule and processor id
     * @param parentSchedule
     * @param processorId
     * @param nodeToBeSchedule
     * @return
     */
    public int calculateEarliestStartTime(MBSchedule parentSchedule, int processorId, INode nodeToBeSchedule) {
        MBSchedule last_processorId_use = null; //last time processor with "processorId" was used.
        MBSchedule cParentSchedule = parentSchedule;

        //---------------------------------------- Getting start time
        // finding the first schedule that has same id
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

                // if edge points to  === childNode
                if (edge.getChild() == nodeToBeSchedule && cParentSchedule.p_id != processorId){
                    //last_parent_processor[ cParentSchedule.p_id ] = true;
                    try {
                        int communicationWeight = _graph.getEdgeWeight(cParentSchedule.node,nodeToBeSchedule);
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
     * Some method that no one understand
     * @return
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
     * Some other method that no one understand
     * @param parentTable
     * @param x
     * @return
     */
    public Hashtable<INode, Integer> getChildTable(Hashtable<INode, Integer> parentTable, INode x){
        Hashtable<INode, Integer> tmp = new Hashtable<INode, Integer>(parentTable);
        tmp.remove(x);
        for (IEdge i : _graph.getOutgoingEdges(x.getName())){
            tmp.put( i.getChild(),  tmp.get(i.getChild()) - 1 );
        }
        return tmp;
    }


}