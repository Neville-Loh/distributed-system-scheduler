package raspberry.scheduler.algorithm.sma;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import raspberry.scheduler.algorithm.Algorithm;
import raspberry.scheduler.algorithm.OutputSchedule;
import raspberry.scheduler.algorithm.Schedule;
import raspberry.scheduler.algorithm.Solution;
import raspberry.scheduler.algorithm.util.Helper;
import raspberry.scheduler.graph.*;
import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;
import raspberry.scheduler.algorithm.common.ScheduledTask;


/**
 * Rough implementation of famous algorithm SMA
 * @author Neville L.
 */
public class MemoryBoundAStar implements Algorithm {
    private static IGraph _graph;
    private TwoWayPriorityQueue _pq;
    private final int TOTAL_NUM_PROCESSOR;
    private int numNode;
    private final int MAX_NUMBER_NODE;
    private Hashtable<INode, Integer> _criticalPathWeightTable;
    private boolean VERBOSE = false;


    // New stuff
    Hashtable<Integer, ArrayList<MBSchedule>> _visited;
    private Hashtable<String, Integer> _heuristic;
    private int _maxCriticalPath;
    
    /**
     * Class constructor
     * @param taskDependencyGraph dependency digraph of the task
     * @param totalProcessorNumber the total number of processor available for schedule
     */
    public MemoryBoundAStar(IGraph taskDependencyGraph, int totalProcessorNumber, int memoryLimit){
        _graph = taskDependencyGraph;
        _pq = new TwoWayPriorityQueue();
        TOTAL_NUM_PROCESSOR = totalProcessorNumber;
        numNode = _graph.getNumNodes();
        _criticalPathWeightTable = _graph.getCriticalPathWeightTable();
        MAX_NUMBER_NODE = memoryLimit;

    }

    /**
     * Get the total compute time require to compute all tasks
     * in the dependency graph when given one resource
     * @return computeTime
     */
    public int getTotalComputeTime(){
        return _graph.getAllNodes().stream().mapToInt(INode::getValue).sum();
    }


    /**
     * Find the optimal schedule with the given dependency graph when initiate
     * @return OutputSchedule
     */
    @Override
    public OutputSchedule findPath() {

        //New stuff
        _heuristic = new Hashtable<String, Integer>();
        getH();

        int totalComputeTime = getTotalComputeTime();
        Hashtable<INode, Integer> parentsLeft;
        MBSchedule cSchedule;

        /*
         * Add all task that can be scheduled to the priority queue as starting point
         * The initial placement for the task is processor 0, since there are no difference
         * between processor.
         */
        //MBSchedule start = new MBSchedule();
        for (INode task: _graph.getNodesWithNoInDegree()){
            int remainingComputeTimeAfterTask = totalComputeTime - task.getValue();
            ScheduledTask scheduledTask = new ScheduledTask(0,task,0);
            MBSchedule newSchedule = new MBSchedule(null , remainingComputeTimeAfterTask, scheduledTask);


            newSchedule.setParentsLeftOfSchedulableTask(
                    newSchedule.parentsLeftsWithoutTask(task,_graph));

            ///
            // newSchedule.setHScore(h(newSchedule));
            newSchedule.setHScore( Math.max(
                h(newSchedule), h1( newSchedule.getParentsLeftOfSchedulableTask(), newSchedule )));
            ///
            
            _pq.add(newSchedule);
        }


        /*
         * While loop
         */
        int iterCount = 0;
        while (true){
            if (VERBOSE) System.out.printf("\nPQ SIZE :  %d\n", _pq.size());

            if (VERBOSE) System.out.println("\n====================");
            if (VERBOSE) System.out.println("Start: "+ iterCount);
            if (VERBOSE) System.out.println(_pq);


            cSchedule = _pq.pollMin();

            // New stuff ==>
            // ArrayList<MBSchedule> listVisitedForSize = _visited.get(cSchedule.getHash());
            // if (listVisitedForSize != null && isIrrelevantDuplicate(listVisitedForSize, cSchedule)) {
            //     continue;
            // } else {
            //     if (listVisitedForSize == null) {
            //         listVisitedForSize = new ArrayList<MBSchedule>();
            //         _visited.put(cSchedule.getHash(), listVisitedForSize);
            //     }
            //     listVisitedForSize.add(cSchedule);
            // }
            // <==-================================


            if (VERBOSE) System.out.println("BEST = "+ cSchedule);

            // if all task is scheduled
            if (cSchedule.getSize() == numNode){
                break;
            }



            if (cSchedule.getForgottenTable() != null && cSchedule.getForgottenTable().size() > 0){
                /*
                 * Remember Routine
                 * Remember about the low f score schedule
                 */
                Hashtable<ScheduledTask, Integer> forgottenSchedule = cSchedule.getForgottenTable();
                for (ScheduledTask scheduledTask: forgottenSchedule.keySet()){
                    MBSchedule subSchedule = cSchedule.createSubSchedule(scheduledTask, _graph);
                    subSchedule.setFScore(forgottenSchedule.get(scheduledTask));
                    if(!_pq.contains(subSchedule)){
                        _pq.add(subSchedule);
                    }
                }
                cSchedule.setForgottenTableToNull();

            } else {
                parentsLeft = cSchedule.getParentsLeftOfSchedulableTask();
                Collection<INode> taskList = parentsLeft.keySet();
                for (INode task: taskList){
                    if (parentsLeft.get(task) == 0 ){
                        for (int numProcessor=0; numProcessor < TOTAL_NUM_PROCESSOR; numProcessor++){
                            int earliestStartTime = calculateEarliestStartTime(cSchedule, numProcessor, task);
                            MBSchedule subSchedule = cSchedule.createSubSchedule(
                                    new ScheduledTask(numProcessor,task,earliestStartTime), _graph);

                            ///
                            // subSchedule.setHScore(h(subSchedule));
                            subSchedule.setHScore( Math.max(
                                h(subSchedule), h1( subSchedule.getParentsLeftOfSchedulableTask(), subSchedule )));

                                
                            // check if subSubchedule is equiv or high than upperBound
                            if (!isHigherThanBound() && !isVisited()){
                                _pq.add(subSchedule);
                            }


                        }
                    }
                }
            }


//            if (VERBOSE) System.out.println("After Exploring: " + iterCount);
//            if (VERBOSE) System.out.println(_pq);
//
//            /*
//             * Forget Routine
//             * Forget about the low f score schedule
//             */
//            ArrayList<MBSchedule> startingPoint = new ArrayList<MBSchedule>();
//            while (_pq.size() > (MAX_NUMBER_NODE - startingPoint.size())){
//                if (VERBOSE) System.out.println("----------------------");
//                if (VERBOSE) System.out.println(_pq);
//                MBSchedule badSchedule = _pq.pollMax();
//
//
//                if (VERBOSE) System.out.println("bad schedule = " +badSchedule);
//                if (VERBOSE) System.out.println("Starting point = " + startingPoint);
//                if (badSchedule.parent != null){
//                    MBSchedule badScheduleParent = badSchedule.parent;
//                    badScheduleParent.forget(badSchedule);
//                    // if parent is not in the queue
//                    if (VERBOSE) System.out.println("Forget -----" + badScheduleParent+ " Forgetting " + badSchedule);
//                    if (_pq.contains(badScheduleParent)){
//                        _pq.remove(badScheduleParent);
//                        _pq.add(badScheduleParent);
//
//                    } else if (!startingPoint.contains(badScheduleParent)) {
//                        if (VERBOSE) System.out.println("Adding " + badScheduleParent + " back to queue");
//                        _pq.add(badScheduleParent);
//                    }
//                } else {
//                    if (VERBOSE) System.out.println("Adding badSchedule: " + badSchedule +" to starting point");
//                    startingPoint.add(badSchedule);
//                }
//                if (VERBOSE) System.out.println("----------------------");
//            }
//            _pq.addAll(startingPoint);
//            iterCount++;
        }

        System.out.print("\n === THE FINAL ANSWER ===");
        Helper.printPath(cSchedule);
        return new Solution(cSchedule,TOTAL_NUM_PROCESSOR);
    }


    /**
     * Heuristic function
     * calculate the critical path and estimate the cost
     * @param schedule schedule that contained scheduled task T
     * @return hScore the estimate cost of the current schedule to finish all non scheduled task
     */
    // public int h(MBSchedule schedule){
    //     ScheduledTask scheduledTask = schedule.getScheduledTask();
    //     return scheduledTask.getFinishTime() + _criticalPathWeightTable.get(scheduledTask.getTask());
    // }


    /**
     * Manhattan Distance Heuristic, require storing the earliest start time
     * @param schedule schedule that contained scheduled task T
     * @return hScore the estimate cost of the current schedule to finish all non scheduled task
     */
    public int manhattanDistanceHeuristic(MBSchedule schedule){
        int emptyGaps = schedule.getOverallFinishTime() - schedule.getEarliestFinishTimeOfAllProcessorsProcessors();
        int perfectScheduling = schedule.getRemainingComputeTime() / TOTAL_NUM_PROCESSOR;
        return Math.max(schedule.getOverallFinishTime(), perfectScheduling - emptyGaps);
    }


    /**
     * Calculate the earliest start time with given schedule and processor id
     * @param parentSchedule ??
     * @param processorId ID of processor that the task
     * @param nodeToBeSchedule The task schedule
     * @return start time the earliest start time that a task begin in the given processor id
     */
    public static int calculateEarliestStartTime(MBSchedule parentSchedule, int processorId, INode nodeToBeSchedule) {
        MBSchedule last_processorId_use = null; //last time processor with "processorId" was used.
        MBSchedule cParentSchedule = parentSchedule;
        //---------------------------------------- Getting start time
        // finding the first schedule that has same id
        while ( cParentSchedule != null){
            if ( cParentSchedule.getScheduledTask().getProcessorID() == processorId ){
                last_processorId_use = cParentSchedule;
                break;
            }
            cParentSchedule = cParentSchedule.parent;
        }
        //last time parent was used. Needs to check for all processor.
        int finished_time_of_last_parent=0;
        if (last_processorId_use != null){
            finished_time_of_last_parent = last_processorId_use.getScheduledTask().getFinishTime();
        }
        cParentSchedule = parentSchedule;
        while ( cParentSchedule != null){
            // for edges in current parent scheduled node
            INode last_scheduled_node = cParentSchedule.getScheduledTask().getTask();

            for ( IEdge edge: _graph.getOutgoingEdges(last_scheduled_node.getName())){

                // if edge points to  === childNode
                if (edge.getChild() == nodeToBeSchedule && cParentSchedule.getScheduledTask().getProcessorID() != processorId){
                    //last_parent_processor[ cParentSchedule.p_id ] = true;
                    try {
                        int communicationWeight = _graph.getEdgeWeight(cParentSchedule.getScheduledTask().getTask(),nodeToBeSchedule);
                        if (finished_time_of_last_parent < (cParentSchedule.getScheduledTask().getFinishTime() + communicationWeight)){
                            finished_time_of_last_parent = cParentSchedule.getScheduledTask().getFinishTime() + communicationWeight;
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

    // --------------------------------------------------------------------------------------------------

    public boolean isHigherThanBound(){
        return false;
    }
    public boolean isVisited(){
        return false;
    }

    
     /**
     * For each task that was scheduled last in the processor.
     * -> find the largest cost
     * --> where cost = finish time of the task + heuristic of the task
     *
     * @param cSchedule : schedule of which we are trying to find heuristic cost for.
     * @return integer : represeting the heuristic cost
     */
    public int h(MBSchedule cSchedule) {
        int max = 0;
        for (String s : cSchedule.getLastForEachProcessor().values()) {
            int tmp = _heuristic.get(s) + cSchedule.getScheduling().get(s).get(1) +
                    _graph.getNode(s).getValue();
            if (tmp > max) {
                max = tmp;
            }
        }
        return max - cSchedule.getScheduledTask().getFinishTime();
    }

    /**
     * Find the best case scheduling where all task are evenly spread out throughout the different processors.
     *
     * @param x         : Hashtable representing the outDegree table. (All the tasks in the table has not been scheduled yet)
     * @param cSchedule : current schedule . Used to find the last task which was scheduled for each processor.
     * @return Integer : Representing the best case scheduling.
     */
    public int h1(Hashtable<INode, Integer> x, MBSchedule cSchedule) {
        int sum = 0;
        for (String s : cSchedule.getLastForEachProcessor().values()) {
            sum += cSchedule.getScheduling().get(s).get(1) +
                    _graph.getNode(s).getValue();
        }
        for (INode i : x.keySet()) {
            sum += i.getValue();
        }
        return sum / TOTAL_NUM_PROCESSOR - cSchedule.getScheduledTask().getFinishTime();
    }


    /**
     * Creates a maximum dependency path table.
     * Also find the maximum critical path cost of the graph.
     * where key : String <- task's name.
     * value : int <- maximum path cost.
     */
    public void getH() {
        _heuristic = new Hashtable<String, Integer>();
        for (INode i : _graph.getAllNodes()) {
            int hVal = getHRecursive(i);
            _heuristic.put(i.getName(), hVal);
            _maxCriticalPath = Math.max( hVal , _maxCriticalPath );
        }
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
    public Boolean isIrrelevantDuplicate(ArrayList<MBSchedule> scheduleList, MBSchedule cSchedule) {
        for (MBSchedule s : scheduleList) {
            if ( s.equals3(cSchedule) ){
                if ( s.getOverallFinishTime() > cSchedule.getOverallFinishTime()) {
//                    System.out.printf("%d -> %d\n", s.getTotal(), cSchedule.getTotal());
                    return false;
                }else{
                    return true;
                }
            }
        }
        return false;
    }

    // --------------------------------------------------------------------------------------------------

}