package main.java.raspberry.scheduler.graph;

public class EdgeDoesNotExistException extends Exception{
    public EdgeDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
