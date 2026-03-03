package dataaccess;

import model.GameData;

public interface GameDataAcess {
    void deleteGame(int gameID) throws DataAccessException;
    GameData crateGame(GameData game) throws DataAccessException;


}
