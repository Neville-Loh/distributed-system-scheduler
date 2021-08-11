package raspberry.scheduler.algorithm.sma;
import java.util.ArrayList;
import java.util.Collection;
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
        MBSchedule schedule = _ascendingFScore.poll();
        _descendingFScore.remove(schedule);
        return schedule;
    }

    /**
     * Retrieves and removes the tail of this queue, or returns null if this queue is empty.
     * the head of this queue, or null if this queue is empty
     * @return MBSchedule
     */
    public MBSchedule pollMax(){
        MBSchedule schedule = _descendingFScore.poll();
        _ascendingFScore.remove(schedule);
        return  schedule;
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


    /**
     * Adds all of the elements in the specified collection to this queue.
     * Attempts to addAll of a queue to itself result in IllegalArgumentException.
     * Further, the behavior of this operation is undefined if the specified collection
     * is modified while the operation is in progress.
     * @see java.util.PriorityQueue
     * @param collection collections of MSchedule
     */
    public void addAll(Collection<MBSchedule> collection){
        _ascendingFScore.addAll(collection);
        _descendingFScore.addAll(collection);
    }


    public void remove(MBSchedule schedule){
        _ascendingFScore.remove(schedule);
        _descendingFScore.remove(schedule);
    }

    /**
     * Get Method
     * Return the ascending priority queue
     * @return priorityQueue
     */
    public PriorityQueue<MBSchedule> getPQ(){
        return _ascendingFScore;
    }

    /**
     * String method, return a string
     * where each line represent an item of the prioirty queue
     * @return outputString with specified format
     */
    @Override
    public String toString(){
        ArrayList<MBSchedule> list = new ArrayList<MBSchedule>();
        list.addAll(_ascendingFScore);
        Collections.sort(list);
        String result = "---------------------------------\n"
                + "PQ SIZE: " + _ascendingFScore.size() + "\n";

        for (MBSchedule mbSchedule : list) {
            result += mbSchedule + " forgotten: " + mbSchedule.getForgottenTable()+ "\n";
        }
        return result + "---------------------------------";
    }
}