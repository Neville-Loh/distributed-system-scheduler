package raspberry.scheduler.cli;

public class CLIConfig {
    /** CLI configuration interface **/

    private int _numProcessors;
    private String _dotFile;


    //Optional settings

    //Number of parallel cores
    private int _numCores;

    //output file name
    private String _outputFile;
    //visualise the search
    private boolean _visualise;

    //default values

    //if no number of cores is defined - run sequentially
    public static final int SEQUENTIAL_EXEC = 1;
    //if no file name is defined - default is "INPUT-output.dot"
    public static final String DEFAULT_OUTPUT = "INPUT-output.dot";

    //Default constructor
    public CLIConfig(){
        _visualise = false;
        _outputFile = DEFAULT_OUTPUT;
        _numCores = SEQUENTIAL_EXEC;
    }

    public void setNumProcessors(int numProcessors){
        _numProcessors = numProcessors;
    }

    public void setDotFile(String fileName){
        _dotFile = fileName;
    }

    public void setNumCores(int numCores){
        _numCores = numCores;
    }


}
