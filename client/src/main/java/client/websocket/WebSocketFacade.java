package client.websocket;



import chess.*;
import com.google.gson.Gson;


import jakarta.websocket.*;
import model.AuthData;
import model.GameData;
import model.ResponseException;
import ui.PrintBoard;
import ui.UI;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_BG_COLOR_BLACK;
import static ui.EscapeSequences.SET_BG_COLOR_DARK_GREEN;
import static ui.EscapeSequences.SET_BG_COLOR_WHITE;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;


public class WebSocketFacade extends Endpoint {

    Session session;
    UI ui;
    HashMap<Integer, GameData> games;
    String userName;

    public WebSocketFacade(String url, UI ui) throws ResponseException {
        this.ui = ui;
        this.games = new HashMap<>();
        this.userName = ui.getuserName();
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

    public void redraw(int id){
        GameData game = games.get(id);
        ui.doBoard(game);
    }

    public void makeMove(int id, ChessMove move, AuthData auth){
        try {
            var command = new MoveCommand(UserGameCommand.CommandType.MAKE_MOVE, auth.authToken(), id, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500,ex.getMessage());
        }
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

    public void highlight(int id, ChessPosition position){
        ChessGame game = games.get(id).game();
        ChessBoard board = game.getBoard();
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        if(Objects.equals(games.get(id).blackUsername(), userName)){
            color = ChessGame.TeamColor.BLACK;
        }
        Collection<ChessMove> legmoves = game.validMoves(position);
        Collection<ChessPosition> poses = new HashSet<>();
        for(ChessMove move : legmoves){
            poses.add(move.getEndPosition());
        }

        boolean toggle = false;
        ChessPiece[][] squares = board.getSquares();
        if (Objects.equals(color, "black")) {
            for (int i = 0; i < squares.length ; i++) {
                for (int j = 0; j < squares[i].length / 2; j++) {
                    ChessPiece temp = squares[i][j];
                    squares[i][j] = squares[squares.length - 1 - i][squares[i].length - 1 - j];
                    squares[squares.length - 1 - i][squares[i].length - 1 - j] = temp;
                }
            }
        }

        String boardText = "";
        for (int i = squares.length - 1; i >= 0; i--) {
            int j = 1;
            for (ChessPiece piece : squares[i]) {
                ChessPosition pos = new ChessPosition(i, j);
                j++;
                if (piece == null) {
                    if (poses.contains(pos)){
                        boardText += SET_BG_COLOR_GREEN;
                    } else if (toggle == false) {
                        boardText += SET_BG_COLOR_WHITE;
                    } else {
                        boardText +=SET_BG_COLOR_DARK_GREEN;
                    }
                    boardText += "   ";
                } else {
                    String pColor;
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        pColor = SET_TEXT_COLOR_LIGHT_GREY;
                    } else {
                        pColor = SET_TEXT_COLOR_BLACK;
                    }
                    if (toggle == false) {
                        boardText += SET_BG_COLOR_WHITE + pColor + " " + piece.toString() + " ";
                    } else {
                        boardText +=SET_BG_COLOR_DARK_GREEN + pColor + " " + piece.toString() + " ";
                    }
                }
                toggle = !toggle;
            }
            toggle = !toggle;
            //boardText += "|";
            boardText +=SET_BG_COLOR_BLACK;
            boardText += "\n";

        }
        boardText += RESET_TEXT_COLOR + RESET_BG_COLOR;
        ui.notify(boardText);
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
