package raspberry.scheduler.graph.exceptions;

public class EdgeDoesNotExistException extends Exception{
    public EdgeDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
