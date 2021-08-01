package raspberry.scheduler.graph;


/**
 * Basic interface of edge representation in IGraph
 * @author Neville
 */
public interface IEdge {

    /**
     * Get the weight of the current edge
     * @return weight weight of the current edge
     */
    int getWeight();

    /**
     * Get the child node of the edge
     * @return node that the edge is pointing to
     */
    INode getChild();

    /**
     * Get the parent node of the edge
     * @return node that the edge is pointing from
     */
    INode getParent();

}
