
package raspberry.scheduler.algorithm;
import org.junit.Before;
import org.junit.Test;
import raspberry.scheduler.algorithm.sma.PriorityQueueAlpha;

import static org.junit.Assert.*;


public class TestPriorityQueue {
    private PriorityQueueAlpha<Integer> _pq;

    @Before
    public void setup(){
        _pq = populatePQ();
    }

    private PriorityQueueAlpha<Integer> populatePQ(){
        PriorityQueueAlpha<Integer> pq = new PriorityQueueAlpha<Integer>();
        pq.add(1);
        pq.add(2);
        pq.add(3);
        pq.add(4);
        pq.add(5);
        return pq;
    }


//    @Test
//    public void testPoll(){
//        _pq = new PriorityQueueAlpha<>();
//        _pq.add(3);
//        _pq.add(2);
//        _pq.add(1);
//        _pq.add(4);
//        _pq.add(5);
//        assertEquals(1, (int) _pq.poll());
//        assertEquals(2, (int) _pq.poll());
//        assertEquals(3, (int) _pq.poll());
//        assertEquals(4, (int) _pq.poll());
//        assertEquals(5, (int) _pq.poll());
//
//    }

    @Test
    public void testRemove(){
        _pq = new PriorityQueueAlpha<>();
        _pq.add(3);
        _pq.add(2);
        _pq.add(1);
        _pq.add(4);
        _pq.add(5);
        _pq.remove(4);
        _pq.remove(2);
        assertEquals(1, (int) _pq.poll());
        assertEquals(3, (int) _pq.poll());
        assertEquals(5, (int) _pq.poll());
    }


    @Test
    public void testRemoveAndAdd(){
        _pq = new PriorityQueueAlpha<>();
        _pq.add(3);
        _pq.add(2);
        _pq.add(1);
        _pq.add(4);
        _pq.add(5);
        System.out.println(_pq.indexTable);
        _pq.remove(4);
        System.out.println(_pq.indexTable);
        _pq.remove(2);
        System.out.println(_pq.indexTable);
        _pq.add(4);
        _pq.add(2);
        assertEquals(1, (int) _pq.poll());
        assertEquals(2, (int) _pq.poll());
        assertEquals(3, (int) _pq.poll());
        assertEquals(4, (int) _pq.poll());
        assertEquals(5, (int) _pq.poll());
    }


    @Test
    public void testContain(){
        PriorityQueueAlpha<String> pq = new PriorityQueueAlpha<>();
        pq.add("c");
        pq.add("b");
        pq.add("e");
        pq.add("d");
        pq.add("a");
        assertTrue(pq.contains("a"));
        assertTrue(pq.contains("b"));
        assertTrue(pq.contains("c"));
        assertTrue(pq.contains("d"));
        assertTrue(pq.contains("e"));
        assertFalse(pq.contains("f"));
        assertEquals("a", (String) pq.poll());
        assertEquals("b", (String) pq.poll());
        assertEquals("c", (String) pq.poll());
        assertEquals("d", (String) pq.poll());
        assertEquals("e", (String) pq.poll());
    }


}