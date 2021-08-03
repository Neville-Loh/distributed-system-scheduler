package raspberry.scheduler.algorithm;

import java.util.*;
import java.lang.Math;

import raspberry.scheduler.graph.*;

// TODO : Replace, Main.NUM_NODE with some other variable.

public class MemoryBoundAStar implements Algorithm{
    private IGraph graph;
    private TwoWayPriorityQueue pq;
    private final int TOTAL_NUM_PROCESSOR;
    private List<Schedule> visited;
    private int numNode;
    private final int MAX_NUMBER_NODE = 2000;

    public MemoryBoundAStar(IGraph graphToSolve, int numProcessors){
        this.graph = graphToSolve;
        pq = new TwoWayPriorityQueue();
        visited = new ArrayList<Schedule>();
        TOTAL_NUM_PROCESSOR = numProcessors;
        numNode = graph.getNumNodes();

    }

    private static class ForgottenSchedule{
        Collection<INode> nodes;
        int fCost;
        ForgottenSchedule(int fCost){
            this.nodes = new ArrayList<INode>();
            this.fCost = fCost;
        }

        public void addNode(INode node){
            nodes.add(node);
        }

        public void update(int newFCost){
            fCost = Math.min(fCost,newFCost);
        }

        public Collection<INode> getNodes(){
            return nodes;
        }

    }



    @Override
    public OutputSchedule findPath() {
        // master table:
        // dic[scheduled] = dic[node] :integer
        // root table = parent_left[Node] : int
        Hashtable<Schedule, Hashtable<INode, Integer>> master = new Hashtable<Schedule, Hashtable<INode, Integer>>();
        Hashtable<INode, Integer> parentsLeft = this.getRootTable();
        Hashtable<Schedule, ForgottenSchedule> forgotten =  new Hashtable<Schedule, ForgottenSchedule>();

        // get all the no degree node, and create a schedule
        for (INode i: parentsLeft.keySet()){
            if (parentsLeft.get(i) == 0 ){
                Schedule newSchedule = new Schedule( 0, h(parentsLeft), null, i, 0 );
                master.put(newSchedule,getChildTable(parentsLeft,i));
                pq.add(newSchedule);
            }
        }

        System.out.print("\n=== WHILE LOOP ===");
        Schedule cSchedule;
        while (true){
            System.out.printf("\nPQ SIZE :  %d", pq.size());

            // pull current node
            cSchedule = pq.pollMin();
            parentsLeft = master.get(cSchedule);
            master.remove(cSchedule);

            // if all task is scheduled
            if (cSchedule.size == numNode){
                break;
            }

            Collection<INode> nodes;
            if (forgotten.containsKey(cSchedule)){
                nodes = forgotten.get(cSchedule).getNodes();
            } else {
                nodes = parentsLeft.keySet();
            }

            for (INode node: nodes){
                if (parentsLeft.get(node) == 0 ){
                    //TODO : Make it so that if there is multiple empty processor, use the lowest value p_id.
                    for (int numProcessor=0; numProcessor < TOTAL_NUM_PROCESSOR; numProcessor++){
                        int startTime = calculateCost(cSchedule, numProcessor, node);
                        Hashtable<INode, Integer> parentsLeftAfterSchedule = getChildTable(parentsLeft,node);
                        Schedule newSchedule = new Schedule(
                                startTime,
                                h(parentsLeftAfterSchedule),
                                cSchedule,
                                node,
                                numProcessor);
                        master.put(newSchedule,parentsLeftAfterSchedule);
                        pq.add(newSchedule);

                    }
                }
            }

            while (pq.size() > MAX_NUMBER_NODE){
                ForgottenSchedule forgottenSchedule;
                Schedule badSchedule = pq.pollMax();
                Schedule badScheduleParent = badSchedule.parent;

                // if badNode parent already forget some other node
                if (forgotten.containsKey(badScheduleParent)){
                    forgottenSchedule = forgotten.get(badScheduleParent);

                } else {
                    forgottenSchedule = new ForgottenSchedule(badSchedule.t);
                    forgotten.put(badScheduleParent,forgottenSchedule);
                }
                forgottenSchedule.addNode(badSchedule.node);
            }
        }

        System.out.print("\n === THE FINAL ANSWER ===");
        cSchedule.printPath();
        return new Solution(cSchedule,TOTAL_NUM_PROCESSOR);
    }

    // Compute heuristic weight
    // Currently our heuristic function is undecided. --> just returns 0.
    public int h(Hashtable<INode, Integer> x){
        int sum = 0;
        for (INode i: x.keySet()){
            sum += i.getValue();
        }
        return sum/TOTAL_NUM_PROCESSOR;
    }

    public int calculateCost(Schedule parentSchedule, int processorId, INode nodeToBeSchedule) {
        // Find last finish parent node
        // Find last finish time for current processor id.
        Schedule last_processorId_use = null; //last time processor with "processorId" was used.
        Schedule cParentSchedule = parentSchedule;

        //---------------------------------------- Geting start time
        // finding the first schedule that has same id
        while ( cParentSchedule != null){
            if ( cParentSchedule.p_id == processorId ){
                last_processorId_use = cParentSchedule;
                break;
            }
            cParentSchedule = cParentSchedule.parent;
        }

        //last time parent was used. Needs to check for all processor.
        int finished_time_of_last_parent=0;
        if (last_processorId_use != null){
            finished_time_of_last_parent = last_processorId_use.finishTime;
        }
        // -------------------------------------------- getting start time
        //Boolean [] last_parent_processor = new Boolean[this.numP];


        cParentSchedule = parentSchedule;
        while ( cParentSchedule != null){
            // for edges in current parent scheduled node
            INode last_scheduled_node = cParentSchedule.node;
            for ( IEdge edge: graph.getOutgoingEdges(last_scheduled_node.getName())){

                // if edge points to  === childNode
                if (edge.getChild() == nodeToBeSchedule && cParentSchedule.p_id != processorId){
                    //last_parent_processor[ cParentSchedule.p_id ] = true;
                    try {
                        int communicationWeight = graph.getEdgeWeight(cParentSchedule.node,nodeToBeSchedule);
                        //  finished_time_of_last_parent  <
                        if (finished_time_of_last_parent < (cParentSchedule.finishTime + communicationWeight)){
                            finished_time_of_last_parent = cParentSchedule.finishTime + communicationWeight;
                        }
                    } catch (EdgeDoesNotExistException e){
                        System.out.println(e.getMessage());
                    }

                }
            }
            cParentSchedule = cParentSchedule.parent;
        }
        return finished_time_of_last_parent;
    }

    public Hashtable<INode, Integer> getRootTable(){
        Hashtable<INode, Integer> tmp = new Hashtable<INode, Integer>();
        for (INode i : this.graph.getAllNodes()){
            tmp.put(i,0);
        }
        for (INode i : this.graph.getAllNodes()){
            for (IEdge j : this.graph.getOutgoingEdges(i.getName())){
                tmp.put( j.getChild(), tmp.get(j.getChild()) + 1);
            }
        }
        return tmp;
    }

    public Hashtable<INode, Integer> getChildTable(Hashtable<INode, Integer> parentTable, INode x){
        Hashtable<INode, Integer> tmp = new Hashtable<INode, Integer>(parentTable);
        tmp.remove(x);

        //System.out.println(graph.toString());
        for (IEdge i : this.graph.getOutgoingEdges(x.getName())){
            tmp.put( i.getChild(),  tmp.get(i.getChild()) - 1 );
        }
        return tmp;
    }




    public void printHashTable(Hashtable<INode, Integer> table){
        System.out.printf("{ ");
        for (INode i: table.keySet()){
            System.out.printf("%s_%d, ", i.getName(), table.get(i));
        }
        System.out.printf(" }\n");
    }
}