package websocket.messages;

public class ERROR extends ServerMessage{
    String message;
    public ERROR(ServerMessageType type, String e) {
        super(type);
        this.message = e;
    }
}
