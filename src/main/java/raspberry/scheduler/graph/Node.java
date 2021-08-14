package raspberry.scheduler.graph;

/**
 * This class represents the nodes of the input graph, which represent tasks.
 * The Node object created stores the node's name and its weight (execution time).
 */
public class Node implements INode{

    private String _name;
    private int _weight;

    /**
     * Default constructor for Node object
     * @param name name of the node
     * @param executionTime how long the task takes
     */

    public Node(String name, int executionTime){
        _name = name;
        _weight = executionTime;
    }

    /**
     * Gets the weight/value of the task: how long it takes to complete
     * @return weight
     */
    @Override
    public int getValue() {
        return _weight;
    }

    /**
     * Gets name of node in the form of a string
     * @return name string
     */
    @Override
    public String toString(){
        return _name;
    }

    /**
     * Gets name of node
     * @return name
     */
    @Override
    public String getName(){
        return _name;
    }
}