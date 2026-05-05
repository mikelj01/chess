package websocket.messages;

public class ErrorMessage extends ServerMessage{
    public String errorMessage;
    public ErrorMessage(ServerMessageType type, String e) {
        super(type);
        this.errorMessage = e;
    }
}
