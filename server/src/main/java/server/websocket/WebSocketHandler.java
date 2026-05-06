package server.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.*;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.Session;
import service.AuthException;
import service.UserException;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.util.HashMap;

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    AuthDataAccess authDB;
    GameDataAccess gameDB;
    public WebSocketHandler(AuthDataAccess adb, GameDataAccess gameDB){
        this.authDB = adb;
        this.gameDB = gameDB;
    }
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
                case CONNECT -> connect(message.getGameID(), ctx.session, message.getAuthToken(), message);
                case MAKE_MOVE -> move(message.getGameID(), ctx.message(), message.getAuthToken(), ctx.session);
//                case LEAVE -> leave('Game', ctx.session);
//                case RESIGN -> resign('Game', ctx.session);
            }
        } catch (IOException | DBException | DataAccessException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    public void addGame(int id){
        ConnectionManager game = new ConnectionManager();
        connections.put(id, game);
    }

    private void connect(int id, Session session, String auth, UserGameCommand msg) throws IOException, DBException, DataAccessException {
        try {
            ConnectionManager connection = connections.get(id);
            if(connection == null){
                throw new UserException("Error: Bad Game ID");
            }
            //get auth and match
            AuthData authorization = authDB.getAuth(auth);
            if (authorization != null) {
                connection.add(session);
                Notification message = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, authorization.username() + " has joined");
                connection.broadcast(session, message, null);
                sendGame(gameDB.getGame(id), session);
            } else {
                throw new AuthException("Error, not authorized");
            }
        } catch (Exception e) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void sendGame(GameData game, Session session) throws IOException {
        LoadMessage message = new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME,game);
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void move(int id, String message, String authToken, Session session) throws DataAccessException, DBException, IOException {
        ConnectionManager connection = connections.get(id);
        try {
            MoveCommand command = new Gson().fromJson(message, MoveCommand.class);
            ChessMove move = command.move;
            GameData gameDat = gameDB.getGame(id);
            ChessGame game = gameDat.game();
            ChessBoard board = game.getBoard();
            AuthData auth = authDB.getAuth(authToken);
            String userName = "";
            if(auth != null){
                userName = auth.username();
            }else{
                throw new AuthException("Error: Unauthorized");
            }
            String bkUser = gameDat.blackUsername();
            String wtUser = gameDat.whiteUsername();
            try {
                GameData newGame = gameDat;
                if(userName != null && userName.equals(wtUser)) {
                    if (board.getPiece(move.getStartPosition()).getTeamColor() == ChessGame.TeamColor.WHITE){
                        game.makeMove(move);
                        gameDB.deleteGame(id);
                        newGame = gameDB.createGame(gameDat);
                        connection.broadcast(null, new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, newGame), newGame);
                        connection.broadcast(session, new Notification(ServerMessage.ServerMessageType.NOTIFICATION, userName + " Made a move"), userName + " Made a move" + move);
                        if(newGame.game().isInCheckmate(ChessGame.TeamColor.BLACK)){
                            connection.broadcast(null, new Notification(ServerMessage.ServerMessageType.NOTIFICATION, userName + " Made a move"), userName + " Wins!");
                        }
                    }
                } else if (userName != null && userName.equals(bkUser)) {
                    if (board.getPiece(move.getStartPosition()).getTeamColor() == ChessGame.TeamColor.BLACK){
                        game.makeMove(move);
                        gameDB.deleteGame(id);
                        newGame = gameDB.createGame(gameDat);
                        connection.broadcast(null, new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, newGame), newGame);
                        connection.broadcast(session, new Notification(ServerMessage.ServerMessageType.NOTIFICATION, userName + " Made a move"), userName + " Made a move" + move);
                        if(newGame.game().isInCheckmate(ChessGame.TeamColor.WHITE)){
                            connection.broadcast(null, new Notification(ServerMessage.ServerMessageType.NOTIFICATION, userName + " Made a move"), userName + " Wins!");
                        }
                    }
                } else{
                    throw new InvalidMoveException("You Can't Move That");
                }


            } catch (InvalidMoveException e) {
                if()
                String msg = new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, userName + " is a cheaty McCheatyface"));
                session.getRemote().sendString(msg);
                //connection.broadcast(null, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg), msg);
            }
        } catch (AuthException e){
            ErrorMessage eMess = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(new Gson().toJson(eMess));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



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