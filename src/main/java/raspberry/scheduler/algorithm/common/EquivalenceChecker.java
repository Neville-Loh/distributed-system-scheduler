package raspberry.scheduler.algorithm.common;

import raspberry.scheduler.algorithm.*;
import raspberry.scheduler.algorithm.sma.MBSchedule;
import raspberry.scheduler.graph.*;

import java.util.*;

import static raspberry.scheduler.algorithm.sma.MemoryBoundAStar.calculateEarliestStartTime;

public class EquivalenceChecker {


    private IGraph _graph;
    private int _numProcessors;

    public EquivalenceChecker(IGraph graph, int numProcessors){
        _graph = graph;
        _numProcessors = numProcessors;
    }


    public boolean weAreDoomned(MBSchedule schedule){
        //Require: Each task n ∈ V has a unique index 0 ≤ index(n) ≤ |V| − 1; in ascending index order tasks are also in
//        a topological order
//        Input: Partial schedule, including last scheduled task m at end of processor P
//        1: tmax = tf (m)
         ScheduledTask m = schedule.getScheduledTask();
         int TMax = m.getFinishTime();
//        2: Name tasks on P n1 to nl n start time order B nl = m
        ArrayList<ScheduledTask> processorTaskList = schedule.getAllTaskinProcessor(m.getProcessorID());
        // sort by accenting start time
        processorTaskList.sort(Comparator.comparingInt(ScheduledTask::getOriginalStartTime));

        Hashtable<Integer, ScheduledTask> callitahhoowwwhatdoicallit = new Hashtable<Integer, ScheduledTask>();
        int i = 1;
        while (i < processorTaskList.size() + 1){
            callitahhoowwwhatdoicallit.put(i, processorTaskList.get(i-1));
            i++;
        }
        int justcallitlikesecondlastindex = callitahhoowwwhatdoicallit.size()-1;
        i = justcallitlikesecondlastindex;
        while (i >= 0
                && _graph.getIndex(m.getTask()) <
                _graph.getIndex(callitahhoowwwhatdoicallit.get(i).getTask())){
                MBSchedule swapped = swap(schedule, m, callitahhoowwwhatdoicallit.get(i));

                if (swapped.getScheduledTask(callitahhoowwwhatdoicallit
                        .get(justcallitlikesecondlastindex)
                        .getTask())
                        .getFinishTime() <= TMax && outgoingCommsOK(processorTaskList,swapped)){
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

    public boolean outgoingCommsOK(List<ScheduledTask> scheduledTasks, MBSchedule schedule) {
//    Algorithm 3 Subroutine OutgoingCommsOK(ni. . . nl−1)
//1: for all nk ∈ {ni. . . nl−1} do
//            2: if ts(nk) > torigs (nk) then B check only if nk starts later
        for (ScheduledTask scheduledTask: scheduledTasks) {
            if(scheduledTask.getStartTime() > scheduledTask.getOriginalStartTime()) {
                //3: for all nc ∈ children(nk) do
                for (IEdge outEdge : _graph.getOutgoingEdges(scheduledTask.getTask())) {
                    INode childNode = outEdge.getChild();
//              4: T ← tf (nk) + c(ekc) B remote data arrival from nk
                    int T = scheduledTask.getFinishTime() + outEdge.getWeight();
//              5: if nc scheduled then
                    if (schedule.getPath().containsKey(childNode)) {
                        ScheduledTask childScheduledTask = schedule.getScheduledTask(childNode);
//              6: if ts(nc) > T ∧ proc(nc) 6= P then B on same proc always OK
                        if (childScheduledTask.getStartTime() > T && childScheduledTask.getProcessorID() != scheduledTask.getProcessorID()) {
                            //              7: return false
                            return false;
                        }
                    }
                    //        8: else B nc not scheduled yet
                    else {
                        //9: for all Pi ∈ P/P do B nc can be on any proc; P always OK
                        for (int i = 1; i < _numProcessors + 1 && i != scheduledTask.getProcessorID(); i++) {
                            //10: atLeastOneLater ← false
                            boolean atLeastOneLater = false;
                            //            11: for all np ∈ parents(nc) − nk do
                            for (IEdge inEdge : _graph.getIngoingEdges(childNode)) {
                                INode parentNode = inEdge.getParent();
                                ScheduledTask parentScheduledTask = schedule.getScheduledTask(parentNode);
                                //            12: if data arrival from np≥ T then and (parents(nc) − nk from line 11)
                                int dataArrivalTime = parentScheduledTask.getFinishTime() + inEdge.getWeight();
                                if (parentScheduledTask != scheduledTask && dataArrivalTime >= T) {
                                    //13: atLeastOneLater ← true
                                    atLeastOneLater = true;
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
    //count all the prefect substring?
    //
    private MBSchedule swap(MBSchedule schedule, ScheduledTask m, ScheduledTask taskToSwap){

        MBSchedule result;
        MBSchedule cSchedule = schedule.parent;
        ArrayList<ScheduledTask> prevTask = new ArrayList<>();

        while (cSchedule != null){
            if (cSchedule.getScheduledTask().equals(taskToSwap)){
                cSchedule = cSchedule.parent;
                break;
            }
            prevTask.add(cSchedule.getScheduledTask());
            cSchedule = cSchedule.parent;
        }

        //int earliestStartTime = calculateEarliestStartTime(cSchedule, numProcessor, task);
        //                            MBSchedule subSchedule = cSchedule.createSubSchedule(
        //                                    new ScheduledTask(numProcessor,task,earliestStartTime), _graph);

        int earliestStartTime = calculateEarliestStartTime(cSchedule, taskToSwap.getProcessorID(), m.getTask());
        int mProcessorID = m.getProcessorID();

        // create new scheduled Task for m in at a new start Time
        ScheduledTask scheduleM = new ScheduledTask( m.getProcessorID(),m.getTask(), m.getOriginalStartTime());
        scheduleM.setStartTime(earliestStartTime);
        result = cSchedule.parent.createSubSchedule(scheduleM, _graph);


        Collections.reverse(prevTask);
        for (ScheduledTask st : prevTask){
            if (st.getProcessorID() == mProcessorID){
                // calculate start time
                ScheduledTask scheduleST = new ScheduledTask( st.getProcessorID(),st.getTask(), st.getOriginalStartTime());
                scheduleST.setStartTime(earliestStartTime);
                result = result.createSubSchedule(scheduleST,_graph);
            } else {
                // just schedule
                result = result.createSubSchedule(st, _graph);
            }
        }

        // scheduling
        ScheduledTask scheduleST = new ScheduledTask( taskToSwap.getProcessorID(),taskToSwap.getTask(), taskToSwap.getOriginalStartTime());
        scheduleST.setStartTime(earliestStartTime);
        result = result.createSubSchedule(scheduleST,_graph);




        return result;
    }
}
