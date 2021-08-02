package raspberry.scheduler.io;


import raspberry.scheduler.algorithm.OutputSchedule;
import raspberry.scheduler.algorithm.Solution;
import raspberry.scheduler.graph.IEdge;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.INode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Writer class writes output schedule as a .dot files in the correct format
 *
 * @author Young
 */

public class Writer {
    private String _outputName;
    private String _filepath = "src/..";
    private IGraph _graph;
    private OutputSchedule _outputSchedule;

    /**
     * Writer constructor
     * @param filename name of output file without .dot extension
     * @param filepath path of file e.g. "src/test/resources/output"
     * @param graph input graph we are getting output for
     * @param outputSchedule output schedule we are getting graph for
     */
    public Writer(String filename, String filepath, IGraph graph, OutputSchedule outputSchedule) {
        _outputName = filename;
        _graph = graph;
        _outputSchedule = outputSchedule;
        _filepath = filepath;
    }

    /** Writer constructor with no filepath specified
     * if no filepath is specified then output file to root folder
     * @param filename name of output file without .dot extension
     * @param graph input graph we are getting output for
     * @param outputSchedule output schedule we are getting graph for
     */
    public Writer(String filename, IGraph graph, OutputSchedule outputSchedule) {
        _outputName = filename;
        _graph = graph;
        _outputSchedule = outputSchedule;
    }

    /**
     * writes the details of the output schedule in the correct format
     * @throws IOException
     */
    public void write() throws IOException {
        createFile();

        //initialize writer
        FileWriter fileWriter = new FileWriter(String.format("%s/%s.dot",_filepath,_outputName));
        PrintWriter pw = new PrintWriter(fileWriter);

        //write first line
        pw.println(String.format("digraph \"%s\" {", _outputName));

        for (INode node : _graph.getAllNodes()) {
            //write node lines in the correct format
            pw.println(String.format("\t%s\t[Weight=%d,Start=%d,Processor=%d];",
                    node.getName(), node.getValue(), _outputSchedule.getStartTime(node), _outputSchedule.getProcessorNum(node)));

            // write edge lines in the correct format
            for (IEdge edge : _graph.getOutgoingEdges(node.getName())) {
                pw.println(String.format("\t%s -> %s\t[Weight=%d];",
                        edge.getParent(), edge.getChild(), edge.getWeight()));
            }
        }
        pw.print("}");
        pw.close();
    }


    /**
     * creates the file
     */
    private void createFile() {
        try {
            File file = new File(String.format("%s/%s.dot",_filepath,_outputName));
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
