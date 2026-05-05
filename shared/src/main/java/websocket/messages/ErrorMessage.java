package websocket.messages;

public class ErrorMessage extends ServerMessage{
    public String message;
    public ErrorMessage(ServerMessageType type, String e) {
        super(type);
        this.message = e;
    }
}
