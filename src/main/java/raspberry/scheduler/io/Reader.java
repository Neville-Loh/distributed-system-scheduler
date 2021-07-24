package main.java.raspberry.scheduler.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.raspberry.scheduler.graph.Graph;
import main.java.raspberry.scheduler.graph.IEdge;
import main.java.raspberry.scheduler.graph.IGraph;
import main.java.raspberry.scheduler.graph.INode;
import main.java.raspberry.scheduler.graph.Node;
import main.java.raspberry.scheduler.graph.Edge;

/**
 * Reader class reads in .dot files in the correct format and and converts it
 * into a Graph
 * 
 * @author Young
 * 
 */

public class Reader {

	// The correct format of the input files in Regex

	private static final String FIRST_LINE = "digraph \".*\" \\{";
	private static final String NODE_LINE = "([a-zA-Z0-9]+)\\[Weight=([0-9]+)];";
	private static final String EDGE_LINE = "([a-zA-Z0-9]+)->([a-zA-Z0-9]+)\\[Weight=([0-9]+)];";

	private IGraph _graph;

	public Reader(String filepath) {
		try {
			File file = new File(filepath);
			Scanner reader = new Scanner(file);
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				String noWhiteSpace = line.replaceAll(" ", "");
				String[] lineInfo = getLineInfo(line);
				if (checkFormat(FIRST_LINE, line)) {
					_graph = new Graph();
					System.out.println(String.format("first line is: %s", line));
				}
				if (checkFormat(NODE_LINE, noWhiteSpace)) {
					String nodeWeight = lineInfo[1].replaceAll("\\D", "");
					INode node = new Node(Integer.parseInt(nodeWeight));
					_graph.addNode(node);
					
					System.out.println(String.format("node name: %s, nodeWeight: %s", lineInfo[0], nodeWeight));
				}
				if (checkFormat(EDGE_LINE, noWhiteSpace)) {
					String parentNode = lineInfo[0];
					String childNode = lineInfo[2];
					String edgeWeight = lineInfo[3].replaceAll("\\D", "");					
					_graph.addEdge(parentNode, childNode, Integer.parseInt(edgeWeight));
					
					System.out.println(String.format("parentNode: %s, childNode: %s, edgeWeight: %s", parentNode, childNode, edgeWeight));
				}

			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}

	}

	/**
	 * get a list of words seperated by whitespace from file
	 * @param line
	 * @return
	 */
	private String[] getLineInfo(String line) {
		String[] nodeInfo;
		line = line.strip();
		nodeInfo = line.split("\\s+");
		return nodeInfo;
	}
	
	/**
	 * check if line from file is in the wanted format
	 * @param regex the wanted format
	 * @param line from file
	 * @return true if correct format, else false
	 */
	private boolean checkFormat(String regex, String line) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		return m.matches();
	}

}
