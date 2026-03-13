package dataaccess;

import model.UserData;
import service.AuthException;
import service.UserException;

import java.util.HashMap;
import java.util.Map;

public class MemUserDA implements UserDataAccess{
    Map<String, UserData> userDataMap;
    public MemUserDA(){
        this.userDataMap = new HashMap<>();
    }

    @Override
    public UserData getUser(String userName) throws DataAccessException {
        return userDataMap.get(userName);
    }

    @Override
    public void deleteUser(String userName) throws DataAccessException {
    if(userDataMap.get(userName) != null){
        userDataMap.remove(userName);
    }
    else{
        throw new AuthException("Error: Unauthorized");
    }
    }

    @Override
    public UserData addUser(UserData user) throws DataAccessException {
        String userName = user.username();
        if(userDataMap.get(userName) != null){
            throw new UserException("Error: That username is taken");
        }
        if(user.password() == null){
            throw new AuthException("Error: bad Request");
        }
        userDataMap.put(userName, user);
        return userDataMap.get(userName);
    }

    public void clear() throws DataAccessException {
        try{
        userDataMap.clear();
        } catch (Exception e) {
            throw new DataAccessException("Error: error accesing Database");
        }

    }
}
