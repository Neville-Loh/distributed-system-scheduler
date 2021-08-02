package raspberry.scheduler;

import raspberry.scheduler.algorithm.OutputSchedule;
import raspberry.scheduler.algorithm.Schedule;
import raspberry.scheduler.graph.EdgeDoesNotExistException;
import raspberry.scheduler.graph.IEdge;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.INode;

import java.util.List;

public class TestSchedule {

    //must check whether there is violation of dependencies
    //check whether tasks overlap
    //check whether communication time is taken into account

    //given functions from interface:
    // start time of every task
    // processor
    // finished time of the entire thing
    // number of processor

    private IGraph _graph;
    private OutputSchedule _outputSchedule;

    public TestSchedule(IGraph graph, OutputSchedule outputSchedule) {
        _graph = graph;
        _outputSchedule = outputSchedule;
    }

    //check whether all tasks are in the schedule
    public boolean allTasksPresent() {
        return _graph.getAllNodes().size() == _outputSchedule.getNumTasks();
    }


    //overlap occurs when the following happens:
    // - a child node begins execution prior to the parent node
    // - a child node begins execution while parent node is still in progress.
    // - node with no dependency relation overlap
    public boolean isValid() throws EdgeDoesNotExistException {

        if(_checkOverlap() || !allTasksPresent()) {
            return false;
        }

        for (INode node : _graph.getAllNodes()) {
            List<IEdge> ingoingEdges = _graph.getIngoingEdges(node.getName());

            for (IEdge edge : ingoingEdges) {
                INode parentNode = edge.getParent();


                int parentEndTime = _outputSchedule.getStartTime(parentNode) + parentNode.getValue();
                if ((_outputSchedule.getProcessorNum(parentNode) != _outputSchedule.getProcessorNum(node))
                        && ((_graph.getEdgeWeight(parentNode, node) + parentEndTime)) > _outputSchedule.getStartTime(node)) {
                    return false;
                } else if (parentEndTime > _outputSchedule.getStartTime(node)) {
                    return false;
                }


            }
        }
        return true;
    }

    private boolean _checkOverlap(){
        for (INode node1 : _graph.getAllNodes()) {
            int startTime1 = _outputSchedule.getStartTime(node1);
            int endTime1 = _outputSchedule.getStartTime(node1) + node1.getValue();

            // check all other nodes to see if it is overlapping
            for (INode node2 : _graph.getAllNodes()) {
                int startTime2 = _outputSchedule.getStartTime(node2);

                // check same processor node for all other node that is not node 1
                if (node1 != node2
                        && _outputSchedule.getProcessorNum(node1) == _outputSchedule.getProcessorNum(node1)) {

                    // if node 2 start in between node 1 computation
                    if (startTime2 < endTime1 && startTime2 > startTime1){
                        return false;
                    }
                }
            }

        }
        return true;
    }


}
