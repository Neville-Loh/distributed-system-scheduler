package raspberry.scheduler.graph;


/**
 * Class represent edges
 * @author Neville
 */
public class Edge implements IEdge{
    private INode _parentNode;
    private INode _childNode;
    private int _weight;

    /**
     *
     * @param parent
     * @param child
     * @param communicatonCost
     */
    public Edge(INode parent, INode child, int communicatonCost){
        _parentNode = parent;
        _childNode = child;
        _weight = communicatonCost;
    }


    @Override
    public int getWeight() {
        return _weight;
    }

    @Override
    public INode getChild() {
        return _childNode;
    }

    @Override
    public INode getParent() {
        return _parentNode;
    }
    
    @Override
    public String toString() {
    	return String.format("(pointsto=%s, weight=%d)", _childNode.getName(), _weight);

    }
}