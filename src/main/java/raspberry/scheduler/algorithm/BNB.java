package main.java.raspberry.scheduler.algorithm;

import main.java.raspberry.scheduler.Main;
import main.java.raspberry.scheduler.graph.Edge;
import main.java.raspberry.scheduler.graph.Graph;
import main.java.raspberry.scheduler.graph.Node;

import java.util.*;

public class BNB implements Algorithm{

    Graph graph;
    PriorityQueue<Schedule> pq;
    int numP;
    List<Schedule> visited;
    Stack<Schedule> scheduleStack;
    int bound;

    public BNB(Graph graphToSolve, int numProcessors){
        graph = graphToSolve;
        pq = new PriorityQueue<Schedule>();
        visited = new ArrayList<Schedule>();
        numP = numProcessors;
    }

    @Override
    public void findPath() {
        // DO dfs , get the "min"
        // Do dfs while the cost is lower than "min"

        this.bound = Integer.MAX_VALUE; /// Set init bound to infinity.
        BNB_DFS();
    }

    public void BNB_DFS(){
        //Compute topological order and return it.
        Schedule shortestPath = null;
        // Stack - Keeps track of all available/scheduable tasks.
        scheduleStack = new Stack<Schedule>();
        Hashtable<Node, Integer> rootTable = getRootTable();
        Hashtable<Schedule, Hashtable<Node, Integer>> master = new Hashtable<Schedule, Hashtable<Node, Integer>>();
        printTable(rootTable);

        for (Node i : rootTable.keySet()){
            if (rootTable.get(i) == 0 ) {
                Schedule newSchedule = new Schedule(0, null, i, 0);
                master.put(newSchedule, getChildTable(rootTable, i));
                scheduleStack.push(newSchedule);
            }
        }
        Schedule cSchedule;
        Hashtable<Node, Integer> cTable;
        while (true){
            cSchedule = scheduleStack.pop();
            cTable = master.get(cSchedule);
            master.remove(cSchedule);

            if (cSchedule.latest >= this.bound){
                // Bounded. Meaning, this current schedule is too slow.
                // We already know better schedule so ignore.
            }else{
                if (cSchedule.size == Main.NUM_NODE){
                    if (cSchedule.latest < this.bound){
                        this.bound = cSchedule.latest;
                        shortestPath = cSchedule;
                        System.out.printf("BOUND : %d\n",this.bound);
                    }
                }else {
                    for (Node i : cTable.keySet()) {
                        if (cTable.get(i) == 0) {
                            for (int j = 0; j < numP; j++) {
                                int start = calculateCost(cSchedule, j, i);
                                Schedule newSchedule = new Schedule(start, cSchedule, i, j);
                                Hashtable<Node, Integer> newTable = getChildTable(cTable,i);
                                master.put(newSchedule, newTable);
                                scheduleStack.push(newSchedule);
                            }
                        }
                    }
                }
            }

            if (scheduleStack.isEmpty()){
                System.out.println("-- BOUND_DFS FINISHED --");
                break;
            }
        }
        printPath(shortestPath);
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
            // TODO : Add parent counter. Can break after checking all parents.
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

    public void printTable(Hashtable<Node, Integer> x){
        System.out.print("\n{");
        for (Node i : x.keySet()){
            System.out.printf( " %c_%d ,", i._id, x.get(i) );
        }
        System.out.println("}");
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
//            System.out.printf("%c_%c_ in-edge : %d\n",x._id, i.childNode._id, tmp.get(i.childNode));
            tmp.replace( i.childNode,  tmp.get(i.childNode) - 1 );
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
}
