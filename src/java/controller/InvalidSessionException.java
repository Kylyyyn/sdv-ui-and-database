package controller;

public class InvalidSessionException extends Exception {

    public InvalidSessionException() {
        super();
    }

    public InvalidSessionException(String message) {
        super(message);
    }
}