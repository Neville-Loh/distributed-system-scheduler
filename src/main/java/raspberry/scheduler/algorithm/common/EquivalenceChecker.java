package raspberry.scheduler.algorithm.common;

import raspberry.scheduler.algorithm.astar.Astar;
import raspberry.scheduler.algorithm.astar.ScheduleAStar;
import raspberry.scheduler.graph.*;
import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;

import java.util.*;


/**
 * Checker for duplicate states in the given schedule
 * @author Neville, Young, Takahiro
 *
 */
public class EquivalenceChecker {
    private IGraph _graph;
    private int _numProcessors;
    private int _counter = 0;
    private final boolean VERBOSE = false;

    /**
     *
     * @param graph
     * @param numProcessors number of processors in this scheduling
     */
    public EquivalenceChecker(IGraph graph, int numProcessors) {
        _graph = graph;
        _numProcessors = numProcessors;
    }


    /**
     * Check if the schedule already exists in the pq,
     * this is done by swapping up task, if after swapping, the result is a better schedule, this schedule is marked as
     * duplicate, else if after swapping, no better schedule is created. THe copy is kept (by returning false, it's not
     * a duplicate.)
     * @param schedule schedule to be checked
     * @return true if it's a duplicate, false if otherwise
     */
    public boolean checkDuplicateBySwap(ScheduleAStar schedule) {
        // declaration
        ScheduleAStar cSchedule = schedule;
        ScheduledTask m = schedule.getScheduledTask();
        int TMax = m.getFinishTime();

        ArrayList<ScheduledTask> processorTaskList = schedule.getAllTaskInProcessor(m.getProcessorID());
        // sort by accenting start time
        processorTaskList.sort(Comparator.comparingInt(ScheduledTask::getStartTime));
        Hashtable<Integer, ScheduledTask> indexTable = new Hashtable<Integer, ScheduledTask>();
        int i = 1;
        while (i < processorTaskList.size() + 1) {
            indexTable.put(i, processorTaskList.get(i - 1));
            i++;
        }
        int secondLastIndex = indexTable.size() - 1;
        i = secondLastIndex;
        while (i > 0
                && _graph.getIndex(m.getTask()) <
                _graph.getIndex(indexTable.get(i).getTask())) {
            print("================================================================================");
            ScheduleAStar before = cSchedule;

            print("NOT SWAPPED:  " + cSchedule);
            cSchedule = swap2(cSchedule, m, indexTable.get(i));
            print(String.format("SWAPPED task %s with %s:  ", m, indexTable.get(i)) + cSchedule.toString());

            if (!isCorrectlySwapped(before, cSchedule, m, indexTable.get(i))) {
                System.out.println("NOT SWAPPED:  " + before);
                System.out.println(String.format("SWAPPED task %s with %s:  ", m, indexTable.get(i)) + cSchedule.toString());
                System.out.println(("WRONG"));
                return false;
            }
            // task in the same processor from task to swap to send to last index
            ArrayList<ScheduledTask> inputList = new ArrayList<>();
            for (int index = i; index <= secondLastIndex; index ++){
                inputList.add(indexTable.get(index));
            }
            print("------------- OUT GOING COMS OK CHECK ");
            if (cSchedule.getScheduledTask(indexTable.get(secondLastIndex).getTask())
                    .getFinishTime() <= TMax
                    && outgoingCommsOK(inputList, cSchedule, schedule)) {
                _counter++;
                print("\n------------- END CHECK\n");
                print("================================================================================");
                print("counter is: " + _counter);
                return true;
            }
            print("\n------------- END CHECK\n");
            print("================================================================================");
            i--;
        }
        return false;
    }


    private ScheduleAStar swap2(ScheduleAStar schedule, ScheduledTask m, ScheduledTask taskToSwap) {
        ArrayList<ScheduledTask> tmp = new ArrayList<>();
        ScheduleAStar cSchedule = schedule;
        while (cSchedule != null) {
            tmp.add(cSchedule.getScheduledTask());
            cSchedule = cSchedule.getParent();
        }
        Collections.reverse(tmp);

        ScheduleAStar result;
        cSchedule = schedule;

        ArrayList<ScheduledTask> prevTask = new ArrayList<>();
        ArrayList<ScheduledTask> afterTask = new ArrayList<>();

        while (cSchedule != null){
            if ( cSchedule.getScheduledTask().getTask() == m.getTask()){
                break;
            }else{
                afterTask.add(cSchedule.getScheduledTask());
                cSchedule = cSchedule.getParent();
            }
        }

        while (cSchedule != null) {
            if (cSchedule.getScheduledTask().getTask().getName().equals(taskToSwap.getTask().getName())) {
                cSchedule = cSchedule.getParent();
                break;
            }
            if ( !(cSchedule.getScheduledTask().getTask().getName().equals(m.getTask().getName() ))
                    && !(cSchedule.getScheduledTask().getTask().getName().equals(taskToSwap.getTask().getName()))) {
                prevTask.add(cSchedule.getScheduledTask());
            }
            cSchedule = cSchedule.getParent();
        }

        INode swapNode = taskToSwap.getTask();
        List<IEdge> childOfSwap = _graph.getOutgoingEdges(swapNode);

        INode mNode = m.getTask();
        List<IEdge> parentOfM = _graph.getIngoingEdges(mNode);

        ArrayList<ScheduledTask> newOrdering = new ArrayList<ScheduledTask>();

        Collections.reverse(prevTask);

        boolean swapIsAdded = false;
        for (ScheduledTask st : prevTask) {
            if (!swapIsAdded) {
                for (IEdge e : childOfSwap) {
                    if (e.getChild() == st.getTask() && !swapIsAdded ) {
                        newOrdering.add(taskToSwap);
                        swapIsAdded = true;
                    }
                }
            }
            newOrdering.add(st);
        }
        if (!swapIsAdded) {
            newOrdering.add(taskToSwap);
        }


        ArrayList<ScheduledTask> newOrdering2 = new ArrayList<ScheduledTask>();
        Collections.reverse(newOrdering);
        boolean mIsAdded = false;
        for (ScheduledTask st : newOrdering) {
            if (!mIsAdded) {
                for (IEdge e : parentOfM) {
                    if ( e.getParent() == st.getTask() && !mIsAdded) {
                        newOrdering2.add(m);
                        mIsAdded = true;
                    }
                }
            }
            newOrdering2.add(st);
        }
        if (!mIsAdded) {
            newOrdering2.add(m);
        }
        Collections.reverse(newOrdering2);


        if (prevTask.isEmpty()){
            newOrdering2 = new ArrayList<ScheduledTask>();
            newOrdering2.add( m );
            newOrdering2.add( taskToSwap );
        }

        int indexM = 0;
        int indexSwap = 0;
        int counter = 0;
        for (ScheduledTask i:newOrdering2){
            if ( i == m ){
                indexM = counter;
            }else if (i == taskToSwap){
                indexSwap = counter;
            }
            counter ++;
        }

        Collections.reverse((newOrdering));
        if ( indexM > indexSwap ){
            System.out.printf("\nindex m: %d, swap: %d \n", indexM,indexSwap);
            System.out.println(tmp);
            System.out.println(prevTask);
            System.out.println(newOrdering);
            System.out.println(newOrdering2);
            System.out.println("NOT SWAPPED:  \n" + schedule);
        }
        result = cSchedule;
        for (ScheduledTask st : newOrdering2) {
            int earliestStartTime = Astar.calculateEarliestStartTime(result, st.getProcessorID(), st.getTask());
            ScheduledTask scheduleST = new ScheduledTask(st.getProcessorID(), st.getTask(), earliestStartTime);
            if (result == null) {
                result = new ScheduleAStar(
                        scheduleST, null
                );
            } else {
                result = createSubSchedule(result, scheduleST);
            }
        }

        Collections.reverse(afterTask);
        for (ScheduledTask st : afterTask) {
            int earliestStartTime = Astar.calculateEarliestStartTime(result, st.getProcessorID(), st.getTask());
            ScheduledTask scheduleST = new ScheduledTask(st.getProcessorID(), st.getTask(), earliestStartTime);
            if (result == null) {
                result = new ScheduleAStar(
                        scheduleST, null
                );
            } else {
                result = createSubSchedule(result, scheduleST);
            }
        }



        if (indexM > indexSwap){
            print(String.format("SWAPPED task %s with %s:  \n", m, taskToSwap) + schedule.toString());
        }
        if (result == null){
            print("NULL");
        }
        return result;
    }


    /**
     * Check if child of swapped task to see if they are delay or affected by the swap, if they are not
     * return ture.
     * @param scheduledTasks task
     * @param after schedule after the swap
     * @param before schedule before the swap
     * @return
     */
    public boolean outgoingCommsOK(List<ScheduledTask> scheduledTasks, ScheduleAStar after, ScheduleAStar before) {
        boolean flag = false;
        boolean flag2 = false;
        //1: for all nk ∈ {ni. . . nl−1} do
        for (ScheduledTask scheduledTask : scheduledTasks) {
            // 2: if ts(nk) > t_originStartTime (nk) then B check only if nk starts later
            int swappedTime = after.getScheduledTask(scheduledTask.getTask()).getStartTime();
            int originalTime = before.getScheduledTask(scheduledTask.getTask()).getStartTime();
            // if after swap, scheduled Task start later (delay) -> not eq
            if (swappedTime > originalTime) {
                flag = true;
                for (IEdge outEdge : _graph.getOutgoingEdges(scheduledTask.getTask())) {
                    INode childNode = outEdge.getChild();
                    // 4: T ← tf (nk) + c(ekc) B remote data arrival from nk
                    int T = scheduledTask.getFinishTime() + outEdge.getWeight();
                    // 5: if nc scheduled then
                    if (after.getScheduledTask(childNode) != null) {
                        ScheduledTask childScheduledTask = after.getScheduledTask(childNode);
                        print("Child task is " + childScheduledTask);
                        //6: if ts(nc) > T ∧ proc(nc) 6= P then B on same proc always OK
                        if (childScheduledTask.getStartTime() > T && childScheduledTask.getProcessorID() != scheduledTask.getProcessorID()) {
                            print("Child Task: = " + childScheduledTask.getName()
                                +" (Data arrive time T = " + T + " AND child start time = " + childScheduledTask.getStartTime() + ")");
                            print("NOT OK");
                            return false;
                        }
                    } else {
                        flag2 = true;
                        //9: for all Pi ∈ P/P do B nc can be on any proc; P always OK
                        for (int i = 1; i < _numProcessors + 1; i++) {
                            if (i == scheduledTask.getProcessorID()){
                                continue;
                            }
                            // 10: atLeastOneLater ← false
                            boolean atLeastOneLater = false;
                            print("Checking processor " + i +" ASS CHILD = " + childNode);
                            // 11: for all np ∈ parents(nc) − nk do
                            for (IEdge inEdge : _graph.getIngoingEdges(childNode)) {
                                INode parentNode = inEdge.getParent();
                                print("Parent is = " + parentNode);

                                if (after.getScheduledTask(parentNode) != null && parentNode!= scheduledTask.getTask()) {
                                    ScheduledTask parentScheduledTask = after.getScheduledTask(parentNode);
                                    int dataArrivalTime;
                                    if (parentScheduledTask.getProcessorID() == i) {
                                        dataArrivalTime = parentScheduledTask.getFinishTime();
                                    } else {
                                        dataArrivalTime = parentScheduledTask.getFinishTime() + inEdge.getWeight();
                                    }

                                    if (parentScheduledTask != scheduledTask && dataArrivalTime >= T) {
                                        atLeastOneLater = true;
                                    }
                                // if parent of the child is not schedule, return that they are delay
                                } else {
                                    return false;
                                }

                            }
                            // 14: if atLeastOneLater = false then
                            if (!atLeastOneLater) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        if (flag) {
            print("FLAG");
            for (ScheduledTask scheduledTask : scheduledTasks) {
                if (VERBOSE) System.out.printf("scheduledTask: %s   stIndex: %d   ,",scheduledTask.getName() , _graph.getIndex(scheduledTask.getTask()));
                for (IEdge outEdge : _graph.getOutgoingEdges(scheduledTask.getTask())) {
                    INode childNode = outEdge.getChild();
                    if (VERBOSE) System.out.printf(" Child Task:%s  childIndex:%d ", childNode.getName(),   _graph.getIndex(childNode));
                }
                print("");
            }
        }
        if (flag2){
            print("ASS");
        }
        print("\nOK schedule is deleted, :" + scheduledTasks);
        return true;
    }


    /**
     * Create a sub schedule without in degree table
     * @param schedule schedule
     * @param scheduledTask a schedule task to be in the next schedule
     * @return a new sub schedule with the task
     */
    private ScheduleAStar createSubSchedule(ScheduleAStar schedule, ScheduledTask scheduledTask) {
        return new ScheduleAStar(
                schedule,
                scheduledTask,
                null);
    }

    /**
     * Check method
     * Check if the swap has been correctly executed, the checker for different scenario
     * - if the both task start with the exact start time
     * - if there are missing task that are not scheduled
     * - if the task is not a valid schedule.
     * @param before the schedule before swap
     * @param after the schedule after swap
     * @param m task to be swap up
     * @param taskToSwap task to be swap down
     * @return true if the swap has been correct executed
     */
    public boolean isCorrectlySwapped(ScheduleAStar before, ScheduleAStar after,
                                      ScheduledTask m, ScheduledTask taskToSwap){

        if (before.getSize() != after.getSize()){
            System.out.println("INCORRECT - Size is different");
            return false;
        }

        int mSwappedTime = after.getScheduledTask(m.getTask()).getStartTime();
        int tSwappedTime = after.getScheduledTask(taskToSwap.getTask()).getStartTime();
        int mOriginalTime = before.getScheduledTask(m.getTask()).getStartTime();
        int tOriginalTime = before.getScheduledTask(taskToSwap.getTask()).getStartTime();

        // check if both task start with the exact start time
        if ((mSwappedTime == mOriginalTime) && tSwappedTime ==tOriginalTime){
            System.out.println("INCORRECT - M AND SwappedTask HAVE THE SAME START TIME");
            return false;
        }
        // if m is later than task to be swapped,
        if (mSwappedTime > tSwappedTime){
            System.out.println("INCORRECT - m is scheduled later than (Task to be Swap)");
            return false;
        }

        ArrayList<ScheduledTask> beforeST = before.getAllTaskInProcessor(m.getProcessorID());
        beforeST.sort(Comparator.comparingInt(ScheduledTask::getStartTime));
        ArrayList<ScheduledTask> afterST = after.getAllTaskInProcessor(m.getProcessorID());
        afterST.sort(Comparator.comparingInt(ScheduledTask::getStartTime));

        // check for missing task and missed order, that is something other than task to be swapped and m are swapped
        for (int i = 0; i < beforeST.size(); i++){
            INode beforeNode = beforeST.get(i).getTask();
            INode afterNode = afterST.get(i).getTask();
            if (beforeNode != m.getTask() && beforeNode != taskToSwap.getTask()){
                if (beforeNode != afterNode){
                    System.out.println("INCORRECT - the order is not the same");
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Hepper print method for debug
     * @param s string to be print
     */
    private void print(String s){
        if (VERBOSE) System.out.println(s);
    }
}
