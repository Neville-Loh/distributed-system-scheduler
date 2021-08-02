package raspberry.scheduler.io;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import raspberry.scheduler.graph.Graph;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.INode;


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
	private Hashtable<INode, Integer> _nodeList;
	private String _filepath;


	public Reader(String filepath) {
		_filepath = filepath;
	}


	public void read() {
		try {
			File file = new File(_filepath);
			Scanner reader = new Scanner(file);

			String firstLine = reader.nextLine();

			String[] firstLineInfo = getLineInfo(firstLine);

			// check format of first line
			if (checkFormat(FIRST_LINE, firstLine) && firstLineInfo.length == 3) {
				String name = firstLineInfo[1].replaceAll("\"", "");
				_graph = new Graph(name);
			}

			// check format of the rest of lines
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				String noWhiteSpace = line.replaceAll(" ", "");
				String[] lineInfo = getLineInfo(line);
				if (checkFormat(NODE_LINE, noWhiteSpace) && lineInfo.length == 2) {
					String nodeWeight = lineInfo[1].replaceAll("\\D", "");
					_graph.addNode(lineInfo[0],Integer.parseInt(nodeWeight));
				}
				else if (checkFormat(EDGE_LINE, noWhiteSpace) && lineInfo.length == 4) {
					String parentNode = lineInfo[0];
					String childNode = lineInfo[2];
					String edgeWeight = lineInfo[3].replaceAll("\\D", "");
					_graph.addEdge(parentNode, childNode, Integer.parseInt(edgeWeight));
				}
				else if (lineInfo.length == 1 && !reader.hasNextLine() && lineInfo[0].equals("}")) {
					System.out.println(lineInfo[0]);
				}

			}
			reader.close();
			System.out.println(_graph);
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}
	}


	/**
	 * turn a line into a list of words split by whitespace from file
	 * @param line
	 * @return
	 */
	private String[] getLineInfo(String line) {
		String[] nodeInfo;
		line = line.trim();
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