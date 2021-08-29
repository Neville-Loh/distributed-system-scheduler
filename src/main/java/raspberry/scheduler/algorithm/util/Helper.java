package raspberry.scheduler.algorithm.util;

import raspberry.scheduler.algorithm.astar.ScheduleAStar;
import raspberry.scheduler.algorithm.bnb.ScheduleB;
import raspberry.scheduler.algorithm.sma.MBSchedule;
import raspberry.scheduler.graph.INode;

import java.util.ArrayList;
import java.util.Hashtable;

public class Helper {

    /**
     * Print the path of the schedule
     * @param schedule
     */
    public static void printPath(MBSchedule schedule){
        System.out.println("");
        Hashtable<INode, int[]> path = schedule.getPath();

        ArrayList<INode> list = new ArrayList(path.keySet());
        list.sort((node1, node2) -> Integer.compare(path.get(node1)[0], path.get(node2)[0]) );
        for (INode i: list){
            System.out.printf("%s : {start:%d}, {finish:%d}, {p_id:%d} \n",
                    i.getName(), path.get(i)[0], path.get(i)[1], path.get(i)[2]);
        }
    }

    public static void printPath(ScheduleB schedule){
        System.out.println(schedule);
        Hashtable<INode, int[]> path = schedule.getPath();

        ArrayList<INode> list = new ArrayList(path.keySet());
        list.sort((node1, node2) -> Integer.compare(path.get(node1)[0], path.get(node2)[0]) );
        for (INode i: list){
            System.out.printf("%s : {start:%d}, {finish:%d}, {p_id:%d} \n",
                    i.getName(), path.get(i)[0], path.get(i)[1], path.get(i)[2]);
        }
    }

    /**
     * Print the path to command line/ terminal.
     *
     * @param x : Partial schedule to print.
     */
    public static void printPath(ScheduleAStar x) {
        System.out.println("");
        Hashtable<INode, int[]> path = x.getPath();
        //path.sort((o1, o2) -> o1.node.getName().compareTo(o2.node.getName()));
        for (INode i : path.keySet()) {
            System.out.printf("%s : {start:%d}, {finish:%d}, {p_id:%d} \n",
                    i.getName(), path.get(i)[0], path.get(i)[1], path.get(i)[2]);
        }
    }

    /**
     * Uti
     * @param table
     */
    public void printHashTable(Hashtable<INode, Integer> table){
        System.out.printf("{ ");
        for (INode i: table.keySet()){
            System.out.printf("%s_%d, ", i.getName(), table.get(i));
        }
        System.out.printf(" }\n");
    }

//    /**
//     * Gets the full path of the partial schedule.
//     * (as Schedule instance is linked with parents like linked list)
//     *
//     * @return : Hashtable :  key : task (INode)
//     * Value : List of Integers. ( size of 3 )
//     * index 0 : start time of the task
//     * index 1 : finsih time of the task
//     * index 2 : processor id of the task.
//     */
//    public Hashtable<INode, int[]> getPath() {
//        Hashtable<INode, int[]> tmp;
//        if (_parent == null) {
//            tmp = new Hashtable<INode, int[]>();
//        } else {
//            tmp = _parent.getPath();
//        }
//        tmp.put(_node, new int[]{_startTime, _finishTime, _pid});
//        return tmp;
//    }

}
