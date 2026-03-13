package service;

import dataaccess.DataAccessException;

public class JoinException extends DataAccessException {

    public JoinException(String message) {
        super(message);
    }
}
