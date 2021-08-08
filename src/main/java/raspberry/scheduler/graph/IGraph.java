package raspberry.scheduler.graph;

import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;
import java.util.Collection;
import java.util.Hashtable;
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
     * @param node the target node
     * @return edges
     */
    List<IEdge> getOutgoingEdges(INode node);


    /**
     * Return the outgoing edges of node
     * @param id the string id of the node
     * @return edges
     */
    List<IEdge> getIngoingEdges(String id);

    /**
     * Return the outgoing edges of node
     * @param node the target node
     * @return edges
     */
    List<IEdge> getIngoingEdges(INode node);


    /**
     * Get the edge weight that points from parent to child
     * Note that if a edge points from child to parent, to get the weight
     * the order of the argument given should be reversed.
     * @param parent node that the edges point from
     * @param child node that the edge points to
     * @return weight weight of the edges
     * @throws EdgeDoesNotExistException if edge does not exists
     */
    int getEdgeWeight(INode parent, INode child) throws EdgeDoesNotExistException;


    /**
     * Get all node in the graph as a collections
     * @return collections of all node in graph
     */
    Collection<INode> getAllNodes();


    /**
     * Get the total number of nodes
     * @return number of node this graph has.
     */
    int getNumNodes ();


    /**
     * Count the maximum node weight from the target node to the end of the path for
     * every node in the path.
     * returns a hashtable
     * key: node
     * value: sum of node weight from the current node to the furthest node (the sum
     * exclude the weight of current node)
     *
     * Note that the weight does not include the edge weight
     * This method is set up to compute the scheduling problem heuristic
     * @return hashtable
     */
    public Hashtable<INode,Integer> getCriticalPathWeightTable();

    /**
     * Get a collection of nodes with no incoming edges in the current
     * graph.
     * @return nodes collection of node that contain no in coming edges
     */
    public Collection<INode> getNodesWithNoInDegree();


    /**
     * Count all node inDegree,
     * return a hashTable with every Node, where Node is the key
     * and parent counts are value.
     * @return table contains Key: INode, Value: number of in degree
     */
    public Hashtable<INode, Integer> getInDegreeCountOfAllNodes();
}

