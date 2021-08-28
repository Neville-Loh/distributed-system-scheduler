package raspberry.scheduler.io;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;
import raspberry.scheduler.graph.adjacencylist.Graph;
import raspberry.scheduler.graph.IGraph;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * GraphReader class reads in .dot files in the correct format and and converts it
 * into a Graph
 * This class uses a dependency from maven com.paypal.digraph
 * link: https://github.com/paypal/digraph-parser
 * @author Young
 *
 *
 * Copyright statement
 * [BSD 3-Clause License]
 *
 * Copyright (c) 2017, PayPal Holdings, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

public class GraphReader {
    private final String _filepath;

    /**
     * GraphReader constructor to read input file
     * @param filepath the filepath of the input .dot file
     */
    public GraphReader(String filepath) {
        _filepath = filepath;
    }

    /**
     * reads the input file and converts it into our IGraph object
     * @return graph , an IGraph object
     * @throws FileNotFoundException when file is not found
     */
    public IGraph read() throws FileNotFoundException {
        //create parser from library that will read in dot file
        GraphParser parser = new GraphParser(new FileInputStream(_filepath));

        //initialise graph with name
        IGraph _graph = new Graph(parser.getGraphId().replaceAll("\"",""));

        //get nodes and edges from parser
        Map<String, GraphNode> nodes = parser.getNodes();
        Map<String, GraphEdge> edges = parser.getEdges();

        // add nodes to _graph
        for (GraphNode node : nodes.values()) {
            int weight = Integer.parseInt((String) node.getAttributes().get("Weight"));
            _graph.addNode(node.getId(), weight);
        }

        // add edges to _graph
        for (GraphEdge edge : edges.values()) {
            int weight = Integer.parseInt((String) edge.getAttributes().get("Weight"));
            _graph.addEdge(edge.getNode1().getId(),edge.getNode2().getId(),weight);
        }
        return _graph;
    }
}