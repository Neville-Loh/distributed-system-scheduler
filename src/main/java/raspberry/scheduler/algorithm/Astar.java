package main.java.raspberry.scheduler.algorithm;

import java.util.*;

import main.java.raspberry.scheduler.graph.Graph;
import main.java.raspberry.scheduler.graph.Node;
import main.java.raspberry.scheduler.graph.Edge;

// TODO : Replace, Main.NUM_NODE with some other variable.
import main.java.raspberry.scheduler.Main;

public class Astar implements Algorithm{

    Graph graph;
    PriorityQueue<Schedule> pq;
    List<Node> visted;
    int numP;
    List<Schedule> visited;

    public Astar(Graph graphToSolve, int numProcessors){
        graph = graphToSolve;
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
        Hashtable<Schedule, Hashtable<Node, Integer>> master = new Hashtable<Schedule, Hashtable<Node, Integer>>();
        Hashtable<Node, Integer> rootTable = this.getRootTable();

        for (Node i: rootTable.keySet()){
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
            Hashtable<Node, Integer> cTable = master.get(cSchedule);
            master.remove(cSchedule);
            for (Node i: cTable.keySet()){
                if (cTable.get(i) == 0 ){
                    //TODO : Make it so that if there is multiple empty processor, use the lowerest value p_id.
                    for (int j=0; j<numP; j++){
//                        System.out.println("\n------------");
                        int start = calculateCost(cSchedule, j, i);
                        Hashtable<Node, Integer> newTable = getChildTable(cTable,i);
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
    // Currently our heurstic function is undecided. --> just returns 0.
    public int h(Hashtable<Node, Integer> x){
        int sum = 0;
        for (Node i: x.keySet()){
            sum += i.getWeight();
        }
        return sum;
    }

    public int calculateCost(Schedule parentSchedule, int processorId, Node childNode){
        // Find last finish parent node
        // Find last finish time for current processor id.
        Schedule last_processorId_use = null; //last time processor with "processorId" was used.
        Schedule cParentSchedule = parentSchedule;
        while ( cParentSchedule != null){
            if ( cParentSchedule.p_id == processorId ){
                last_processorId_use = cParentSchedule;
                break;
            }
            cParentSchedule = cParentSchedule.parent;
        }


        //last time parent was used. Needs to check for all processor.
        int last_parent=0;
        if (last_processorId_use != null){
            last_parent = last_processorId_use.f;
        }

        Boolean [] last_parent_processor = new Boolean[this.numP];
        cParentSchedule = parentSchedule;
        while ( cParentSchedule != null){
            if ( !Arrays.asList(last_parent_processor).contains(null) ){
                break;
            }

            for ( Edge j: graph.adjacencyList.get( cParentSchedule.child ) ){
                if (j.childNode == childNode && cParentSchedule.p_id != processorId){
                    last_parent_processor[ cParentSchedule.p_id ] = true;
                    if (last_parent < (cParentSchedule.f + childNode.parentCommunicationWeight.get(cParentSchedule.child))){
                        last_parent = cParentSchedule.f + childNode.parentCommunicationWeight.get(cParentSchedule.child);
                    }
                }
            }
            cParentSchedule = cParentSchedule.parent;
        }
        return last_parent;
    }

    public Hashtable<Node, Integer> getRootTable(){
        Hashtable<Node, Integer> tmp = new Hashtable<Node, Integer>();
        for (Node i : this.graph.adjacencyList.keySet()){
            tmp.put(i,0);
        }
        for (Node i : this.graph.adjacencyList.keySet()){
            for (Edge j : this.graph.adjacencyList.get(i) ){
                tmp.put( j.childNode, tmp.get(j.childNode) + 1);
            }
        }
        return tmp;
    }

    public Hashtable<Node, Integer> getChildTable(Hashtable<Node, Integer> parentTable, Node x){
        Hashtable<Node, Integer> tmp = new Hashtable<Node, Integer>(parentTable);
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
            System.out.printf("%c : {start:%d}, {finish:%d}, {p_id:%d} \n",i.child._id,i.s,i.f,i.p_id);
        }
    }

    public void printHashTable(Hashtable<Node, Integer> table){
        System.out.printf("{ ");
        for (Node i: table.keySet()){
            System.out.printf("%c_%d, ", i._id, table.get(i));
        }
        System.out.printf(" }\n");
    }
}
