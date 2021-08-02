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

    //Default constructor
    public CLIConfig(){
        _visualise = false;
        _numCores = SEQUENTIAL_EXEC;
    }

    public void setNumProcessors(int numProcessors){
        _numProcessors = numProcessors;
    }
    public int get_numProcessors(){
        return _numProcessors;
    }

    public void setDotFile(String fileName){
        _dotFile = fileName;
    }

    public String getDotFile(){
        return _dotFile;
    }

    public void setNumCores(int numCores){
        _numCores = numCores;
    }

    public int getNumCores(){
        return _numCores;
    }
    public void setVisualise(boolean visualise){
        _visualise = visualise;
    }

    public boolean getVisualise(){
        return _visualise;
    }

    public void setOutputFile(String fileName){
        _outputFile = fileName;
    }

    public String getOutputFile(){
        return _outputFile;
    }

    public void defaultOutput(){
        String inputFileName = _dotFile.substring(0, _dotFile.length() - 4)
        _outputFile = _dotFile.concat(inputFileName);
    }



}
