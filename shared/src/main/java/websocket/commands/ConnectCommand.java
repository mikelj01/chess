package websocket.commands;

public class ConnectCommand extends UserGameCommand{
    public String color;
    public ConnectCommand(CommandType commandType, String Color, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
        this.color = Color;
    }
}
