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
    //HashMap<Integer, GameData> games;
    String userName;
    GameData currGame;


    public WebSocketFacade(String url, UI ui) throws ResponseException {
        this.ui = ui;
        //this.games = new HashMap<>();
        this.userName = "";
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
                        currGame = gameDat;
                        ChessBoard board = currGame.game().getBoard();
                        ChessBoard newBoard = new ChessBoard(board);
                        ui.doBoard(gameDat, newBoard);
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

    public void redraw(){
        ChessBoard board = currGame.game().getBoard();
        ChessBoard newBoard = new ChessBoard(board);
        ui.doBoard(currGame, newBoard);
    }

    public void makeMove(ChessMove move, AuthData auth){
        try {
            var command = new MoveCommand(UserGameCommand.CommandType.MAKE_MOVE, auth.authToken(), currGame.gameID(), move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500,ex.getMessage());
        }
    }

    public void leave(AuthData auth){
        try {
            var command = new LeaveCommand(UserGameCommand.CommandType.LEAVE, auth.authToken(), currGame.gameID());
            this.session.getBasicRemote().sendText(new Gson().toJson(command));

        } catch (IOException ex) {
            throw new ResponseException(500,ex.getMessage());
        }
    }

    public void resign(String authtoken){
        try {
            var command = new LeaveCommand(UserGameCommand.CommandType.RESIGN, authtoken, currGame.gameID());
            this.session.getBasicRemote().sendText(new Gson().toJson(command));

        } catch (IOException ex) {
            throw new ResponseException(500,ex.getMessage());
        }
    }

    public void highlight(ChessPosition position){
        //gets board, username and color
        ChessGame game = currGame.game();
        ChessBoard oldboard = game.getBoard();
        ChessBoard board = new ChessBoard(oldboard);
        userName = ui.getuserName();
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        if(Objects.equals(currGame.blackUsername(), userName)){
            color = ChessGame.TeamColor.BLACK;
        }

        //accounting for something that has been broken since the beginning.
        int posRow = position.getRow();
        int posCol = position.getColumn();
        int row = posRow;
        int col = posCol;
        if(color == ChessGame.TeamColor.BLACK) {
            if(posRow == 1){
                row = 8;
            }
            if(posRow == 2){
                row = 7;
            }
            if(posRow == 3){
                row = 6;
            }
            if(posRow == 4){
                row = 5;
            }
            if(posRow == 5){
                row = 4;
            }
            if(posRow == 6){
                row = 3;
            }
            if(posRow == 7){
                row = 2;
            }
            if(posRow == 8){
                row = 2;
            }
            if(posCol == 1){
                col = 8;
            }
            if(posCol == 2){
                col = 7;
            }
            if(posCol == 3){
                col = 6;
            }
            if(posCol == 4){
                col = 5;
            }
            if(posCol == 5){
                col = 4;
            }
            if(posCol == 6){
                col = 3;
            }
            if(posCol == 7){
                col = 2;
            }
            if(posCol == 8){
                col = 2;
            }
        }

        position = new ChessPosition(row, col);
        //getting legal moves
        Collection<ChessMove> legmoves = game.validMoves(position);
        Collection<ChessPosition> poses = new HashSet<>();
        for(ChessMove move : legmoves){
            poses.add(move.getEndPosition());
        }

        boolean toggle = false;
        ChessPiece[][] squares = board.getSquares();
        int rowNum;
        if(color == ChessGame.TeamColor.BLACK){
            rowNum = 1;
        }else{
            rowNum = 8;
        }

        if (color == ChessGame.TeamColor.BLACK) {
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
            boardText += RESET_TEXT_COLOR;
            boardText += rowNum + " ";
            if(color == ChessGame.TeamColor.BLACK){
                rowNum ++;
            }else {
                rowNum--;
            }
            for (ChessPiece piece : squares[i]) {
                ChessPosition pos = new ChessPosition(i + 1, j);
                j++;
                if (piece == null) {
                    if (poses.contains(pos)){
                        boardText += SET_BG_COLOR_BLUE;
                    } else if (!toggle) {
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
        boardText += RESET_TEXT_COLOR;
        if(color == ChessGame.TeamColor.BLACK){
            boardText += "   8  7  6  5  4  3  2  1  \n";
        }else{
            boardText += "   1  2  3  4  5  6  7  8  \n";
        }
        boardText += RESET_BG_COLOR;
        ui.notify(boardText);
    }

    public void help(){
        ui.notify("""
                    Please enter the command that corresponds with what you want to do.
                    To create redraw the board: redraw
                    To highlight legal moves: highlight <position of the piece you want to move>
                    To make a move:  <start position> <end position> <promotion piece> 
                            (example of promotion: 7,8 8,8 knight)
                            If you are not promoting, leave it empty, ex:1,1 2,2)
                    To resign: resign 
                    To leave: leave 
                    To see this message again: commands
                    """);
    }
}
