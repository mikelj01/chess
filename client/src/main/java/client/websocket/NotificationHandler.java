package client.websocket;

import ui.UI;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage notification, UI ui);
}
