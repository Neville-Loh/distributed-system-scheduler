package main.java.raspberry.scheduler.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import main.java.raspberry.scheduler.graph.Graph;
import main.java.raspberry.scheduler.graph.Node;
import main.java.raspberry.scheduler.graph.Edge;

/**
 * Reader class reads in .dot files in the correct format and 
 * and converts it into a Graph
 * @author Young
 * 
 */

public class Reader {
	
	//The correct format of the input files in Regex
	
		private static final String FIRST_LINE = "digraph \".*\" \\{";
		private static final String NODE_LINE = "([a-zA-Z0-9]+)\\[Weight=([0-9]+)];";
		private static final String EDGE_LINE = "([a-zA-Z0-9]+)->([a-zA-Z0-9]+)\\[Weight=([0-9]+)];";
		private static final String LAST_LINE = "\t\\{";

	private Graph _graph;
	private List<Node> _nodeList;
	private List<Edge> _edgeList;
	

	public Reader(String filepath) {
		try {
			File file = new File(filepath);
			Scanner reader = new Scanner(file);
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				String noWhiteSpace = line.replaceAll(" ", "");
//				System.out.println(line);
				if(checkFormat(FIRST_LINE,line)) {
//					_graph = new Graph("placeholder");
					System.out.println(String.format("first line is: %s", line));
				}
				if(checkFormat(NODE_LINE,noWhiteSpace)) {
//					_graph = new Graph("placeholder");
					System.out.println(String.format("Node line is: %s", line));
				}
				if(checkFormat(EDGE_LINE,noWhiteSpace)) {
//					_graph = new Graph("placeholder");
					System.out.println(String.format("Edge line is: %s", line));
				}
				if(checkFormat(LAST_LINE,line)) {
//					_graph = new Graph("placeholder");
					System.out.println(String.format("LAST line is: %s", line));
				}
//				System.out.println(data);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}

	}
	
	
	private boolean checkFormat(String regex, String line) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		return m.matches();
	}

}
