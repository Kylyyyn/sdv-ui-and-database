package controller;

// incorrect username or password

public class AuthenticationException extends Exception {

    private String errorCode;

    public AuthenticationException() {
        super();
    }

    public AuthenticationException(String message) {
        super(message);
        this.errorCode = "";
    }

    public AuthenticationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode != null ? errorCode : "";
    }
    
}