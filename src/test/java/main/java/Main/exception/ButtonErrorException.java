package Main.exception;

public class ButtonErrorException extends Exception{
    public ButtonErrorException() {
    }

    public ButtonErrorException(String message) {
        super(message);
    }

    public ButtonErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
