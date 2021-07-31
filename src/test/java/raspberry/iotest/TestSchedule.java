package test.java.raspberry.iotest;

import main.java.raspberry.scheduler.algorithm.OutputSchedule;

public abstract class TestSchedule implements OutputSchedule{

    //must check whether there is violation of dependencies
    //check whether tasks overlap
    //check whether communication time is taken into account

    //given functions from interface:
    // start time of every task
    // processor
    // finished time of the entire thing
    // number of processor

    private int numInputTasks;
    private int numOutputTasks;
    private int startTime;
    private int nodeWeight;


    public TestSchedule(numInputTasks, numOutputTasks, startTime){
        _numInputTasks = numInputTasks;
        _numOutputTasks = numInputTasks;
        _startTime = startTime;
        _nodeWeight = nodeWeight;
    }

    //check whether all tasks are in the schedule
    public boolean allTasksPresent(int Graph, int Schedule){
        if (_numInputTasks == _numOutputTasks) {
            return true;
        }
        else {
            return false;
        }
    }


    //overlap occurs when the following happens:
    // - a child node begins execution prior to the parent node
    // - a child node begins execution while parent node is still in progress.
    // - node with no dependency relation overlap
    public boolean isValid (Graph graph, Schedule schedule){
        for (nodes.GetAllNodes()){
            parents = node.getParents();
            if (getStartTime(parent) > getStartTime(child)){
                return false;
            }
            else if ((getProcessor(Parent<node>) != getProcessor(node)) & (child.getStartTime < (edge.getWeight() + parent.getWeight() + parent.getStartTime()))){
                return false;
            }
            else if ((getProcessor(Parent<node>) == getProcessor(node)) & (){
            }
            else if {//need to account for when nodes with no dependency relation overlap{
        }
        return 1;
        }
    }

}
