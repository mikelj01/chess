package service;

import dataaccess.DataAccessException;

public class UserException extends DataAccessException {
    public UserException(String message){super(message);}
}
