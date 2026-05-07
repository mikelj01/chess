package client.websocket;

import ui.UI;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

public class ErrorHandler implements NotificationHandler{
    UI ui;
    @Override
    public void notify(ServerMessage notification, UI ui) {
        this.ui = ui;
        ServerMessage.ServerMessageType type = notification.getServerMessageType();
        if(type == ServerMessage.ServerMessageType.ERROR){
           ErrorMessage notific= (ErrorMessage) notification;
            sendNotification(notific);
        }
    }
    public void sendNotification(ErrorMessage message){
        String notific = message.errorMessage;
        ui.notify(notific);
    }


}

