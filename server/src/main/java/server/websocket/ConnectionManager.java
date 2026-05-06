package server.websocket;

import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();

    public void add(Session session) {
        connections.put(session, session);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(Session excludeSession, ServerMessage notification, Object var) throws IOException {
        String msg = "";
        if(notification.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            Notification message = (Notification) notification;
            msg = new Gson().toJson(message);
        }
        if(notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME){
            GameData game = (GameData)var;
            LoadMessage messi = new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            msg = new Gson().toJson(messi);
        }
        if(notification.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){
            ErrorMessage message = (ErrorMessage) notification;
            msg = new Gson().toJson(message);
        }

        for (Session c : connections.values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}