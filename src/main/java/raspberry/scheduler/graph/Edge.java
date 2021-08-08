package raspberry.scheduler.graph;


/**
 * This class represents edges, which represent the communication cost of moving between
 * two nodes/tasks. It also stores the parent and child nodes connected.
 * @author Neville
 */
public class Edge implements IEdge{
    private INode _parentNode;
    private INode _childNode;
    private int _weight;

    /**
     * Default constructor for Edge object
     * @param parent
     * @param child
     * @param communicatonCost
     */
    public Edge(INode parent, INode child, int communicatonCost){
        _parentNode = parent;
        _childNode = child;
        _weight = communicatonCost;
    }

    /**
     * Gets the weight of the edge, which is the communication cost of transferring
     * between the parent and child nodes/tasks.
     * @return weight
     */
    @Override
    public int getWeight() {
        return _weight;
    }

    /**
     * Gets the child node.
     * @return child node
     */
    @Override
    public INode getChild() {
        return _childNode;
    }

    /**
     * Gets the parent node.
     * @return parent node
     */
    @Override
    public INode getParent() {
        return _parentNode;
    }

    /**
     * Gets a string describing the nature of the string: its child node and its weight.
     * @return string
     */
    @Override
    public String toString() {
    	return String.format("(pointsto=%s, weight=%d)", _childNode.getName(), _weight);

    }
}