package raspberry.scheduler.algorithm.common;

import raspberry.scheduler.algorithm.*;
import raspberry.scheduler.graph.*;

import java.util.List;

public class EquivalenceChecker {

    public boolean OutgoingCommsOK(List<ScheduledTask> scheduledTasks, Schedule schedule) {
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
                        for (int i = 1; i < _numP + 1 && i != scheduledTask.getProcessorID(); i++) {
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
}
