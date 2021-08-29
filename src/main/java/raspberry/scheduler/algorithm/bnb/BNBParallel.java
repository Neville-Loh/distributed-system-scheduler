package raspberry.scheduler.algorithm.bnb;

import raspberry.scheduler.algorithm.common.FixOrderChecker;
import raspberry.scheduler.algorithm.common.OutputSchedule;
import raspberry.scheduler.algorithm.common.ScheduledTask;
import raspberry.scheduler.algorithm.common.Solution;
import raspberry.scheduler.app.visualisation.model.AlgoStats;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.INode;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

public class BNBParallel extends BNB {

    private int _numCores;
    // thread pool that will deal with all the threads
    private ThreadPoolExecutor _threadPool = null;
    private List<Stack<ScheduleB>> stacks;
    private Semaphore _lock;
    private AlgoStats _algoStats;
    private FixOrderChecker _fixOrderChecker;

    public BNBParallel(IGraph graphToSolve, int numProcessors, int bound, int numCores) {
        super(graphToSolve, numProcessors, bound);
        initialiseThreadPool(numCores);
        _numCores = numCores;
        _lock = new Semaphore(1);

        // Stack - Keeps track of all available/scheduable tasks.
        stacks = new ArrayList<Stack<ScheduleB>>();
        for (int i=0; i<_numCores; i++){
            stacks.add( new Stack<ScheduleB>() );
        }
        _algoStats = AlgoStats.getInstance();
        _fixOrderChecker = new FixOrderChecker(_graph);
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
        _algoStats.setIterations(0);
        _algoStats.setIsFinish(false);
        _visited = new Hashtable<Integer, ArrayList<ScheduleB>>();
        Hashtable<INode, Integer> rootTable = getRootTable();
        getH();

        Stack<ScheduleB> rootSchedules = getRootSchedules();
        if (rootSchedules.isEmpty()){
            _algoStats.setSolution(new Solution(shortestPath, _numP));
            return new Solution(shortestPath, _numP);
        }else{
            Collections.reverse(rootSchedules);
            int sizeOfrootSchedules = rootSchedules.size();
            for (int i=0; i< sizeOfrootSchedules; i++){
                stacks.get( i % _numCores).push( rootSchedules.pop() );
            }
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
            System.out.println("- Algorithm failed to find solution -");
        }
        _algoStats.setSolution(new Solution(shortestPath, _numP));
        _algoStats.setIsFinish(true);
        _threadPool.shutdownNow();
        return new Solution(shortestPath, _numP);
    }

    /**
     * This is the task that each thread will run.
     * @param stack : A stack, which each thread will use to do DFS with bound.
     */
    public void task(Stack<ScheduleB> stack){
        ScheduleB cSchedule;
        Hashtable<INode, Integer> cTable;
        while (true) {

            if (_visited.size() > 5000000){
                _visited.clear();
            }
            _algoStats.increment();
//            System.out.printf("Stack SIZE: %d\n", stack.size());
            if (stack.isEmpty()) {
//                System.out.println("-- BOUND_DFS FINISHED --");
                break;
            }

            cSchedule = stack.pop();
            if ( canPrune( cSchedule, true, false )){
                continue;
            }

            cTable = cSchedule.getIndegreeTable();

            if ( cSchedule.getSize() == _numNode ) {
                int totalFinishTime = cSchedule.getOverallFinishTime();
                try{
                    _lock.acquire();
                    if (totalFinishTime <= _bound) {
                        _bound = totalFinishTime;
                        shortestPath = cSchedule;
                        if( totalFinishTime < _bound ){
//                            System.out.printf("\nNEW BOUND : %d", _bound);
                        }
                    }
                    _lock.release();
                    continue;
                }catch (InterruptedException e){
                    System.out.println(e);
                    _lock.release();
                }
            }

            int currentMaxPid = cSchedule.getMaxPid();
            int pidBound;
            if (currentMaxPid + 1 > _numP) {
                pidBound = _numP;
            } else {
                pidBound = currentMaxPid + 1;
            }

            ArrayList<INode> freeNodes = new ArrayList<INode>();
            for (INode node : cTable.keySet()) {
                if (cTable.get(node) == 0) {
                    freeNodes.add(node);
                }
            }

            if ( _fixOrderChecker.check(freeNodes, cSchedule) &&
                    _fixOrderChecker.getFixOrder(freeNodes,cSchedule) != null){

                INode node = _fixOrderChecker.getFixOrder(freeNodes,cSchedule).get(0);
                for (int pid = 1; pid <= pidBound; pid++) {
                    int start = calculateEarliestStartTime(cSchedule, pid, node);
                    ScheduleB newSchedule = new ScheduleB(cSchedule,
                            new ScheduledTask(pid,node,start),
                            getChildTable(cTable,node));
                    newSchedule.addLowerBound( Math.max( lowerBound_1(newSchedule), _maxCriticalPath ) );
                    _algoStats.setSolution(new Solution(newSchedule, _numP));

                    if ( canPrune( newSchedule , false, false)){
                        continue;
                    }
                    stack.push(newSchedule);
                }
            } else {
                for (INode node : freeNodes) {
                    for (int pid = 1; pid <= pidBound; pid++) {
                        int start = calculateEarliestStartTime(cSchedule, pid, node);
                        ScheduleB newSchedule = new ScheduleB(cSchedule,
                                new ScheduledTask(pid,node,start),
                                getChildTable(cTable,node));
                        newSchedule.addLowerBound( Math.max( lowerBound_1(newSchedule), _maxCriticalPath ) );
                        _algoStats.setSolution(new Solution(newSchedule, _numP));

                        if ( canPrune( newSchedule , false, true)){
                            continue;
                        }
                        stack.push(newSchedule);
                    }
                }
            }
        }
        return;
    }

    /**
     * Get "ncore" amount of root schedule.
     * @return : Stack of Schedules
     */
    public Stack<ScheduleB> getRootSchedules(){

        Stack<ScheduleB> rootSchedules = new Stack<ScheduleB>();
        Hashtable<INode, Integer> rootTable = getRootTable();

        for (INode i : rootTable.keySet()) {
            if (rootTable.get(i) == 0) {

                ScheduleB newSchedule = new ScheduleB(new ScheduledTask(1, i,0),
                        getChildTable(rootTable, i));

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
            if (rootSchedules.isEmpty()) {
//                System.out.println("-- BOUND_DFS FINISHED --");
                break;
            }

            cSchedule = rootSchedules.pop();
            if ( canPrune( cSchedule, true , false)){
                continue;
            }
            cTable = cSchedule.getIndegreeTable();
            if ( cSchedule.getSize() == _numNode ) {
                int totalFinishTime = cSchedule.getOverallFinishTime();
                if (totalFinishTime <= _bound) {
                    _bound = totalFinishTime;
                    shortestPath = cSchedule;
                    if( totalFinishTime < _bound){
//                        System.out.printf("\nBOUND : %d", _bound);
                    }
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

            ArrayList<INode> freeNodes = new ArrayList<INode>();
            for (INode node : cTable.keySet()) {
                if (cTable.get(node) == 0) {
                    freeNodes.add(node);
                }
            }

            if ( _fixOrderChecker.check(freeNodes, cSchedule) &&
                    _fixOrderChecker.getFixOrder(freeNodes,cSchedule) != null){

                INode node = _fixOrderChecker.getFixOrder(freeNodes,cSchedule).get(0);
                for (int pid = 1; pid <= pidBound; pid++) {
                    int start = calculateEarliestStartTime(cSchedule, pid, node);
                    ScheduleB newSchedule = new ScheduleB(cSchedule,
                            new ScheduledTask(pid,node,start),
                            getChildTable(cTable,node));
                    newSchedule.addLowerBound( Math.max( lowerBound_1(newSchedule), _maxCriticalPath ) );
                    _algoStats.setSolution(new Solution(newSchedule, _numP));

                    if ( canPrune( newSchedule , false, false)){
                        continue;
                    }
                    rootSchedules.push(newSchedule);
                }
            } else {
                for (INode node : freeNodes) {
                    for (int pid = 1; pid <= pidBound; pid++) {
                        int start = calculateEarliestStartTime(cSchedule, pid, node);
                        ScheduleB newSchedule = new ScheduleB(cSchedule,
                                new ScheduledTask(pid,node,start),
                                getChildTable(cTable,node));
                        newSchedule.addLowerBound( Math.max( lowerBound_1(newSchedule), _maxCriticalPath ) );
                        _algoStats.setSolution(new Solution(newSchedule, _numP));

                        if ( canPrune( newSchedule , false, true)){
                            continue;
                        }
                        rootSchedules.push(newSchedule);
                    }
                }
            }
        }
        return rootSchedules;
    }

    /**
     * Submit a new task to thread pool.
     * @param stack : stack, each thread uses to do DFS with bound.
     * @param latch : latch to check if a thead has finished doing its job.
     */
    public void assignNewTask( Stack<ScheduleB> stack , CountDownLatch latch) {
        _threadPool.submit(() -> {
            task( stack );
            latch.countDown();
        });
    }

    /**
     * This implementation deals with concurrency issues.
     * @param scheduleList : list of visited schedule. (with same getHash() value)
     * @param cSchedule    : schedule that we are trying to find if duplicate exists of not.
     * @return
     */
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
