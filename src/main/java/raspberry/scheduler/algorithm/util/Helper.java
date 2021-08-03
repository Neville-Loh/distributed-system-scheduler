package raspberry.scheduler.algorithm.util;

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
}
