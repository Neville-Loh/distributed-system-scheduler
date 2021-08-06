package raspberry.scheduler.algorithm;

import raspberry.scheduler.graph.IEdge;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.INode;

import java.util.*;

public class Heuristic {

    IGraph _graph;
    Hashtable<String, Integer> _orderingHeuristic;

    public Heuristic(IGraph graph){
        _graph = graph;
    }

    //Compute heuristic based on the ordering of the task scheduling.
//    public void computeOrderingHeuristic(int numP,){
//        _orderingHeuristic = new Hashtable<String, Integer>();
//        Hashtable<String, Integer> numOutGoingEdge = getNumOutDegreeEdge();
//
//        while (!numOutGoingEdge.isEmpty()){
//            ArrayList<String> toRemove = new ArrayList<String>();
//            // Find the nodes with 0 outgoing edge and find their heuristic.
//            for ( String i: numOutGoingEdge.keySet() ){
//                if ( numOutGoingEdge.get(i) == 0 ){
//                    toRemove.add(i);
//
//                    List<IEdge> childs = _graph.getOutgoingEdges(i);
//                    if (childs.size() == 0){
//                        _orderingHeuristic.put(i, 0);
//                        return;
//                    }else {
//
//                        ArrayList<Integer> weightContainer = new ArrayList<Integer>();
//                        int numProcessorVariant = Collections.min( Arrays.asList(
//                                numP, childs.size() ) );
//
//
//                        for (IEdge j : childs) {
//                            weightContainer = r(weightContainer, childs.size(), numProcessorVariant, j);
//                        }
//                        _orderingHeuristic.put( i, Collections.min(weightContainer));
//                    }
//
//                    for (IEdge j : _graph.getIngoingEdges(i)){
//                        String parent = j.getParent().getName();
//                        numOutGoingEdge.replace( parent, numOutGoingEdge.get(parent) -1 );
//                    }
//                }
//            }
//
//            for ( String i: toRemove){
//                numOutGoingEdge.remove(i);
//            }
//        }
//    }



    public Hashtable<String, Integer> getNumOutDegreeEdge(){
        Hashtable<String, Integer> numOutGoingEdge = new Hashtable<String, Integer>();
        for ( INode i : _graph.getAllNodes()){
            numOutGoingEdge.put( i.getName(), _graph.getOutgoingEdges(i.getName()).size());
        }
        return numOutGoingEdge;
    }
}
