package websocket.messages;
import model.GameData;

public class LoadMessage extends ServerMessage{
    public GameData game;
    public LoadMessage(ServerMessageType type, GameData game) {
        super(type);
        this.game=game;
    }
}
