package raspberry.scheduler.graph;

import java.util.Collection;
import java.util.List;

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
     * @param id name of the node
     * @param value weight of the node
     */
    void addNode(String id, int value);


    /**
     *  Add an edge to the current graph
     * @param parentNodeID id of the node that the edge is pointing from
     * @param childNodeID id of the node that the edge is pointing to
     * @param Weight weight of the edge
     */
    void addEdge(String parentNodeID, String childNodeID, int Weight);


    /**
     * Return the outgoing edges of node
     * @param id the string id of the node
     * @return edges
     */
    List<IEdge> getOutgoingEdges(String id);


    /**
     * Return the outgoing edges of node
     * @param id the string id of the node
     * @return edges
     */
    List<IEdge> getIngoingEdges(String id);


    /**
     * @param parent
     * @param child
     * @return
     * @throws EdgeDoesNotExistException
     */
    int getEdgeWeight(INode parent, INode child) throws EdgeDoesNotExistException;


    /**
     *
     * @return
     */
    Collection<INode> getAllNodes();


    /**
     * @return number of node this graph has.
     */
    int getNumNodes ();
}

