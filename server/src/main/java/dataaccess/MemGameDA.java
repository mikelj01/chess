package dataaccess;

import model.AuthData;
import model.GameData;
import service.JoinException;
import service.UserException;

import java.util.ArrayList;
import java.util.Objects;

public class MemGameDA implements GameDataAccess {

    ArrayList<GameData> games;

    public MemGameDA(){
        this.games = new ArrayList<>();
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
    games.remove(gameID);
    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        games.add(game);
        return game;
    }

    @Override
    public GameData joinGame(String color, int gameID, AuthData auth) throws DataAccessException {

        try{
            if(Objects.equals(color, "WHITE")){
            GameData game = games.get(gameID-1);
            if(game.whiteUsername() != null){
                throw new JoinException("Error: That seat is already taken");
            }
            GameData newGame = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
            games.set(gameID-1, newGame);
            return newGame;
            }
            if(Objects.equals(color, "BLACK")){
                GameData game = games.get(gameID-1);
                if(game.blackUsername() != null){
                    throw new JoinException("Error: That seat is already taken");
                }
                GameData newGame = new GameData(game.gameID(),game.whiteUsername(), auth.username() , game.gameName(), game.game());
                games.set(gameID-1, newGame);
                return newGame;
            }
            throw new UserException("Error: Bad Request");
        }catch(IndexOutOfBoundsException e){
            throw new DataAccessException("Error: That game doesn't exist");
        }
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return games;
    }

    public void clear() throws DataAccessException {
        try {
            games.clear();
        }catch(Exception e){
            throw new DataAccessException("Error: There was an error accessing the Database.");
        }
    }
}
