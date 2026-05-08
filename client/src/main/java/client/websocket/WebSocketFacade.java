package client.websocket;



import chess.ChessGame;
import com.google.gson.Gson;


import jakarta.websocket.*;
import model.AuthData;
import model.GameData;
import model.ResponseException;
import ui.PrintBoard;
import ui.UI;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;


public class WebSocketFacade extends Endpoint {

    Session session;
    UI ui;
    HashMap<Integer, GameData> games;

    public WebSocketFacade(String url, UI ui) throws ResponseException {
        this.ui = ui;
        this.games = new HashMap<>();
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");


            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    ServerMessage.ServerMessageType type = notification.getServerMessageType();
                    if(type == ServerMessage.ServerMessageType.ERROR){
                        ErrorMessage e = new Gson().fromJson(message, ErrorMessage.class);
                        ui.notify(e.errorMessage);
                    } else if (type == ServerMessage.ServerMessageType.LOAD_GAME) {
                        LoadMessage payLoad = new Gson().fromJson(message, LoadMessage.class);
                        GameData gameDat = payLoad.game;
                        games.put(gameDat.gameID(), gameDat);
                        ui.doBoard(gameDat);
                    }else{
                        Notification notif = new Gson().fromJson(message, Notification.class);
                        ui.notify(notif.message);
                    }

                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(AuthData auth, String color, int id) throws ResponseException {
        try {
            var command = new ConnectCommand(UserGameCommand.CommandType.CONNECT, color, auth.authToken(), id);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500,ex.getMessage());
        }
    }

    public void redraw(AuthData auth, int id){
        GameData game = games.get(id);
        ui.doBoard(game);
    }

    public void makeMove(  ){

    }

    public void leave(AuthData auth, int id){
        try {
            var command = new LeaveCommand(UserGameCommand.CommandType.LEAVE, auth.authToken(), id);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
            games.remove(id);
        } catch (IOException ex) {
            throw new ResponseException(500,ex.getMessage());
        }
    }

    public void resign(){

    }

    public void highlight(){

    }

    public void help(){
        ui.notify("""
                    Please enter the command that corresponds with what you want to do.
                    To create redraw the board, please enter: redraw <game id>
                    To highlight legal moves, please enter: highlight <position of the piece you want to move>
                    To make a move, please enter: <start position> <end position>
                    To resign, please enter: resign
                    To leave, please enter: leave
                    To see this message again, enter: help
                    """);
    }
}
