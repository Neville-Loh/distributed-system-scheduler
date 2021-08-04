package raspberry.scheduler.algorithm;

import raspberry.scheduler.graph.IEdge;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.INode;

import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class Heuristic {

    public Heuristic(){ }

    public int getH(Hashtable<String, Integer> heuristicTable, INode i,
                    Hashtable<INode, Integer> rootTable,
                    int maxCriticalPath,
                    int numP){
        return Collections.max(Arrays.asList(
                heuristicTable.get( i.getName() ),
                h1(rootTable, i.getValue(), numP),
                maxCriticalPath-i.getValue(),
                h2(rootTable, 0,i.getValue(), null, numP)));
    }

//    public int h(String s){
//        return heuristic.get(s);
//    }

    public int h1(Hashtable<INode, Integer> x , int finishTime, int numP){
        int sum = finishTime;
        for (INode i: x.keySet()){
            sum += i.getValue();
        }
        return sum/numP - finishTime;
    }

    public int h2(Hashtable<INode, Integer> x, int start, int cost, Schedule parent, int numP){
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

    public int getLastPTime(Schedule cParentSchedule, int processorId){
        while ( cParentSchedule != null){
            if ( cParentSchedule.p_id == processorId ){
                return cParentSchedule.finishTime;
            }
            cParentSchedule = cParentSchedule.parent;
        }
        return 0;
    }

    public Hashtable<String, Integer> getHeuristicTable(IGraph graph){
        Hashtable<String, Integer> heuristic = new Hashtable<String, Integer>();
        for ( INode i : graph.getAllNodes()){
            heuristic.put(i.getName(), 0);
        }

        for ( INode i: graph.getAllNodes() ){
            heuristic.put(i.getName(), getHRecursive( i , graph));
        }

        for (String j: heuristic.keySet()){
            System.out.printf("%s_%d ", j, heuristic.get(j));
        }
        return heuristic;
    }

    public int getHRecursive( INode n, IGraph graph ){
        List<IEdge> e = graph.getOutgoingEdges(n.getName());
        if ( e.size() == 0){
            return 0;
        } else if (e.size() == 1){
            return getHRecursive(e.get(0).getChild(), graph) + n.getValue();
        }
//        int min = Integer.MAX_VALUE;;
        int max = 0;
        for ( IEdge i : e){
            int justCost = getHRecursive(i.getChild(), graph) + n.getValue();
            if ( max < justCost ){
                max = justCost;
            }
        }
        return max;
    }
}
