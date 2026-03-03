package dataaccess;

import model.AuthData;
import model.GameData;

public interface GameDataAccess {
    void deleteGame(int gameID) throws DataAccessException;
    GameData crateGame(GameData game) throws DataAccessException;
    GameData joinGame(int gameID, AuthData auth) throws DataAccessException;
}
