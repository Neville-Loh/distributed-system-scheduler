package raspberry.scheduler.io;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;
import raspberry.scheduler.graph.Graph;
import raspberry.scheduler.graph.IGraph;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * GraphReader class reads in .dot files in the correct format and and converts it
 * into a Graph
 *
 * This class uses a dependency from maven com.paypal.digraph
 * link: https://github.com/paypal/digraph-parser
 * @author Young
 *
 */

public class GraphReader {

    private IGraph _graph;
    private String _filepath;

    public GraphReader(String filepath) {
        _filepath = filepath;
    }

    public IGraph read() throws FileNotFoundException {
        GraphParser parser = new GraphParser(new FileInputStream(_filepath));
        System.out.println(parser.getGraphId());

        //initialise graph with name
        _graph = new Graph(parser.getGraphId());
        Map<String, GraphNode> nodes = parser.getNodes();
        Map<String, GraphEdge> edges = parser.getEdges();

        System.out.println("--- nodes:");
        // add nodes
        for (GraphNode node : nodes.values()) {
            System.out.println(node.getId() + " " + node.getAttributes());
            int weight = Integer.parseInt((String) node.getAttributes().get("Weight"));
            _graph.addNode(node.getId(), weight);
        }

        System.out.println("--- edges:");
        // add edges
        for (GraphEdge edge : edges.values()) {
            System.out.println(edge.getNode1().getId() + "->" + edge.getNode2().getId() + " " + edge.getAttributes());
            int weight = Integer.parseInt((String) edge.getAttributes().get("Weight"));
            _graph.addEdge(edge.getNode1().getId(),edge.getNode2().getId(),weight);
        }
        return _graph;
    }
}