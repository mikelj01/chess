package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import model.AuthData;
import model.GameData;
import model.JoinRequest;

import java.util.ArrayList;

public class GameService {

    GameDataAccess gameDB;
    AuthService aServe;

    public GameService(GameDataAccess gameDB, AuthService aServe){
        this.gameDB = gameDB;
        this.aServe = aServe;
    }

    public GameData newGame (String gameName, String authToken)throws UserException{
        if(aServe.getAuth(authToken)== null){
            throw new UserException("Not authorized");
        }
        try{
            ArrayList<GameData> games = gameDB.listGames();
            for(GameData game: games){
                if(game.gameName() == gameName){
                    throw  new UserException("That Game Name is Taken");
                }
            }
            GameData newGame = new GameData(games.size(),null, null, gameName, new ChessGame());
            gameDB.crateGame(newGame);
        }catch(DataAccessException e){
            throw new UserException("There was an error accessing the Data (In GameService");
        }
        return null;
    }

    public GameData joinGame(String authToken, JoinRequest req) throws UserException {
        try{
            AuthData auth = aServe.getAuth(authToken);
            return gameDB.joinGame(req.Color(), req.gameID(), auth);
        }catch(UserException e){
            throw new UserException("You are not logged in.");
        } catch (DataAccessException e) {
            throw new UserException("That Game Doesn't Exist");
        }

    }

    public ArrayList<GameData> getGames(String authToken) throws UserException {
        try {
            aServe.getAuth(authToken);
        } catch (UserException e) {
            throw new UserException("You are not Authorized");
        }
        try {
            ArrayList<GameData> games = gameDB.listGames();
            return games;
        } catch (DataAccessException e) {
            throw new UserException(e.getMessage());
        }
    }

    public void clear(){
        gameDB.clear();
    }
}
