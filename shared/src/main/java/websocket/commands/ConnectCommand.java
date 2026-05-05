package websocket.commands;

public class ConnectCommand extends UserGameCommand{
    public ConnectCommand(CommandType commandType, String Color, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
