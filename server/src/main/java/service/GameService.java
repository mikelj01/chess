package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import model.AuthData;
import model.GameData;
import model.JoinRequest;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Objects;

public class GameService {

    GameDataAccess gameDB;
    AuthService aServe;

    public GameService(GameDataAccess gameDB, AuthService aServe){
        this.gameDB = gameDB;
        this.aServe = aServe;
    }

    public GameData newGame (String gameName, String authToken) throws UserException, AuthException {
        if (aServe.getAuth(authToken) == null) {
            throw new AuthException("Not authorized");
        }
        try {
            ArrayList<GameData> games = gameDB.listGames();
            for (GameData game : games) {
                if (Objects.equals(game.gameName(), gameName)) {
                    throw new UserException("That Game Name is Taken");
                }
            }
            GameData newGame = new GameData(games.size(), null, null, gameName, new ChessGame());
            gameDB.createGame(newGame);
        }catch(AuthException e){
            throw new AuthException(e.getMessage());
        }catch(DataAccessException e){
            try {
                if (e.getMessage() == "There are no Games") {
                    GameData newGame = new GameData(0, null, null, gameName, new ChessGame());
                    gameDB.createGame(newGame);
                }
                else{
                    throw new DataAccessException(e.getMessage());
                }
            }catch (DataAccessException f) {
                throw new UserException(f.getMessage());
            }
        }


        return null;
    }

    public GameData joinGame(String authToken, JoinRequest req) throws UserException {
        try{
            AuthData auth = aServe.getAuth(authToken);
            return gameDB.joinGame(req.playerColor().toUpperCase(), req.gameID(), auth);
        }catch(UserException e){
            throw new UserException("You are not logged in.");
        } catch (DataAccessException e) {
            throw new UserException("That Game Doesn't Exist");
        }

    }

    public ArrayList<GameData> getGames(String authToken) throws UserException, AuthException {
        try {
            AuthData auth = aServe.getAuth(authToken);
            if(auth == null){
                throw new AuthException(" unauthorized");
            }
            ArrayList<GameData> games = gameDB.listGames();
            return games;
        } catch (UserException e) {
            throw new UserException("You are not Authorized");
        } catch (AuthException e) {
            throw new AuthException(e.getMessage());
        }catch (DataAccessException e) {
            throw new UserException(e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        try {
            gameDB.clear();
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
