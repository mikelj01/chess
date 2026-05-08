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
import java.util.InvalidPropertiesFormatException;
import java.util.Objects;

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
                case LEAVE -> leave(message.getGameID(), ctx.message(), message.getAuthToken(), ctx.session);
                case RESIGN -> resign(message.getGameID(), ctx.message(), message.getAuthToken(), ctx.session);
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
                if(game.gameOver){
                    throw new InvalidMoveException("Game Over");
                }

                GameData newGame = gameDat;
                if(userName != null && userName.equals(wtUser)) {
                    if(game.getTeamTurn() != ChessGame.TeamColor.WHITE){
                        throw new InvalidMoveException("Not your turn");
                    }
                    if (board.getPiece(move.getStartPosition()).getTeamColor() != ChessGame.TeamColor.WHITE){
                        throw new InvalidMoveException("You can't move that");
                    }
                    game.makeMove(move);
                    if(game.isInCheckmate(ChessGame.TeamColor.BLACK)){
                        connection.broadcast(null, new Notification(ServerMessage.ServerMessageType.NOTIFICATION, userName + " Wins!"), userName + " Wins!");
                        game.gameOver = true;
                    }
                    else if(game.isInCheckmate(ChessGame.TeamColor.WHITE)){
                        throw new InvalidMoveException("You lost Bucko");
                    }
                    gameDB.deleteGame(id);
                    newGame = gameDB.createGame(gameDat);
                    connection.broadcast(null, new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, newGame), newGame);
                    connection.broadcast(session, new Notification(ServerMessage.ServerMessageType.NOTIFICATION, userName + " Made a move"), userName + " Made a move" + move);


                } else if (userName != null && userName.equals(bkUser)) {
                    if(game.getTeamTurn() != ChessGame.TeamColor.BLACK){
                        throw new InvalidMoveException("Not your turn");
                    }
                    ChessGame.TeamColor pColor = board.getPiece(move.getStartPosition()).getTeamColor();
                    if (pColor != ChessGame.TeamColor.BLACK) {
                        throw new InvalidMoveException("You can't move that");
                    }
                    game.makeMove(move);
                    if(game.isInCheckmate(ChessGame.TeamColor.WHITE)){
                        connection.broadcast(null, new Notification(ServerMessage.ServerMessageType.NOTIFICATION, userName + " Wins!"), userName + " Wins!");
                        game.gameOver = true;
                    }
                    else if(game.isInCheckmate(ChessGame.TeamColor.BLACK)){
                        throw new InvalidMoveException("You lost Bucko");
                    }
                    gameDB.deleteGame(id);
                    newGame = gameDB.createGame(gameDat);
                    connection.broadcast(null, new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, newGame), newGame);
                    connection.broadcast(session, new Notification(ServerMessage.ServerMessageType.NOTIFICATION, userName + " Made a move"), userName + " Made a move" + move);
                } else{
                    throw new InvalidMoveException("You Can't Move That");
                }
            } catch (InvalidMoveException e) {
                String msg = new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()));
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

    private void leave(int id, String message, String authToken, Session session){
        try {
            ConnectionManager connection = connections.get(id);
            connection.remove(session);
            String userName = "";
            AuthData auth = authDB.getAuth(authToken);
            if (auth != null) {
                userName = auth.username();
            } else {
                throw new AuthException("Error: Unauthorized");
            }
            GameData gameDat = gameDB.getGame(id);
            String bkUser = gameDat.blackUsername();
            String wtUser = gameDat.whiteUsername();
            if(Objects.equals(userName, bkUser)){
                GameData newGame = new GameData(id, gameDat.whiteUsername(), null, gameDat.gameName(), gameDat.game());
                gameDB.deleteGame(id);
                gameDB.createGame(newGame);
                connection.remove(session);
            } else if (Objects.equals(userName, wtUser)) {
                GameData newGame = new GameData(id, null, gameDat.blackUsername(), gameDat.gameName(), gameDat.game());
                gameDB.deleteGame(id);
                gameDB.createGame(newGame);
                connection.remove(session);
            }else{
                connection.remove(session);
            }
            connection.broadcast(session, new Notification(ServerMessage.ServerMessageType.NOTIFICATION, userName + " Left the Game"), userName + " Left the Game");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void resign(int id, String message, String authToken, Session session) throws DBException, DataAccessException, IOException {
        try{

        ConnectionManager connection = connections.get(id);
        String userName = "";
        AuthData auth = authDB.getAuth(authToken);
        if(auth != null){
            userName = auth.username();
        }else{
            throw new AuthException("Error: Unauthorized");
        }
        GameData gameDat = gameDB.getGame(id);
        String bkUser = gameDat.blackUsername();
        String wtUser = gameDat.whiteUsername();
        if(gameDat.game().gameOver){
            throw new InvalidMoveException("Game Over");
        }
        if(Objects.equals(userName, bkUser)){
            gameDat.game().gameOver = true;
            gameDB.deleteGame(id);
            gameDB.createGame(gameDat);
        } else if (Objects.equals(userName, wtUser)) {
            gameDat.game().gameOver = true;
            gameDB.deleteGame(id);
            gameDB.createGame(gameDat);
        }else{
            throw new UserException("You are not Authorized");
        }
        connection.broadcast(null, new Notification(ServerMessage.ServerMessageType.NOTIFICATION, userName + " Resigned"), userName + " Resigned");
    } catch (Exception e) {
            String msg = new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()));
            session.getRemote().sendString(msg);
        }
    }

}