package websocket.commands;

public class MoveCommand extends UserGameCommand{
    public MoveCommand(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
