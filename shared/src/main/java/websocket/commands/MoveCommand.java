package websocket.commands;
import chess.ChessMove;

public class MoveCommand extends UserGameCommand{
    public MoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        super(commandType, authToken, gameID);

    }
}
