package raspberry.scheduler.algorithm;

import java.util.*;

import raspberry.scheduler.graph.*;

// TODO : Replace, Main.NUM_NODE with some other variable.
import raspberry.scheduler.Main;

public class Astar2 implements Algorithm{

    private IGraph graph;

    PriorityQueue<Schedule2> pq;
    int numP;
    Hashtable<Integer,ArrayList<Schedule2>> visited;
    int numNode;
    Hashtable<String, Integer> heuristic = new Hashtable<String, Integer>();

    int maxCriticalPath;

    public Astar2(IGraph graphToSolve, int numProcessors){
        this.graph = graphToSolve;
        pq = new PriorityQueue<Schedule2>();
        visited = new Hashtable<Integer,ArrayList<Schedule2>>();

        numP = numProcessors;
        numNode = graph.getNumNodes();
        System.out.printf("NUM_NODE : %d\n", numNode);
        System.out.printf("NUM_P    : %d\n", numP);
    }

    @Override
    public OutputSchedule findPath() {
        // find the path
        // "master" stores, schedule and its counterTable.
        // "rootTable" is the table all counterTable is based of off.
        //  --> stores a node and number of incoming edges.
        getH();

        Hashtable<Schedule2, Hashtable<INode, Integer>> master = new Hashtable<Schedule2, Hashtable<INode, Integer>>();
        Hashtable<INode, Integer> rootTable = this.getRootTable();

        for (INode i: rootTable.keySet()){
            if (rootTable.get(i) == 0 ){
                Schedule2 newSchedule = new Schedule2( 0,
                        Collections.max(Arrays.asList(
                                h(i.getName()),
                                h1(rootTable, i.getValue()),
                                maxCriticalPath-i.getValue(),
                                h2(rootTable, 0,i.getValue(), null)
                        )),
                        null, i, 1 );
                master.put(newSchedule,getChildTable(rootTable,i));
                pq.add(newSchedule);
            }
        }

        int duplicate = 0;
        System.out.print("\n=== WHILE LOOP ===");
        Schedule2 cSchedule;
        while (true){
//            System.out.printf("\n PQ SIZE :  %d", pq.size());
            cSchedule = pq.poll();
            ArrayList<Schedule2> listVisitedForSize = visited.get(cSchedule.getHash());

            if ( listVisitedForSize != null && listVisitedForSize.contains(cSchedule) ) {
                duplicate++;
                continue;
            }else{
                if (listVisitedForSize == null){
                    listVisitedForSize = new ArrayList<Schedule2>();
                }
                listVisitedForSize.add(cSchedule);
                visited.put(cSchedule.getHash(),listVisitedForSize);
            }


            //todo replace num_node,Main.NUM_NODE
            if (cSchedule.size == numNode){
                break;
            }
            Hashtable<INode, Integer> cTable = master.get(cSchedule);
            master.remove(cSchedule);
            int currentMaxPid = cSchedule.maxPid;
            int pidBound;  // Schedule.pid -> 1~ n,
                            // numP -> 1~n
            if (currentMaxPid+1 > numP){
                pidBound = numNode;
            }else{
                pidBound = currentMaxPid + 1;
            }

            for (INode node: cTable.keySet()){
                if (cTable.get(node) == 0 ){
                    //TODO : Make it so that if there is multiple empty processor, use the lowest value p_id.
                    for (int j=1; j<=pidBound; j++){
                        int start = calculateCost(cSchedule, j, node);
                        Schedule2 x = new Schedule2(start , cSchedule, node, j);
                        Hashtable<INode, Integer> newTable = getChildTable(cTable,node);
                        Schedule2 newSchedule = new Schedule2(
                                start,
                                Collections.max(Arrays.asList(
                                        h(node.getName()),
                                        h1(newTable, start+node.getValue()),
                                        maxCriticalPath-start-node.getValue(),
                                        h2(newTable, start,node.getValue(), cSchedule)
                                )),
                                cSchedule, node, j );
                        master.put(newSchedule,newTable);
                        pq.add(newSchedule);
                    }
                }
            }
        }

        System.out.print("\n === THE FINAL ANSWER ===\n");
        System.out.printf("NUM VISITED NODE   : %d\n", visited.size());
        System.out.printf("NUM DUPLICATE NODE : %d\n", duplicate);
        System.out.printf("NUM PQ NODE        : %d", pq.size());

        printPath(cSchedule);
        return new Solution(cSchedule,numP);
    }

    // Compute heuristic weight
    // Currently our heuristic function is undecided. --> just returns 0.
    public int h(String s){
        return heuristic.get(s);
    }

    public int h1(Hashtable<INode, Integer> x ,int finishTime){
        int sum = finishTime;
        for (INode i: x.keySet()){
            sum += i.getValue();
        }
        return sum/numP - finishTime;
    }

    public int h2(Hashtable<INode, Integer> x, int start, int cost, Schedule2 parent){
        int sum = cost;
        for ( int i=0; i<numP; i++){
            sum += getLastPTime(parent, i);
        }
        for (INode i: x.keySet()){
            sum += i.getValue();
        }
        int spreadOutTime =  sum/numP;
        return spreadOutTime-start-cost;
    }

    public int getLastPTime(Schedule2 cParentSchedule, int processorId){
        while ( cParentSchedule != null){
            if ( cParentSchedule.p_id == processorId ){
                return cParentSchedule.finishTime;
            }
            cParentSchedule = cParentSchedule.parent;
        }
        return 0;
    }


    public int calculateCost(Schedule2 parentSchedule, int processorId, INode nodeToBeSchedule) {
        // Find last finish parent node
        // Find last finish time for current processor id.
        Schedule2 last_processorId_use = null; //last time processor with "processorId" was used.
        Schedule2 cParentSchedule = parentSchedule;

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
                tmp.replace( j.getChild(), tmp.get(j.getChild()) + 1);
            }
        }
        return tmp;
    }

    public Hashtable<INode, Integer> getChildTable(Hashtable<INode, Integer> parentTable, INode x){
        Hashtable<INode, Integer> tmp = new Hashtable<INode, Integer>(parentTable);
        tmp.remove(x);

//        System.out.println(this.graph.getOutgoingEdges(x.getName()));
//        System.out.println(graph.toString());

        for (IEdge i : this.graph.getOutgoingEdges(x.getName())){
            tmp.replace( i.getChild(),  tmp.get(i.getChild()) - 1 );
        }
        return tmp;
    }

    public void printPath(Schedule2 x){
        System.out.println("");
        Hashtable<INode, int[]> path = x.getPath();

        //path.sort((o1, o2) -> o1.node.getName().compareTo(o2.node.getName()));
        for (INode i: path.keySet()){
            System.out.printf("%s : {start:%d}, {finish:%d}, {p_id:%d} \n",
                    i.getName(), path.get(i)[0], path.get(i)[1], path.get(i)[2]);
        }
    }


    public void printHashTable(Hashtable<INode, Integer> table){
        System.out.printf("{ ");
        for (INode i: table.keySet()){
            System.out.printf("%s_%d, ", i.getName(), table.get(i));
        }
        System.out.printf(" }\n");
    }

    public void getH(){
        heuristic = new Hashtable<String, Integer>();
        for ( INode i : this.graph.getAllNodes()){
            heuristic.put(i.getName(), 0);
        }

        for ( INode i: this.graph.getAllNodes() ){
            heuristic.put(i.getName(), getHRecursive( i ));
        }

        for (String j: heuristic.keySet()){
            System.out.printf("%s_%d ", j, heuristic.get(j));
        }
        maxCriticalPath = Collections.max(heuristic.values());
        System.out.printf("MAX : %d\n",Collections.max(heuristic.values()));
    }

    public int getHRecursive( INode n){
        List<IEdge> e = this.graph.getOutgoingEdges(n.getName());
        if ( e.size() == 0){
            return 0;
        } else if (e.size() == 1){
            return getHRecursive(e.get(0).getChild()) + n.getValue();
        }
        int max = 0;
        for ( IEdge i : e){
            int justCost = getHRecursive(i.getChild()) + n.getValue();
            if ( max < justCost ){
                max = justCost;
            }
        }
        return max;
    }



}