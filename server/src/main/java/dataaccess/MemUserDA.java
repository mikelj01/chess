package dataaccess;

import model.UserData;
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
        if(userDataMap.get(userName) == null){
            throw new DataAccessException("That user doesn't exist");
        }
        return userDataMap.get(userName);
    }

    @Override
    public void deleteUser(String userName) throws DataAccessException {
    if(userDataMap.get(userName) != null){
        userDataMap.remove(userName);
    }
    else{
        throw new DataAccessException("That Username Doesn't exist.");
    }
    }

    @Override
    public UserData addUser(UserData user) throws DataAccessException {
        String userName = user.username();
        if(userDataMap.get(userName) != null){
            throw new UserException("That username is taken");
        }
        userDataMap.put(userName, user);
        return userDataMap.get(userName);
    }

    public void clear() throws DataAccessException {
        try{
        userDataMap.clear();
        } catch (Exception e) {
            throw new DataAccessException("error accesing Database");
        }

    }
}
