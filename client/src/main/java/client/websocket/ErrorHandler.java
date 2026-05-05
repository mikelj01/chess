package client.websocket;

import ui.UI;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

public class ErrorHandler implements NotificationHandler{
    @Override
    public void notify(ServerMessage notification) {
        ServerMessage.ServerMessageType type= notification.getServerMessageType();
        if(type == ServerMessage.ServerMessageType.NOTIFICATION){

        }
    }
    public void sendNotification(ErrorMessage message){
        String notific = message.message;
        UI.notify(notific);
    }
}

