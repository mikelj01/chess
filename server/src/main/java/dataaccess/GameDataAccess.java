package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public interface GameDataAccess {
    void deleteGame(int gameID) throws DataAccessException;

    GameData createGame(GameData game) throws DataAccessException;

    GameData joinGame(String color, int gameID, AuthData auth) throws DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;

    void clear() throws DataAccessException;
}
