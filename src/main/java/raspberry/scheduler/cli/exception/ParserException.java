package raspberry.scheduler.cli.exception;

/**
 *
 */
public class ParserException extends Exception{
    public String _message;

    /**
     *
     * @param message
     */
    public ParserException(String message){
        _message = message;
    }

    @Override
    public String getMessage() {
        return _message;
    }
}
