package raspberry.scheduler.cli.exception;

/**
 * This class handles the special exceptions in the CLIParser class, not handled by
 * the other excpetions.
 */
public class ParserException extends Exception{
    public String _message;

    /**
     * Returns error message.
     * @param message message returned
     */
    public ParserException(String message){
        _message = message;
    }

    /**
     * Gets error message.
     * @return message
     */
    @Override
    public String getMessage() {
        return _message;
    }
}
