package dataaccess;


import model.UserData;

public interface UserDataAccess {

    UserData getUser(String userName) throws DataAccessException;

    void deleteUser(String userName) throws DataAccessException;

    UserData addUser(UserData user) throws DataAccessException;

    void clear ()throws DataAccessException;

}
