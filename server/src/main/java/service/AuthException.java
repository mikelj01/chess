package service;

import dataaccess.DataAccessException;

public class AuthException extends DataAccessException {
    public AuthException(String message) {
        super(message);
    }
}
