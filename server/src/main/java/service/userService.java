package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;
import server.websocket.AuthGenerator;


public class userService {


    public static UserData register(UserData user) throws UserException{
        try {
            UserData existUser = DataAccess.getUser(user.username());
            if(existUser != null){
                throw new UserException("That username is taken");
            }
            else{
                DataAccess.addUser(user);
                return user;
            }
        }catch (DataAccessException error){
            throw new UserException(error.getMessage());
        }

    }
    public static AuthData login(LoginRequest user) throws UserException {
        try {
            UserData existUser = DataAccess.getUser(user.username());
            if(existUser == null){
                throw new UserException("There is no account with that username");
            }
            else if(existUser.password() == user.password()){
                AuthData userAuth = AuthGenerator.genAuth(user.username());
                DataAccess.createAuth(userAuth);
                return userAuth;
            }
        }catch (DataAccessException error){
            throw new UserException(error.getMessage());
        }
    }
}
