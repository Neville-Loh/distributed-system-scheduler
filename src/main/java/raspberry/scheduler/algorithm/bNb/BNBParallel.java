package raspberry.scheduler.algorithm.bNb;

import raspberry.scheduler.algorithm.OutputSchedule;
import raspberry.scheduler.algorithm.Schedule;
import raspberry.scheduler.algorithm.Solution;
import raspberry.scheduler.algorithm.common.ScheduledTask;
import raspberry.scheduler.algorithm.util.Helper;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.INode;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class BNBParallel extends BNB2{



    private int _numCores;
    // thread pool that will deal with all the threads
    private ThreadPoolExecutor _threadPool = null;

    private List<Stack<ScheduleB>> stacks;

    public BNBParallel(IGraph graphToSolve, int numProcessors, int bound, int numCores) {
        super(graphToSolve, numProcessors, bound);
        initialiseThreadPool(numCores);
        _numCores = numCores;
        // Stack - Keeps track of all available/scheduable tasks.


        stacks = new ArrayList<Stack<ScheduleB>>();
        for (int i=0; i<_numCores; i++){
            stacks.add( new Stack<ScheduleB>() );
        }
    }

    /**
     * Initialises the ThreadPool. Called in VariableScheduler
     *
     * @param numCores
     */
    public void initialiseThreadPool(int numCores) {
        _numCores = numCores;
        // Allow numParallelCores - 1 extra threads to be made
        _threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(numCores - 1);
    }

    @Override
    public OutputSchedule findPath(){
        _visited = new Hashtable<Integer, ArrayList<ScheduleB>>();
        Hashtable<INode, Integer> rootTable = getRootTable();
        getH();

        Stack<ScheduleB> rootSchedules = getRootSchedules();
        System.out.printf( "\n NUMCORE : %d, StackSize: %d \n" , _numCores, rootSchedules.size());
        if (rootSchedules.isEmpty()){
            return new Solution(shortestPath, _numP);
        }else{
            Collections.reverse(rootSchedules);
            int sizeOfrootSchedules = rootSchedules.size();
            for (int i=0; i< sizeOfrootSchedules; i++){
                stacks.get( i % _numCores).push( rootSchedules.pop() );
            }
        }
        System.out.println("");
        for (Stack<ScheduleB> i : stacks){
            System.out.printf("%d_", i.size());
        }

        CountDownLatch latch = new CountDownLatch( _numCores - 1 );
        for (int i=1; i<_numCores; i++){
            assignNewTask( stacks.get(i),  latch);
        }
        task( stacks.get(0) );
        
        try {
            latch.await();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        if (shortestPath == null){
            System.out.println("==== WTF IS WRONG WITH U");
        }
        return new Solution(shortestPath, _numP);
    }


    public void task(Stack<ScheduleB> stack){
        ScheduleB cSchedule;
        Hashtable<INode, Integer> cTable;
        while (true) {
//            System.out.printf("Stack SIZE: %d\n", stack.size());
            if (stack.isEmpty()) {
                System.out.println("-- BOUND_DFS FINISHED --");
                break;
            }

            cSchedule = stack.pop();
            if ( canPrune( cSchedule, true )){
                continue;
            }

            cTable = cSchedule.getIndegreeTable();

            if ( cSchedule.getSize() == _numNode ) {
                int totalFinishTime = cSchedule.getOverallFinishTime();
                if (totalFinishTime <= _bound) {
                    _bound = totalFinishTime;
                    shortestPath = cSchedule;
                    if( totalFinishTime < _bound ){ System.out.printf("\nNEW BOUND : %d", _bound); }
                }
                continue;
            }

            int currentMaxPid = cSchedule.getMaxPid();
            int pidBound;
            if (currentMaxPid + 1 > _numP) {
                pidBound = _numP;
            } else {
                pidBound = currentMaxPid + 1;
            }

            for (INode node : cTable.keySet()) {
                if (cTable.get(node) == 0) {
                    for (int j = 1; j <= pidBound; j++) {
                        int start = calculateCost(cSchedule, j, node);
                        ScheduleB newSchedule = new ScheduleB(cSchedule,new ScheduledTask(j,node,start),getChildTable(cTable,node));
                        newSchedule.addLowerBound( Math.max( lowerBound_1(newSchedule), _maxCriticalPath ) );

                        if ( canPrune( newSchedule , false)){
                            continue;
                        }
                        stack.push(newSchedule);
                    }
                }
            }
        }
        System.out.println(shortestPath);
        return;
    }

    // Get "ncore" amount of root schedules
    public Stack<ScheduleB> getRootSchedules(){

        Stack<ScheduleB> rootSchedules = new Stack<ScheduleB>();
        Hashtable<INode, Integer> rootTable = getRootTable();

        for (INode i : rootTable.keySet()) {
            if (rootTable.get(i) == 0) {
                ScheduleB newSchedule = new ScheduleB(
                        null, new ScheduledTask(1, i,0),getChildTable(rootTable, i));
                newSchedule.addLowerBound( Math.max(lowerBound_1(newSchedule), _maxCriticalPath) );
                if ( newSchedule.getLowerBound() > _bound ){
                    continue;
                }
                rootSchedules.push(newSchedule);
            }
        }

        ScheduleB cSchedule;
        Hashtable<INode, Integer> cTable;
        while ( rootSchedules.size() < _numCores ) {
//            System.out.printf("Stack SIZE: %d\n", _scheduleStack.size());
            if (rootSchedules.isEmpty()) {
                System.out.println("-- BOUND_DFS FINISHED --");
                break;
            }

            cSchedule = rootSchedules.pop();

            if ( canPrune( cSchedule, true )){
                continue;
            }

            cTable = cSchedule.getIndegreeTable();

            if ( cSchedule.getSize() == _numNode ) {
                int totalFinishTime = cSchedule.getOverallFinishTime();
                if (totalFinishTime <= _bound) {
                    _bound = totalFinishTime;
                    shortestPath = cSchedule;
                    if( totalFinishTime < _bound){ System.out.printf("\nBOUND : %d", _bound); }
                }
                continue;
            }

            int currentMaxPid = cSchedule.getMaxPid();
            int pidBound;
            if (currentMaxPid + 1 > _numP) {
                pidBound = _numP;
            } else {
                pidBound = currentMaxPid + 1;
            }

            for (INode node : cTable.keySet()) {
                if (cTable.get(node) == 0) {
                    for (int j = 1; j <= pidBound; j++) {
                        int start = calculateCost(cSchedule, j, node);
                        ScheduleB newSchedule = new ScheduleB(cSchedule,new ScheduledTask(j,node,start),getChildTable(cTable,node));
                        newSchedule.addLowerBound( Math.max( lowerBound_1(newSchedule), _maxCriticalPath ) );

                        if ( canPrune( newSchedule , false)){
                            continue;
                        }
                        rootSchedules.push(newSchedule);
                    }
                }
            }
        }
        return rootSchedules;
    }

    public void assignNewTask( Stack<ScheduleB> stack , CountDownLatch latch) {
        _threadPool.submit(() -> {
            task( stack );
            latch.countDown();
        });
    }

    // This implementation deals with "ConcurrentModificationException" error
    @Override
    public Boolean isIrrelevantDuplicate(ArrayList<ScheduleB> scheduleList, ScheduleB cSchedule) {
        ArrayList<ScheduleB> copyScheduleList = new ArrayList<ScheduleB>(scheduleList);
        for (ScheduleB s : copyScheduleList) {
            if ( s.equals2(cSchedule) ){
                if ( s.getLowerBound() > cSchedule.getLowerBound() ) {
//                    System.out.println("Re-opening node: Should not happen if heuristic is consistant");
                    return false;
                }else{
                    return true;
                }
            }
        }
        return false;
    }
}
