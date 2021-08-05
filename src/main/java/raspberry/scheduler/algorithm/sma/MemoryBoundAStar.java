package raspberry.scheduler.algorithm.sma;

import java.lang.Math;
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
    private final int MAX_NUMBER_NODE = 8;
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
        return _graph.getAllNodes().stream().mapToInt(INode::getValue).sum();
    }


    /**
     *
     * @return OutputSchedule
     */
    @Override
    public OutputSchedule findPath() {
        int totalComputeTime = getTotalComputeTime();
        Hashtable<INode, Integer> parentsLeft;
        MBSchedule cSchedule;

        MBSchedule head = new MBSchedule();


        // Initial set up for starting point
        for (INode task: _graph.getNodesWithNoInDegree()){
            int remainingComputeTimeAfterTask = totalComputeTime - task.getValue();
            ScheduledTask scheduledTask = new ScheduledTask(0,task,0);
            MBSchedule newSchedule = new MBSchedule(null , remainingComputeTimeAfterTask, scheduledTask);
            newSchedule.setHScore(h(newSchedule));
            newSchedule.setParentsLeftOfSchedulableTask(
                    newSchedule.parentsLeftsWithoutTask(task,_graph));
            _pq.add(newSchedule);
        }


        //
        while (true){
            System.out.printf("\nPQ SIZE :  %d", _pq.size());
            cSchedule = _pq.pollMin();
            // if all task is scheduled
            if (cSchedule.size == numNode){
                break;
            }

            parentsLeft = cSchedule.getParentsLeftOfSchedulableTask();
            Collection<INode> nodes;
            if (cSchedule.getForgottenTable() != null){
                cSchedule.getForgottenTable().keySet().forEach(forgottenSchedule ->
                        _pq.add(forgottenSchedule));
                cSchedule.setForgottenTableToNull();
            } else {
                nodes = parentsLeft.keySet();
                for (INode node: nodes){
                    if (parentsLeft.get(node) == 0 ){
                        for (int numProcessor=0; numProcessor < TOTAL_NUM_PROCESSOR; numProcessor++){
                            int earliestStartTime = calculateEarliestStartTime(cSchedule, numProcessor, node);
                            ScheduledTask scheduledTask = new ScheduledTask(numProcessor,node,earliestStartTime);
                            MBSchedule newSchedule = cSchedule.createSubSchedule(scheduledTask);
                            newSchedule.setHScore(h(newSchedule));
                            newSchedule.setParentsLeftOfSchedulableTask(
                                    newSchedule.parentsLeftsWithoutTask(node,_graph));
                            _pq.add(newSchedule);

                        }
                    }
                }
            }

            // Forget Routine
            while (_pq.size() > MAX_NUMBER_NODE){
                MBSchedule badSchedule = _pq.pollMax();
                System.out.println(badSchedule);
                if (badSchedule.parent != null){
                    MBSchedule badScheduleParent = badSchedule.parent;
                    badScheduleParent.forget(badSchedule);
                    // if parent is not in the queue
                    if (!_pq.contains(badScheduleParent)) {
                        _pq.add(badScheduleParent);
                    }
                }
            }
        }
        System.out.print("\n === THE FINAL ANSWER ===");
        Helper.printPath(cSchedule);
        return new Solution(cSchedule,TOTAL_NUM_PROCESSOR);
    }


    /**
     * Heuristic function
     * @param schedule schedule that contained scheduled task T
     * @return hScore the estimate cost of the current schedule to finish all non scheduled task
     */
    public int h(MBSchedule schedule){
        ScheduledTask scheduledTask = schedule.getScheduledTask();
        return scheduledTask.getFinishTime() + _criticalPathWeightTable.get(scheduledTask.getTask());
    }


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
    public int calculateEarliestStartTime(MBSchedule parentSchedule, int processorId, INode nodeToBeSchedule) {
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




}