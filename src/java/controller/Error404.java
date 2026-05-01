package controller;

// tries to access an invalid URL / context path

public class Error404 extends Exception {

    public Error404() {
        super();
    }

    public Error404(String message) {
        super(message);
    }
}