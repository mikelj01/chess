package dataaccess;

import model.AuthData;

public interface AuthDataAccess {
    AuthData createAuth(String userName) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException, DBException;

    AuthData getAuth(String authToken) throws DataAccessException, DBException;

    void clear() throws DataAccessException;
}
