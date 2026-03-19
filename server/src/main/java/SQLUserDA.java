import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import model.UserData;

public class SQLUserDA implements UserDataAccess {
    @Override
    public UserData getUser(String userName) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteUser(String userName) throws DataAccessException {

    }

    @Override
    public UserData addUser(UserData user) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {

    }
}
