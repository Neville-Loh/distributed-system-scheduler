package raspberry.scheduler.graph.exceptions;

/**
 * This class handles the exception case where a edge is called for and
 * can not be found.
 */
public class EdgeDoesNotExistException extends Exception{
    public EdgeDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
