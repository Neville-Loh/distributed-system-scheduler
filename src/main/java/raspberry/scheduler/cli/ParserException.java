package raspberry.scheduler.cli;

public class ParserException extends Exception{
    public String _message;

    public ParserException(String message){
        _message = message;
    }

}
