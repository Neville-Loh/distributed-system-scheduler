package raspberry.scheduler.graph;

/**
 * Interface for graph data representation of the applications
 * @author Neville
 */

public interface INode {
    /**
     * Get the value which contained in the node
     * @return integer that is contained in the node
     */
    int getValue();

    /**
     * Get the name of the graph
     * @return name name of the graph
     */
    String getName();
}
