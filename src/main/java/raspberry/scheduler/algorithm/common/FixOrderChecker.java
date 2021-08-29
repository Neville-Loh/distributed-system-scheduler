package raspberry.scheduler.algorithm.common;

import raspberry.scheduler.graph.IEdge;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.INode;

import java.util.List;

public class FixOrderChecker {

    private IGraph _graph;

    /**
     * Constructor
     * @param graph dependency graph
     */
    public FixOrderChecker(IGraph graph){
        _graph = graph;
    }


    /**
     * checks if a fixed order is possible given the free nodes available
     * @param freeNodes list of available nodes that can be scheduled
     * @param schedule current schedule
     * @return true if fixed order is possible, false otherwise
     */
    public boolean check(List<INode> freeNodes, Schedule schedule){
        // if nf has at most one parent and at most one child
        for (INode node : freeNodes){
            int numOfParent = _graph.getIngoingEdges(node).size();
            int numOfChild = _graph.getOutgoingEdges(node).size();
            if ( numOfParent > 1 || numOfChild > 1){
                return false;
            }
        }

        // if nf has a child, | child (nf )| = 1,
        // then it is the same child as for any other task in free(s):
        INode firstFoundChild = null;
        for (INode node : freeNodes){
            int numOfChild = _graph.getOutgoingEdges(node).size();

            // if node has child
            if ( numOfChild == 1){
                // if child is not initialized
                if (firstFoundChild == null) {
                    // put the first found child as variable
                    firstFoundChild = _graph.getOutgoingEdges(node).get(0).getChild();

                    // check if the found child is the first found child
                } else if (firstFoundChild != _graph.getOutgoingEdges(node).get(0).getChild()){
                    return false;
                }
            }
        }

        // if nf has a parent, |parents(nf )| = 1, then all other parents of tasks in free(s)
        // are allocated to the same processor Pp:
        int firstFoundPID = -1;
        for (INode node : freeNodes){
            int numOfParent = _graph.getIngoingEdges(node).size();
            if ( numOfParent == 1){
                INode parent = _graph.getIngoingEdges(node).get(0).getParent();
                if (firstFoundPID == -1) {
                    firstFoundPID = schedule.getScheduledTask(parent).getProcessorID();
                } else if (firstFoundPID != schedule.getScheduledTask(parent).getProcessorID()){
                    return false;
                }

            }
        }
        return true;
    }

    /**
     *  get list of free nodes in fixed order which is the order
     *  which guarantees the order in which the free nodes can be scheduled
     * @param freeNodes list of free nodes that are available to be scheduled
     * @param schedule the current schedule
     * @return the list of nodes in fixed order
     */
    public List<INode> getFixOrder(List<INode> freeNodes, Schedule schedule){
        freeNodes.sort( (n1,n2) -> {
            int drt1 = getDataReadyTime(n1, schedule);
            int drt2 = getDataReadyTime(n2, schedule);

            if (drt1 == drt2) {
                return -1 * Integer.compare(outGoingEdgeCost(n1), outGoingEdgeCost(n2));
            } else{
                return Integer.compare(drt1,drt2);
            }});

        for (int i = 1; i < freeNodes.size(); i++ ){
            INode n1 = freeNodes.get(i-1);
            INode n2 = freeNodes.get(i);
            if (outGoingEdgeCost(n2)> outGoingEdgeCost(n1)){
                return null;
            }
        }
        return freeNodes;
    }


    /**
     * get data ready time which is the finish time of parent task plus the
     * communication cost if in different processors
     * @param task the task we are getting dataReadyTime for
     * @param schedule the current schedule
     * @return the data ready time
     */
    private int getDataReadyTime(INode task, Schedule schedule){

        // if task does not have a parent
        if (_graph.getIngoingEdges(task).size() == 0){
            return 0;
        }

        // if the task has a parent
        int dataReadyTime = Integer.MIN_VALUE;
        for (IEdge edge : _graph.getIngoingEdges(task)){
            INode parent = edge.getParent();
            int parentDataReadyTime = schedule.getScheduledTask(parent).getFinishTime() + edge.getWeight();
            dataReadyTime = Math.max(dataReadyTime,parentDataReadyTime);
        }
        return dataReadyTime;
    }

    /**
     * get the outgoing edge cost of a node with its parent
     * @param node the node we are getting the outGoingEdge cost for
     * @return the edge cost from node to its parent, 0 if node has no parent,
     */
    private int outGoingEdgeCost(INode node){
        if (_graph.getOutgoingEdges(node).size() == 0){
            return 0;
        }
        return _graph.getOutgoingEdges(node).get(0).getWeight();
    }
}
