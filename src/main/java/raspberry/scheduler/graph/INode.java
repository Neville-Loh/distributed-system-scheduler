package main.java.raspberry.scheduler.graph;

import java.util.List;

/**
 * Interface for graph data representation of the applications
 * @author Neville
 */

public interface INode {
    /**
     * Check and see if the node has parent or not
     * @return True if node has no parent, else False
     */
    boolean hasParent();

    /**
     * Get a list containing all outgoing edges of the current node
     * @return the outgoing edges of the current node
     */
    List<IEdge> getOutGoingEdges();


    /**
     * Get the value which contained in the node
     * @return integer that is contained in the node
     */
    int getValue();

    /**
     * String
     */
    String getName();
}
