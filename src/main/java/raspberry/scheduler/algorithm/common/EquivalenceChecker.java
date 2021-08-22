package raspberry.scheduler.algorithm.common;

import raspberry.scheduler.algorithm.astar.Astar;
import raspberry.scheduler.algorithm.astar.ScheduleAStar;
import raspberry.scheduler.algorithm.util.OutputChecker;
import raspberry.scheduler.graph.*;
import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;

import java.util.*;


public class EquivalenceChecker {


    private IGraph _graph;
    private int _numProcessors;
    private int _counter = 0;

    public EquivalenceChecker(IGraph graph, int numProcessors) {
        _graph = graph;
        _numProcessors = numProcessors;
    }


    public boolean weAreDoomed(ScheduleAStar schedule) {

        // declaration
        ScheduleAStar cSchedule = schedule;
        ScheduledTask m = schedule.getScheduledTask();
        int TMax = m.getFinishTime();


        ArrayList<ScheduledTask> processorTaskList = schedule.getAllTaskInProcessor(m.getProcessorID());
        // sort by accenting start time
        processorTaskList.sort(Comparator.comparingInt(ScheduledTask::getOriginalStartTime));

        Hashtable<Integer, ScheduledTask> indexTable = new Hashtable<Integer, ScheduledTask>();
        int i = 1;
        while (i < processorTaskList.size() + 1) {
            indexTable.put(i, processorTaskList.get(i - 1));
            i++;
        }
        int secondlastindex = indexTable.size() - 1;
        i = secondlastindex;
        System.out.println(indexTable.toString());
        while (i > 0
                && _graph.getIndex(m.getTask()) <
                _graph.getIndex(indexTable.get(i).getTask())) {

            System.out.println("NOT SWAPPED:  " + cSchedule);
            cSchedule = swap2(cSchedule, m, indexTable.get(i));
            System.out.println(String.format("SWAPPED task %s with %s:  ", m, indexTable.get(i)) + cSchedule.toString());
            Object temp = cSchedule.getScheduledTask(indexTable
                            .get(secondlastindex)
                            .getTask());
            System.out.println("TEMP is not null: " + temp);
            if (cSchedule.getScheduledTask(indexTable
                            .get(secondlastindex)
                            .getTask())
                    .getFinishTime() <= TMax
                    && outgoingCommsOK(processorTaskList, cSchedule)) {
                _counter++;
                System.out.println("counter is: " + _counter);
                try {
                    if (!OutputChecker.isValid(_graph, new Solution(cSchedule, 10))) {
                        System.out.println("We are Screwed!!!!!!!!!!!!!!!!!!!!!!!!!");
                    }
                } catch (EdgeDoesNotExistException e) {
                    e.printStackTrace();
                }
                return true;
            }
            i--;

        }
        return false;


//        i
//        3: i ← l − 1
//        4: while i ≥ 0 ∧ index(m) < index(ni) do


//        5: Swap position of m and ni
//        6: Schedule m and ni      nl−1 each as early as possible
//        7: if tf (nl−1) ≤ tmax ∧ OutgoingCommsOK(ni. . . nl−1) then
//        8: return EQUIVALENT
//        9: i ← i − 1
//        10: return NOT EQUIVALENT

    }

    //count all the prefect substring?
    //
    private ScheduleAStar swap(ScheduleAStar schedule, ScheduledTask m, ScheduledTask taskToSwap) {

        ScheduleAStar result;
        ScheduleAStar cSchedule = schedule.getParent();
        ArrayList<ScheduledTask> prevTask = new ArrayList<>();

        while (cSchedule != null) {
            if (cSchedule.getScheduledTask().equals(taskToSwap)) {
                cSchedule = cSchedule.getParent();
                break;
            }
            prevTask.add(cSchedule.getScheduledTask());
            cSchedule = cSchedule.getParent();
        }

        // calculating m new time
        int mProcessorID = m.getProcessorID();
        int earliestStartTime = Astar.calculateEarliestStartTime(cSchedule, mProcessorID, m.getTask());

        // create new scheduled Task for m in at a new start Time
        ScheduledTask scheduleM = new ScheduledTask(mProcessorID, m.getTask(), m.getOriginalStartTime());
        scheduleM.setStartTime(earliestStartTime);

//        result = cSchedule.getParent().createSubSchedule(scheduleM, _graph);
        if (cSchedule != null) {
            result = createSubSchedule(cSchedule, scheduleM);
        } else {
            Hashtable<INode, Integer> rootTable = this.getRootTable();
            result = new ScheduleAStar(
                    scheduleM,
                    getChildTable(rootTable, scheduleM.getTask())
            );
        }

        Collections.reverse(prevTask);
        for (ScheduledTask st : prevTask) {
            if (st.getProcessorID() == mProcessorID) {
                // calculate start time
                earliestStartTime = Astar.calculateEarliestStartTime(result, mProcessorID, st.getTask());
                ScheduledTask scheduleST = new ScheduledTask(st.getProcessorID(), st.getTask(), st.getOriginalStartTime());
                scheduleST.setStartTime(earliestStartTime);
//                result = result.createSubSchedule(scheduleST,_graph);
                result = createSubSchedule(result, scheduleST);
            } else {
                // just schedule
//                result = result.createSubSchedule(st, _graph);
                result = createSubSchedule(result, st);
            }
        }

        // scheduling
        earliestStartTime = Astar.calculateEarliestStartTime(result, mProcessorID, taskToSwap.getTask());
        ScheduledTask scheduleST = new ScheduledTask(taskToSwap.getProcessorID(), taskToSwap.getTask(), taskToSwap.getOriginalStartTime());
        scheduleST.setStartTime(earliestStartTime);
//        result = result.createSubSchedule(scheduleST,_graph);
        result = createSubSchedule(result, scheduleST);


        return result;
//        return null;
    }


    private ScheduleAStar swap2(ScheduleAStar schedule, ScheduledTask m, ScheduledTask taskToSwap) {
        ScheduleAStar result;
        ScheduleAStar cSchedule = schedule;

        ArrayList<ScheduledTask> prevTask = new ArrayList<>();

        while (cSchedule != null) {
            if (cSchedule.getScheduledTask().equals(taskToSwap)) {
                cSchedule = cSchedule.getParent();
                break;
            }
            if (cSchedule.getScheduledTask() != m ){
                prevTask.add(cSchedule.getScheduledTask());
            }
            cSchedule = cSchedule.getParent();
        }
        // --------------------------------------------------------
        INode swapNode = taskToSwap.getTask();
        List<IEdge> childOfSwap = _graph.getOutgoingEdges(swapNode);

        INode mNode = m.getTask();
        List<IEdge> parentOfM = _graph.getIngoingEdges(mNode);

        ArrayList<ScheduledTask> newOrdering = new ArrayList<ScheduledTask>();

        boolean mIsAdded = false;
        for (ScheduledTask st : prevTask) {
            if (!mIsAdded) {
                for (IEdge e : parentOfM) {
                    if (e.getParent() == st.getTask()) {
                        newOrdering.add(m);
                        mIsAdded = true;
                    }
                }
            }
            newOrdering.add(st);
        }
        if (!mIsAdded) {
            newOrdering.add(m);
        }

        ArrayList<ScheduledTask> newOrdering2 = new ArrayList<ScheduledTask>();

        Collections.reverse(newOrdering);
        boolean swapIsAdded = false;
        for (ScheduledTask st : newOrdering) {
            if (!swapIsAdded) {
                for (IEdge e : childOfSwap) {
                    if (e.getChild() == st.getTask()) {
                        newOrdering2.add(taskToSwap);
                        swapIsAdded = true;
                    }
                }
            }
            newOrdering2.add(st);
        }
        if (!swapIsAdded) {
            newOrdering2.add(taskToSwap);
        }
        result = cSchedule;
        System.out.printf("ORDERING2 : %d , ORDERING1 : %d , prevTask: %d\n",newOrdering2.size(),newOrdering.size(),prevTask.size());
        for (ScheduledTask st : newOrdering2) {
            int earliestStartTime = Astar.calculateEarliestStartTime(result, st.getProcessorID(), st.getTask());
            ScheduledTask scheduleST = new ScheduledTask(st.getProcessorID(), st.getTask(), st.getOriginalStartTime());
            scheduleST.setStartTime(earliestStartTime);

            if (result == null){
                result = new ScheduleAStar(
                     scheduleST,null
                );

            } else {
                result = createSubSchedule(result, scheduleST);
            }
        }

        return result;
    }



    public boolean outgoingCommsOK(List<ScheduledTask> scheduledTasks, ScheduleAStar schedule) {
//    Algorithm 3 Subroutine OutgoingCommsOK(ni. . . nl−1)
//1: for all nk ∈ {ni. . . nl−1} do
//            2: if ts(nk) > torigs (nk) then B check only if nk starts later
        for (ScheduledTask scheduledTask : scheduledTasks) {
            if (schedule.getScheduledTask(scheduledTask.getTask()).getStartTime() > scheduledTask.getStartTime()) {
                //3: for all nc ∈ children(nk) do
                for (IEdge outEdge : _graph.getOutgoingEdges(scheduledTask.getTask())) {
                    INode childNode = outEdge.getChild();
                    System.out.println("\nchild scheduled task is :   " + childNode.toString());
//              4: T ← tf (nk) + c(ekc) B remote data arrival from nk
                    int T = scheduledTask.getFinishTime() + outEdge.getWeight();
//              5: if nc scheduled then
                    if (schedule.getScheduling().containsKey(childNode.getName())) {
                        ScheduledTask childScheduledTask = schedule.getScheduledTask(childNode);
//              6: if ts(nc) > T ∧ proc(nc) 6= P then B on same proc always OK
                        if (childScheduledTask.getStartTime() > T && childScheduledTask.getProcessorID() != scheduledTask.getProcessorID()) {
                            //              7: return false
                            return false;
                        }
                    }
                    //        8: else B nc not scheduled yet
                    else {
//                        System.out.println("\n\n\n\n\nELSE STATMENT IS REACHED \n\n\n\n\n");
                        //9: for all Pi ∈ P/P do B nc can be on any proc; P always OK
                        for (int i = 1; i < _numProcessors + 1 && i != scheduledTask.getProcessorID(); i++) {
                            //10: atLeastOneLater ← false
                            boolean atLeastOneLater = false;
                            //            11: for all np ∈ parents(nc) − nk do
                            for (IEdge inEdge : _graph.getIngoingEdges(childNode)) {
                                INode parentNode = inEdge.getParent();
                                if (schedule.getScheduling().containsKey(parentNode.getName())) {
                                    ScheduledTask parentScheduledTask = schedule.getScheduledTask(parentNode);
                                    //            12: if data arrival from np≥ T then and (parents(nc) − nk from line 11)
                                    System.out.println(schedule.toString());
                                    System.out.println("parent finish time \n\n:  " + parentScheduledTask.getFinishTime());
                                    int dataArrivalTime = parentScheduledTask.getFinishTime() + inEdge.getWeight();
                                    if (parentScheduledTask != scheduledTask && dataArrivalTime >= T) {
                                        //13: atLeastOneLater ← true
                                        atLeastOneLater = true;
                                    }
                                } else {
                                    atLeastOneLater = false;
                                }

                            }
                            //            14: if atLeastOneLater = false then
                            if (atLeastOneLater == false) {
                                //15: return false
                                return false;
                            }
                        }
                    }
                }
            }
        }
        //        16: return true
        return true;
    }


    private ScheduleAStar createSubSchedule(ScheduleAStar schedule, ScheduledTask scheduledTask) {
//        Hashtable<INode, Integer> newTable = getChildTable(schedule._inDegreeTable, scheduledTask.getTask());

        return new ScheduleAStar(
                schedule,
                scheduledTask,
                null);
    }

    public Hashtable<INode, Integer> getChildTable(Hashtable<INode, Integer> parentTable, INode x) {
        Hashtable<INode, Integer> tmp = new Hashtable<INode, Integer>(parentTable);
        tmp.remove(x);
        for (IEdge i : _graph.getOutgoingEdges(x.getName())) {
            int somthing = tmp.get(i.getChild()) - 1;
            tmp.replace(i.getChild(), tmp.get(i.getChild()) - 1);
        }
        return tmp;
    }

    public Hashtable<INode, Integer> getRootTable() {
        Hashtable<INode, Integer> tmp = new Hashtable<INode, Integer>();
        for (INode i : _graph.getAllNodes()) {
            tmp.put(i, 0);
        }
        for (INode i : _graph.getAllNodes()) {
            for (IEdge j : _graph.getOutgoingEdges(i.getName())) {
                tmp.replace(j.getChild(), tmp.get(j.getChild()) + 1);
            }
        }
        return tmp;
    }

}
