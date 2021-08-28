package raspberry.scheduler.graph.util;

import raspberry.scheduler.graph.IEdge;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.INode;

import java.util.*;

public class TopologicalOrder {

    private IGraph m_digraph;
    private Queue<INode> m_candidate;
    public ArrayList<INode> m_topological_order;
    private String runTimeString = "";
    private Hashtable<INode, Integer> inDegreeCount;
    public boolean orderExist = false;


    public TopologicalOrder(IGraph digraph){
        m_digraph = digraph;
        m_candidate = new LinkedList<INode>();
        m_topological_order = new ArrayList<INode>();
        inDegreeCount = new Hashtable<INode, Integer>();
    }

    /**
     * Check if there exist a topological order in a {@code Graph} , if it exists
     * then replace current topological order in the class to the order. Return true.
     * If not exist return false.
     *
     * <p> This function first find all the {@code Vertex} with 0 in-degree. Since if
     * there exist a topological order implies no cycle is in the graph, there exist
     *  a {@code Vertex} with 0 in-degree. Then added the vertex in priority queue.
     *  When any vertex is pop from the queue, the in-degree of its neighbor is deducted
     *  by 1. All neighbor with 0 in-degree afterward is added to the queue. This
     *  terminates when there is no element in the priority queue.
     *
     * @return if there exist a topological Order
     */
    public boolean computeOrder()
    {
        // count the in degree
        for (INode node : m_digraph.getAllNodes()){
            inDegreeCount.put(node, m_digraph.getIngoingEdges(node).size());
        }
        m_candidate.addAll(m_digraph.getNodesWithNoInDegree());
        runTimeString += ("initialize queue with 0 indegree vertex: " + m_candidate + "\n");

        while (!m_candidate.isEmpty())
        {
            // poll the smallest number in the priority queue and set it to vertex
            INode vertex = m_candidate.poll();
            m_topological_order.add(vertex);

            // loop through out degree of vertex and deduct one in degree from all neighbors.
            for (IEdge outDegree : m_digraph.getOutgoingEdges(vertex)){
                INode child = outDegree.getChild();
                inDegreeCount.replace(child,
                        inDegreeCount.get(child) -1 );
                if (inDegreeCount.get(child) == 0){
                    m_candidate.add(child);
                }
            }

            runTimeString += String.format("Exploring vertex %-5s candidate queue = %s%n",vertex.getName(), m_candidate);
        }

        // check if all vertex is include, if not, there exist a cycle
        if (m_topological_order.size() == m_digraph.getNumNodes()) {
            orderExist = true;
            return true;
        }

        return false;
    }


    /**
     * Return the topological order
     * @return arraylist of Inode
     */
    public ArrayList<INode> getTopologicalOrder(){
        return m_topological_order;
    }


    /**
     * Print runtime Report of topologicalOrder algorithm.
     */
    public void printReport() {

        System.out.println("---------------------------------------------");
        System.out.println("Topological order report:");
        System.out.print(runTimeString);

        System.out.println("---------------------------------------------");
        System.out.println("Result Topological Order:");
        System.out.println(m_topological_order);
        System.out.println();

    }

}
