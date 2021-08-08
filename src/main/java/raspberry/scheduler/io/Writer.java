package raspberry.scheduler.io;


import raspberry.scheduler.algorithm.OutputSchedule;
import raspberry.scheduler.graph.IEdge;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.INode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Writer class writes output schedule as a .dot files in the correct format
 *
 * @author Young
 */

public class Writer {
    private String _filepath;
    private IGraph _graph;
    private OutputSchedule _outputSchedule;
    private String _filename;


    /** Writer constructor
     * @param filepath name of output file without .dot extension
     * @param graph input graph we are getting output for
     * @param outputSchedule output schedule we are getting graph for
     */
    public Writer(String filepath, IGraph graph, OutputSchedule outputSchedule) {
        _filepath = filepath;
        _graph = graph;
        _outputSchedule = outputSchedule;
    }

    /**
     * writes the details of the output schedule in the correct format
     * @throws IOException indicate failed or interrupted I/O
     */
    public void write() throws IOException {
        createFile();

        //initialize writer
        FileWriter fileWriter = new FileWriter(_filepath);
        PrintWriter pw = new PrintWriter(fileWriter);

        //get filename without .dot extension
        String name = _filename.substring(0, _filename.lastIndexOf('.'));

        //write first line
        pw.println(String.format("digraph \"%s\" {", name));

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
        //retrieve file name without the path
        Path p = Paths.get(_filepath);
        _filename = p.getFileName().toString();

        try {
            File file = new File(_filepath);
            if (file.createNewFile()) {
                System.out.println("File created: " + _filepath);
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
