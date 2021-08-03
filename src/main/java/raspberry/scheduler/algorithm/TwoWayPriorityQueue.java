package raspberry.scheduler.algorithm;

import java.util.Collections;
import java.util.PriorityQueue;

public class TwoWayPriorityQueue{
    private PriorityQueue<Schedule> _ascendingFScore;
    private PriorityQueue<Schedule> _descendingFScore;

    public TwoWayPriorityQueue(){
        _ascendingFScore = new PriorityQueue<Schedule>();
        _descendingFScore = new PriorityQueue<Schedule>(Collections.reverseOrder());
    }

    public void add(Schedule s){
        _ascendingFScore.add(s);
        _descendingFScore.add(s);
    }

    public Schedule pollMin(){
        _descendingFScore.poll();
        return _ascendingFScore.poll();
    }

    public Schedule pollMax(){
        _ascendingFScore.poll();
        return _descendingFScore.poll();
    }

    public int size(){
        return _ascendingFScore.size();
    }

}
