package raspberry.scheduler.algorithm;

import raspberry.scheduler.graph.INode;

public interface OutputSchedule {


    // start time of every task
    // processor
    // finished time of the entire thing
    // number of processor


    int getTotalProcessorNum();

    int getProcessorNum(INode node);

    int getStartTime(INode node);

    int getFinishTime();

    int getNumTasks();

}
