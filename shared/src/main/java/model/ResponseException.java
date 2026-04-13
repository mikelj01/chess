package model;

public class ResponseException extends RuntimeException {
    public ResponseException(int status, String message) {
        super(message);
    }
}
