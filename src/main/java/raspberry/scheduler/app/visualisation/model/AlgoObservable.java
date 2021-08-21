package raspberry.scheduler.app.visualisation.model;
import raspberry.scheduler.algorithm.Schedule;
import raspberry.scheduler.algorithm.Solution;
import raspberry.scheduler.graph.INode;
import raspberry.scheduler.graph.Node;

/**
 * AlgoObservable class receives the output data from the algorithm classes, such as iterations
 * and current schedule for live visualisation and stores it.
 */
public class AlgoObservable{

    //number of iterations the algorithms has passed through
    private int _iterations;
    // boolean for whether algorithm is running
    private boolean _isFinish;
    // the current output schedule
    private Solution _solution;
    //current best schedule
    private  Solution _currentBestSchedule;

    // checks for whether there is a single instance of the class running
    private static AlgoObservable single_instance = null;

    /**
     * Default constructor for class
     */
    private AlgoObservable(){
        _isFinish = false;
        _currentBestSchedule = new Solution(new Schedule(0,null,new Node("zero",0),0),0);
    }

    /**
     * Update the number of iterations once the algorithm is run
     */
    public void increment() {
        _iterations++;
    }

    /**
     * Returns the number of iterations.
     * @return number of iterations
     */
    public int getIterations() {
        return _iterations;
    }

    /**
     * Assign the number of iterations.
     * @param iterations number of iterations
     */
    public void setIterations(int iterations) {
        _iterations = iterations;
    }

    /**
     * Returns the run state of the algorithm
     * @return boolean indicating whether the algorithm is running
     */
    public boolean getIsFinish() {
        return _isFinish;
    }

    /**
     * Appoints the run state of the algorithm.
     * @param isFinish boolean indicating whether the algorithm is running
     */
    public void setIsFinish(boolean isFinish) {
        _isFinish = isFinish;
    }

    /**
     * Returns current output schedule.
     * @return current output schedule
     */
    public Solution getSolution() {
        return _solution;
    }

    /**
     * Sets the output schedule
     * @param solution output schedule
     */
    public void setSolution(Solution solution) {
        _solution = solution;
        if ((_solution.getNumTasks() > _currentBestSchedule.getNumTasks()) || (_solution.getNumTasks() == _currentBestSchedule.getNumTasks() && _solution.getFinishTime() < _currentBestSchedule.getFinishTime())){
            _currentBestSchedule = _solution;
        }
    }

    public Solution getcurrentBestSchedule(){
        return _currentBestSchedule;
    }

    /**
     * Returns the current instance of the class and creates one, if there is no
     * existing instance
     * @return instance of the class
     */
    public static AlgoObservable getInstance()
    {
        if (single_instance == null)
            single_instance = new AlgoObservable();

        return single_instance;
    }
}
