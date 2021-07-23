package main.java.raspberry.scheduler.graph;

/**
 * Interface for graph data representation of the applications
 * @author Neville
 */

public interface IGraph{
    /**
     * Get the node in the current graph with the given id
     * @return node Node with the id
     */
    INode getNode(String id);

    /**
     * Add a node the the current graph
     * @see INode
     * @param node the node to be added
     */
    void addNode(INode node);

    /**
     *  Add an edge to the current graph
     * @param parentNodeID id of the node that the edge is pointing from
     * @param childNodeID id of the node that the edge is pointing to
     * @param Weight weight of the edge
     */
    void addEdge(String parentNodeID, String childNodeID, int Weight);
}

