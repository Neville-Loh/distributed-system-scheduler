package raspberry.scheduler.graph.adjacencylist;


import raspberry.scheduler.graph.IEdge;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.INode;
import raspberry.scheduler.graph.exceptions.EdgeDoesNotExistException;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Adjacency graph implementation of the graph
 * This class store the graph data using adjacency list
 * which a list of edges is stored for each node.
 */
public class Graph implements IGraph {
    private String _name;
    private Hashtable<String, INode> _nodes;
    private Hashtable<String, List<IEdge>> _inDegreeAdjacencyList;
    private Hashtable<String, List<IEdge>> _outDegreeAdjacencyList;
    private Hashtable<String,Integer> _criticalPathWeightTable;
    private Hashtable<INode, Integer> _indexTable;
    private ArrayList<INode> _topologicalOrder;

    /**
     * Class Constructor
     * @param name name of the graph
     */
    public Graph(String name){
        _name = name;
        _nodes = new Hashtable<String, INode>();
        _inDegreeAdjacencyList = new Hashtable<String, List<IEdge>>();
        _outDegreeAdjacencyList = new Hashtable<String, List<IEdge>>();
    }

    @Override
    public INode getNode(String id) {
        return _nodes.get(id);
    }

    @Override
    public void addNode(String id, int value) {
        INode node = new Node(id, value);
        _inDegreeAdjacencyList.put(id, new ArrayList<IEdge>());
        _outDegreeAdjacencyList.put(id, new ArrayList<IEdge>());
        _nodes.put(id,node);
    }

    @Override
    public void addEdge(String parentNodeID, String childNodeID, int weight) {
        INode p = _nodes.get(parentNodeID);
        INode c = _nodes.get(childNodeID);
        IEdge e = new Edge(p, c, weight);
        _outDegreeAdjacencyList.get(parentNodeID).add(e);
        _inDegreeAdjacencyList.get(childNodeID).add(e);
    }

    @Override
    public int getEdgeWeight(INode parent, INode child) throws EdgeDoesNotExistException {
        for (IEdge edge : _outDegreeAdjacencyList.get(parent.getName())){
            if (edge.getChild() == child){
                return edge.getWeight();
            }
        }
        throw new EdgeDoesNotExistException("Edge does not exists");
    }

    @Override
    public Collection<INode> getAllNodes() {
        return this._nodes.values();
    }

    @Override
    public int getNumNodes() {
        return this._nodes.size();
    }


    @Override
    public String toString(){
    	StringBuilder output = new StringBuilder("Graph: " + this._name + "\n");
    	for (String name: _outDegreeAdjacencyList.keySet()) {
    	    String key = name.toString();
    	    String value = _outDegreeAdjacencyList.get(name).toString();
    	    output.append("Node:")
                    .append(key)
                    .append(" cost=")
                    .append(_nodes.get(key).getValue())
                    .append(" ")
                    .append(value)
                    .append("\n");
    	}
        return output.toString();
    }


    @Override
    public Collection<INode> getNodesWithNoInDegree() {
        ArrayList<INode> result = new ArrayList<INode>();
        _inDegreeAdjacencyList.forEach( (nodeID, inEdges) -> {
            if (inEdges.size() == 0){
                result.add(_nodes.get(nodeID));
            }
        });
        return result;
    }

    @Override
    public Hashtable<INode, Integer> getInDegreeCountOfAllNodes() {
        Hashtable<INode, Integer> result = new Hashtable<INode, Integer>();
        this.getAllNodes().forEach( node -> result.put(node,0));
        this.getAllNodes().forEach( node -> {
            this.getOutgoingEdges(node.getName()).forEach( edge -> {
                result.put(edge.getChild(), result.get(edge.getChild()) + 1);
            });
        });
        return result;
    }


    @Override
    public Hashtable<INode,Integer> getCriticalPathWeightTable(){
        _criticalPathWeightTable = new Hashtable<String, Integer>();
        ArrayList<String> start = new ArrayList<String>();
        _inDegreeAdjacencyList.forEach( (k,v) -> {
            if (v.size() == 0){
                start.add(k);
            }
            _criticalPathWeightTable.put(k,-1);
        });
        start.forEach(node -> {
            int val = dfsFindCriticalWeight(node);
            if (_criticalPathWeightTable.containsKey(node)){
                _criticalPathWeightTable.put(node, Math.max(_criticalPathWeightTable.get((node)), val));
            } else {
                _criticalPathWeightTable.put(node, val);
            }
        });
        Hashtable<INode, Integer> result = new Hashtable<INode, Integer>();
        _criticalPathWeightTable.forEach((k,v) -> result.put(_nodes.get(k), v - _nodes.get(k).getValue()));
        return result;
    }

    /**
     * Private method
     * recursive method to find the critical path weight of the node
     * @param node node to be found
     * @return weight the critical path weight
     */
    private int dfsFindCriticalWeight(String node){
        List<IEdge> edges = getOutgoingEdges(node);
        int computeTime = _nodes.get(node).getValue();
        if (edges.size() == 0 ){
            _criticalPathWeightTable.put(node,computeTime);
            return computeTime;
        } else {
            AtomicInteger currentMax = new AtomicInteger(_criticalPathWeightTable.get(node));
            edges.forEach(edge -> {
                currentMax.set(Math.max(currentMax.get(),computeTime + dfsFindCriticalWeight(edge.getChild().getName())));
            });
            _criticalPathWeightTable.put(node,currentMax.intValue());
            return currentMax.intValue();
        }

    }


    @Override
    public List<IEdge> getOutgoingEdges(String id) {
        return _outDegreeAdjacencyList.get(id);
    }

    @Override
    public List<IEdge> getOutgoingEdges(INode node) {
        return getOutgoingEdges(node.getName());
    }

    @Override
    public List<IEdge> getIngoingEdges(String id) {
        return _inDegreeAdjacencyList.get(id);
    }

    @Override
    public List<IEdge> getIngoingEdges(INode node) {
        return getIngoingEdges(node.getName());
    }

    @Override
    public String getName() {
        return _name;
    }

    /**
     *  Topological order
     */

    @Override
    public int getIndex(INode node){
        if (_indexTable == null){
            setIndexTable();
        }
        return _indexTable.get(node);
    }



    private void setIndexTable(){
        _indexTable = new Hashtable<>();
        ArrayList<INode> visited = new ArrayList<>();
        Collections.reverse(_topologicalOrder);
        for(int i = 0; i < _topologicalOrder.size(); i++){
            _indexTable.put(_topologicalOrder.get(i), i);
        }

    }
    private void getTopologicalOrder(){
        _topologicalOrder = new ArrayList<INode>();
        for ( INode i : _nodes.values()){
            getTopoligicalOrderRecursive(i);
        }
        printTopo();
    }

    private void getTopoligicalOrderRecursive(INode i){
        if ( _outDegreeAdjacencyList.get(i) == null || _outDegreeAdjacencyList.get(i).isEmpty() ){
            //
        }else{
            for ( IEdge e : _outDegreeAdjacencyList.get(i) ) {
                getTopoligicalOrderRecursive(e.getChild());
            }
        }
        if ( !_topologicalOrder.contains(i) ){
            _topologicalOrder.add(i);
        }
    }

    public void printTopo(){
        for (INode i: _topologicalOrder){
            System.out.printf("%s_",i.getName());
        }
    }
}
