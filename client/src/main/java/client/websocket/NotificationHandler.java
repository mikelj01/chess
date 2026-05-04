package client.websocket;


import webSocketMessages.messages.Notification;

public interface NotificationHandler {
    void notify(Notification notification);
}
