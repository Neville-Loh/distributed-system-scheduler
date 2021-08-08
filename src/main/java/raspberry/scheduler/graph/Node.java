package raspberry.scheduler.graph;


public class Node implements INode{

    private String name;
    private int weight;

    public Node(String name, int executionTime){
        this.name = name;
        this.weight = executionTime;
    }

    @Override
    public int getValue() {
        return weight;
    }

    @Override
    public String toString(){
        return this.name;
    }

    public String getName(){
        return this.name;
    }
}