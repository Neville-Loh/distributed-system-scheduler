package raspberry.scheduler.algorithm.sma;
import java.util.Collections;
import java.util.PriorityQueue;


/**
 * Implementation of a two way priority queue which make use of the
 * normal java priority queue.
 * This data structure has log(n) complexity for insertion and deletion
 * @author Neville
 */
public class TwoWayPriorityQueue{
    private PriorityQueue<MBSchedule> _ascendingFScore;
    private PriorityQueue<MBSchedule> _descendingFScore;

    /**
     * Class constructor
     */
    public TwoWayPriorityQueue(){
        _ascendingFScore = new PriorityQueue<MBSchedule>();
        _descendingFScore = new PriorityQueue<MBSchedule>(Collections.reverseOrder());
    }

    /**
     * Inserts the specified element into this priority queue.
     * @param e element whose presence in this collection is to be ensured
     * @return true (as specified by Collection.add)
     */
    public boolean add(MBSchedule e){
        return _ascendingFScore.add(e) && _descendingFScore.add(e);
    }

    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     * the head of this queue, or null if this queue is empty
     * @return MBSchedule
     */
    public MBSchedule pollMin(){
        _descendingFScore.poll();
        return _ascendingFScore.poll();
    }

    /**
     * Retrieves and removes the tail of this queue, or returns null if this queue is empty.
     * the head of this queue, or null if this queue is empty
     * @return MBSchedule
     */
    public MBSchedule pollMax(){
        _ascendingFScore.poll();
        return  _descendingFScore.poll();
    }

    /**
     * returns the number of elements in this collection. If this collection contains more
     * than Integer.MAX_VALUE elements, returns Integer.MAX_VALUE.
     * @return size of the queue
     */
    public int size(){
        return _ascendingFScore.size();
    }


    /**
     * Returns true if this queue contains the specified element.
     * More formally, returns true if and only if this queue contains at least one element e such that o.equals(e).
     * @param schedule to be search
     * @return true if this queue contains the specified element
     */
    public boolean contains(MBSchedule schedule){
        return _ascendingFScore.contains(schedule);
    }

}
