package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.util.HashMap;

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final HashMap<Integer, ConnectionManager> connections = new HashMap<>();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand message = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (message.getCommandType()) {
                case CONNECT -> connect(message.getGameID(), ctx.session);
//                case MAKE_MOVE -> move('Game', 'Move', ctx.session);
//                case LEAVE -> leave('Game', ctx.session);
//                case RESIGN -> resign('Game', ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }


    private void connect(int id, Session session, String auth) throws IOException {
        ConnectionManager connection = connections.get(id);
        connection.add(session);

        Notification message = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username + " has joined");
        connection.broadcast(session,message);
    }

//    private void move(GameData gameDat, ChessMove move) {
//        ChessGame game = gameDat.game();
//        int id = gameDat.gameID();
//        ConnectionManager connection = connections.get(id);
//        try {
//            game.makeMove(move);
//            connection.broadcast(session,"notification");
//        } catch (InvalidMoveException e) {
//
//        }
//    }
//
//    private void leave(GameData game, Session session){
//        int id = game.gameID();
//        ConnectionManager connection = connections.get(id);
//        connection.remove(session);
//        connection.broadcast(session,"notification");
//    }
//
//    private void resign(){
//
//    }

}