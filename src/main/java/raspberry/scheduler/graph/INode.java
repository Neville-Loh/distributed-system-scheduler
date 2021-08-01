package raspberry.scheduler.graph;

import java.util.List;

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
     * String
     */
    String getName();
}
