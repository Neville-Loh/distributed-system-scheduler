package main.java.raspberry.scheduler.cli;

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
    public static final int DEFAULT_NUMBER_OF_PROCESSORS = 1;


    //Default constructor
    public CLIConfig(){
        _visualise = false;
        _numCores = SEQUENTIAL_EXEC;
    }

    // I made this into a string to resolve an issue.
    //Sets the number of processors running the algorithm
    public void setNumProcessors(int numProcessors){
        _numProcessors = numProcessors;
    }
    //returns the number of processors running the algorithm
    public int get_numProcessors(){
        return _numProcessors;
    }

    // throws ParserConfigurationException <- add this later for checking whether the input is valid
    // pnum = numProcessors in string format
    public int get_numProcessors(String pnum){
        _numProcessors = DEFAULT_NUMBER_OF_PROCESSORS;
        _numProcessors = Integer.parseInt(pnum);
        return _numProcessors;
    }

    //sets the specified input dot file
    public void setDotFile(String fileName){
        _dotFile = fileName;
    }

    //returns input dot file name as a string
    public String getDotFile(){
        return _dotFile;
    }

    //sets the number of dedicated cores to run the program - default value is one.
    public void setNumCores(int numCores){
        _numCores = numCores;
    }

    //returns the number of dedicated cores to run the program
    public int getNumCores(){
        return _numCores;
    }

    //sets wether or not program has to be visualised - default is false
    public void setVisualise(boolean visualise){
        _visualise = visualise;
    }

    //returns a boolean value for visualise to represent whether the program needs to be visualised.
    public boolean getVisualise(){
        return _visualise;
    }

    //Sets the output file name - default name format is input file name + out.dot i.e. INPUT-output.dot
    public void setOutputFile(String fileName){
        _outputFile = fileName;
    }

    //returns the Output file name as a String
    public String getOutputFile(){
        return _outputFile;
    }

    //sets the default Output file name as a string in the INPUT-output.dot format
    public void defaultOutput(){
        String inputFileName = _dotFile.substring(0, _dotFile.length() - 4);
        _outputFile = _dotFile.concat(inputFileName);
    }




}