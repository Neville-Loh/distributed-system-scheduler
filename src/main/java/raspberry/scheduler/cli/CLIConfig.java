package raspberry.scheduler.cli;

/**
 * CLIConfig class holds all the required and optional settings given for the program
 * @author Alan
 */
public class CLIConfig {

    /*
     * Required settings
     */
    private int _numProcessors;
    private String _dotFile;

    // number of parallel cores
    private int _numCores;

    // name of the output file
    private String _outputFile;

    // is visualise or not
    private boolean _visualise;

    /**
     * Default values
     * If no number of cores is defined - run sequntially
     * If no file name is defined - default is "INPUT-output.dot"
     */
    public static final int SEQUENTIAL_EXEC = 1;
    public static final int DEFAULT_NUMBER_OF_PROCESSORS = 1;

    /**
     * Constructor
     */
    public CLIConfig() {
        _visualise = false;
        _numCores = SEQUENTIAL_EXEC;
    }

    /**
     * Sets number of proccessors running for the program
     * @param numProcessors is the number of processes set by the user
     */
    public void setNumProcessors(int numProcessors) {
        _numProcessors = numProcessors;
    }

    /**
     * Returns the number of processes set
     * @return _numProcessors - an int that gives the number of processors set
     */
    public int get_numProcessors() {
        return _numProcessors;
    }


    /**
     * Sets the default input dot file read in by the program
     * @param fileName the file name as a string for the input dot file
     */
    public void setDotFile(String fileName) {
        _dotFile = fileName;
    }

    /**
     * returns input dot file name as a string
     * @return _dotFile - the input dot file name as a String
     */
    public String getDotFile() {
        return _dotFile;
    }

    /**
     * /sets the number of dedicated cores to run the program - default value is one.
     *
     * @param numCores - the number of cores set by the user
     */
    public void setNumCores(int numCores) {
        _numCores = numCores;
    }

    /**
     * returns the number of dedicated cores to run the program
     *
     * @return _numCores - the number of cores set to run the program
     */
    public int getNumCores() {
        return _numCores;
    }

    /**
     * sets whether or not program has to be visualised - default is false
     * @param visualise - boolean value - if false no visualisation, if true has visualisation.
     */
    public void setVisualise(boolean visualise) {
        _visualise = visualise;
    }

    /**
     * returns a boolean value for visualise to represent whether the program needs to be visualised.
     * @return _visualise - a boolean value of true or false, where true means program needs to be visualised
     * false meaning it doesn't
     */
    public boolean getVisualise() {
        return _visualise;
    }

    /**
     *Sets the output file name - default name format is input file name + out.dot i.e. INPUT-output.dot
     * @param fileName - the output file name given by the user. If filename has has .dot stated, it will be concatenated.
     */
    public void setOutputFile(String fileName) {
        fileName = fileName.replaceAll(".dot", "");
        _outputFile = fileName.concat(".dot");
    }

    /**
     * returns the Output file name as a String
     * @return _outputFile - returns the output file name as a string
     */
    public String getOutputFile() {
        return _outputFile;
    }


    /**
     * sets the default Output file name as a string in the INPUT-output.dot format
     */
    public void defaultOutput() {
        String inputFileName = _dotFile.replaceAll(".dot", "");
        _outputFile = inputFileName.concat("-output.dot");
    }


}