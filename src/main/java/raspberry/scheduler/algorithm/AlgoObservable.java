package raspberry.scheduler.algorithm;

import java.util.Observable;

public class AlgoObservable extends Observable {

    private int _iterations;
    private boolean _isFinish;
    private Solution _solution;

    private static AlgoObservable single_instance = null;

    private AlgoObservable(){
        super();
        _isFinish = false;
    }


    public void increment() {
        setChanged();
        notifyObservers(_iterations);
        _iterations++;
        if (_isFinish) {
            System.out.println("-------------FINISHED-------------");
        }
    }

    public void add(int number){
        _iterations = number;
    }

    public int getIterations() {
        return _iterations;
    }

    public void setIterations(int iterations) {
        _iterations = iterations;
    }

    public boolean getIsFinish() {
        return _isFinish;
    }

    public void setIsFinish(boolean isFinish) {
        _isFinish = isFinish;
    }

    public Solution getSolution() {
        return _solution;
    }
    public void setSolution(Solution solution) {
        _solution = solution;
    }

    public static AlgoObservable getInstance()
    {
        if (single_instance == null)
            single_instance = new AlgoObservable();

        return single_instance;
    }
}
