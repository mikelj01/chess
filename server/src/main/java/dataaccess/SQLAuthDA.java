package dataaccess;

import model.AuthData;

public class SQLAuthDA implements AuthDataAccess{
    @Override
    public AuthData createAuth(String userName) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {

    }
}
