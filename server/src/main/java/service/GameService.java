package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import model.AuthData;
import model.GameData;
import model.JoinRequest;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Map;
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
            throw new AuthException("Error: Not authorized");
        }
        if(gameName == null){
            throw new UserException("Error: Bad Request");
        }
        try {
            ArrayList<GameData> games = gameDB.listGames();
            if(games == null){
                GameData newGame = new GameData(1, null, null, gameName, new ChessGame());
                gameDB.createGame(newGame);
                return newGame;
            }
            for (GameData game : games) {
                if (Objects.equals(game.gameName(), gameName)) {
                    throw new UserException("Error: That Game Name is Taken");
                }
            }
            GameData newGame = new GameData(games.size()+1, null, null, gameName, new ChessGame());
            gameDB.createGame(newGame);
            return newGame;
        }catch(AuthException e){
            throw new AuthException(e.getMessage());
        }catch(DataAccessException e){
            try {
                if (e.getMessage() == "Error: There are no Games") {
                    GameData newGame = new GameData(1, null, null, gameName, new ChessGame());
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

    public GameData joinGame(String authToken, JoinRequest req) throws UserException, AuthException, JoinException {
        try{
            AuthData auth = aServe.getAuth(authToken);
            if(auth == null){
                throw new AuthException("Error: Unauthorized");
            }
            if(req.playerColor() == null){
                throw new UserException("Error: bad request");
            }
            return gameDB.joinGame(req.playerColor().toUpperCase(), req.gameID(), auth);
        }catch(UserException e){
            throw new UserException("Error: You are not logged in.");
        } catch (AuthException e){
            throw new AuthException(e.getMessage());
        }catch(JoinException e){
            throw new JoinException(e.getMessage());
        }
        catch (DataAccessException e) {
            throw new UserException(e.getMessage());
        }

    }

    public ArrayList<GameData> getGames(String authToken) throws UserException, AuthException {
        try {
            AuthData auth = aServe.getAuth(authToken);
            if(auth == null){
                throw new AuthException("Error: unauthorized");
            }
            ArrayList<GameData> games = gameDB.listGames();
            return games;
        } catch (UserException e) {
            throw new UserException("Error: You are not Authorized");
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
