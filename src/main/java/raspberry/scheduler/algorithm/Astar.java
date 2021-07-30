package main.java.raspberry.scheduler.algorithm;

import java.util.*;

import main.java.raspberry.scheduler.graph.*;

// TODO : Replace, Main.NUM_NODE with some other variable.
import main.java.raspberry.scheduler.Main;

public class Astar implements Algorithm{

    private IGraph graph;

    PriorityQueue<Schedule> pq;
    int numP;
    List<Schedule> visited;

    public Astar(IGraph graphToSolve, int numProcessors){
        this.graph = graphToSolve;
        pq = new PriorityQueue<Schedule>();
        visited = new ArrayList<Schedule>();
        numP = numProcessors;
    }

    @Override
    public void findPath() {
        // find the path
        // "master" stores, schedule and its counterTable.
        // "rootTable" is the table all counterTable is based of off.
        //  --> stores a node and number of incoming edges.
        Hashtable<Schedule, Hashtable<INode, Integer>> master = new Hashtable<Schedule, Hashtable<INode, Integer>>();
        Hashtable<INode, Integer> rootTable = this.getRootTable();

        for (INode i: rootTable.keySet()){
            if (rootTable.get(i) == 0 ){
                Schedule newSchedule = new Schedule( 0, h(rootTable), null, i, 0 );
                master.put(newSchedule,getChildTable(rootTable,i));
                pq.add(newSchedule);
            }
        }
        System.out.print("ROOT TABLE :\n");
        printHashTable(rootTable);
        System.out.println("");

        for (Schedule sc: master.keySet()){
            printPath(sc);
//            printHashTable(master.get(sc));
        }

        System.out.print("\n=== WHILE LOOP ===");
        Schedule cSchedule;
        while (true){
            System.out.printf("\n PQ SIZE :  %d", pq.size());
            cSchedule = pq.poll();
            if (cSchedule.size == Main.NUM_NODE){
                break;
            }
            Hashtable<INode, Integer> cTable = master.get(cSchedule);
            master.remove(cSchedule);
            for (INode i: cTable.keySet()){
                if (cTable.get(i) == 0 ){
                    //TODO : Make it so that if there is multiple empty processor, use the lowest value p_id.
                    for (int j=0; j<numP; j++){
//                        System.out.println("\n------------");
                        int start = calculateCost(cSchedule, j, i);
                        Hashtable<INode, Integer> newTable = getChildTable(cTable,i);
                        Schedule newSchedule = new Schedule( start, h(newTable), cSchedule, i, j );
                        master.put(newSchedule,newTable);
                        pq.add(newSchedule);

//                        printPath(newSchedule);
//                        printHashTable(newTable);
                    }
                }
            }
        }

        System.out.print("\n === THE FINAL ANSWER ===");
        printPath(cSchedule);
        //return cSchedule;
    }

    // Compute heuristic weight
    // Currently our heuristic function is undecided. --> just returns 0.
    public int h(Hashtable<INode, Integer> x){
        int sum = 0;
        for (INode i: x.keySet()){
            sum += i.getValue();
        }
        return sum/numP;
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
            finished_time_of_last_parent = last_processorId_use.fisnishTime;
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
                        if (finished_time_of_last_parent < (cParentSchedule.fisnishTime + communicationWeight)){
                            finished_time_of_last_parent = cParentSchedule.fisnishTime + communicationWeight;
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
        Hashtable<INode, Integer> tmp = new Hashtable<Node, Integer>();
        for (INode i : this.graph.adjacencyList.keySet()){
            tmp.put(i,0);
        }
        for (INode i : this.graph.adjacencyList.keySet()){
            for (Edge j : this.graph.adjacencyList.get(i) ){
                tmp.put( j.childNode, tmp.get(j.childNode) + 1);
            }
        }
        return tmp;
    }

    public Hashtable<INode, Integer> getChildTable(Hashtable<INode, Integer> parentTable, INode x){
        Hashtable<INode, Integer> tmp = new Hashtable<Node, Integer>(parentTable);
        tmp.remove(x);
        for (Edge i : this.graph.getChild(x)){
            tmp.put( i.childNode,  tmp.get(i.childNode) - 1 );
        }
        return tmp;
    }

    public void printPath(Schedule x){
        System.out.println("");
        ArrayList<Schedule> path = (ArrayList<Schedule>) x.getPath();
        for (Schedule i: path){
            System.out.printf("%c : {start:%d}, {finish:%d}, {p_id:%d} \n",i.node._id,i.startTime,i.fisnishTime,i.p_id);
        }
    }

    public void printHashTable(Hashtable<INode, Integer> table){
        System.out.printf("{ ");
        for (INode i: table.keySet()){
            System.out.printf("%c_%d, ", i._id, table.get(i));
        }
        System.out.printf(" }\n");
    }
}
