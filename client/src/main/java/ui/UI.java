package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import model.*;
import client.ServerFacade;
import websocket.messages.ServerMessage;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;



public class UI {
    private String userName;
    private boolean signedIn = false;
    ServerFacade server;
    WebSocketFacade socket;

    private boolean resigning = false;
    private int resignId;

    public UI(ServerFacade f) {
        this.server = f;
        socket = new WebSocketFacade(f.getServerURL(), this);
    }

    public void run(){
        System.out.println("Welcome to Chess.");
        System.out.print(help());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")){
            String line = scanner.nextLine();
            try{
                result = eval(line);
                System.out.print(result + "\n");

            }catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg + "\n");
            }

        }

    }

    public void notify(String message){
        System.out.println(message + "\n");
    }




    public String eval(String input){
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return String.valueOf(switch (cmd) {
                case "login" -> logIn(params);
                case "register" -> register(params);
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "logout" -> logOut();
                case "observe" -> observeGame(params);
                case "join" -> joinGame(params);
                case "quit" -> "quit";
                case "move" -> move(params);
                case "resign" -> resign(params);
                case "yes" -> confirmRes();
                case "no" -> denyRes();
                case "leave" -> leave(params);
                case "redraw" -> reDraw(params);
                case "commands" -> gamehelp();
                case "highlight" -> legMoves(params);
                default -> help();
            });
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String joinGame(String... params) throws Exception {
        if(resigning){
            return "please confirm resignation before issuing any other commands \n Or \n type 'list' to see the list of games again";
        }
        if(signedIn){
            String gameID = params[0];
            try {
                int iD= Integer.parseInt(gameID);
                JoinRequest req = new JoinRequest(params[1], iD);
                GameData game = server.joinGame(req);

                //getting ws connection
                model.AuthData auth =  new AuthData(server.authToken, userName);

                if(Objects.equals(game.blackUsername(), userName)){
                    socket.connect(auth, "BLACK", game.gameID());
                } else if (Objects.equals(game.whiteUsername(), userName)) {
                    socket.connect(auth, "WHITE", game.gameID());
                }
                String result = "";
//                PrintBoard board = new PrintBoard(game.game().getBoard(), params[1]);
//                result = board.print();

                return result;
            } catch (NumberFormatException e) {
                return "Not a valid game ID";
            } catch (Exception e) {
                String msg = e.getMessage();
                String[] parts = msg.split(":");
                String code = parts[parts.length - 1].trim();
                if(code.equals("400")){
                    return "Please Enter a valid color:  'white' or 'black'";
                } else if (code.equals("403")) {
                    return "That spot is already taken";
                }else{
                    return "Invalid input";
                }
            }

        }
        return "You are not signed in";
    }

    private String register(String... params){
        if(resigning){
            return "please confirm resignation before issuing any other commands \n Or \n type 'list' to see the list of games again";
        }
        try {
            if(params.length != 3){
                return "Expected <USERNAME> <PASSWORD> <EMAIL>";
            }
            UserData req = new UserData(params[0], params[1], params[2]);
            server.register(req);
            userName = req.username();
            signedIn = true;
            return req.username() + " signed in \n" + help();
        } catch (RuntimeException e) {
            return e.getMessage() + "\n" + help();
        }
    }

    private String observeGame(String... params) {
        if(resigning){
            return "please confirm resignation before issuing any other commands \n Or \n type 'list' to see the list of games again";
        }
        if(!signedIn){
            return "You are not Signed in";
        }
        if(params.length != 1){
            return "Expected <game ID>";
        }
        GameList games = server.getGames();
        ArrayList<Integer> nums = new ArrayList<>();
        for(GameResult game : games.games()){
            nums.add(game.gameID());
        }
        int iD;
        try {
            iD = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return "Invalid game ID";
        }
        if(nums.contains(iD)){
            JoinRequest req = new JoinRequest(params[0], iD);
            //getting ws connection
            model.AuthData auth =  new AuthData(server.authToken, userName);
            socket.connect(auth, "Observer", iD);
            return "\n";
        }
        return "Invalid input";
    }  //(Get valid gamenumber and return board)

    private String logOut() throws Exception {
        if(resigning){
            return "please confirm resignation before issuing any other commands \n Or \n type 'list' to see the list of games again";
        }
        if(signedIn){
            server.logout(userName);
            signedIn = false;
            return String.format("You logged out. \n" + help());
        }
        throw new Exception("You are not Signed in");
    }

    private String listGames() {
        if(signedIn){
            GameList games = server.getGames();
            String result = "";
            for(GameResult game : games.games()){
                String players = ", W:: ";
                if(game.whiteUsername() != null){
                    players += game.whiteUsername();
                }else{
                    players += "<empty seat> ";
                }
                players += " B:: ";
                if(game.blackUsername() != null){
                    players += game.blackUsername();
                }else{
                    players += "<empty seat> ";
                }
                result += game.gameID() +  ". " + game.gameName() + players +"\n";
            }
            if(result.equals("")){
                result = "There are no games.";
            }
            return result;
        }
        return "You are not signed in";
    }

    private String createGame(String... params) {
        if(resigning){
            return "please confirm resignation before issuing any other commands \n Or \n type 'list' to see the list of games again";
        }
        if(signedIn) {
            CreateGameRequest req = new CreateGameRequest(params[0]);
            server.newGame(req);
            return "Creation Sucessful \n" + listGames();
        }
        return "You are not signed in";
    }

    private String logIn(String... params) throws Exception {
        if(resigning){
            return "please confirm resignation before issuing any other commands \n Or \n type 'list' to see the list of games again";
        }
        if (params.length == 2) {
            try {
                userName = params[0];
                String password = params[1];
                LoginRequest req = new LoginRequest(userName, password);
                server.login(req);
                signedIn = true;
                return String.format("You signed in as %s. \n " + help(), userName);
            } catch (Exception e) {
                String msg = e.getMessage();
                String[] parts = msg.split(":");
                String code = parts[parts.length - 1].trim();
                if(code.equals("401")){
                    return "Incorrect Login Information";
                }
                return e.getMessage();
            }
        }
        throw new Exception("Expected: <username> <password>");
    }

    private String move(String... params){
        if(resigning){
            return "please confirm resignation before issuing any other commands \n Or \n type 'list' to see the list of games again";
        }
        if(params.length < 3){
            return "please enter <game id> <start position> <end position>";
        }
        try {
            String param1 = params[1];
            String param2 = params[2];
            int sPRow = Integer.parseInt(param1.substring(0, 1));
            int sPCol = Integer.parseInt(param1.substring(param1.length() - 1));

            int ePRow = Integer.parseInt(param2.substring(0, 1));
            int ePCol = Integer.parseInt(param2.substring(param1.length() - 1));

            ChessPosition sp = new ChessPosition(sPRow, sPCol);
            ChessPosition ep = new ChessPosition(ePRow, ePCol);
            ChessPiece.PieceType pp = null;

            if (params.length > 3) {
                String type = params[3].toUpperCase();
                if (type.equals("QUEEN")) {
                    pp = ChessPiece.PieceType.QUEEN;
                } else if (type.equals("ROOK")) {
                    pp = ChessPiece.PieceType.ROOK;
                } else if (type.equals("KNIGHT")) {
                    pp = ChessPiece.PieceType.KNIGHT;
                } else if (type.equals("BISHOP")) {
                    pp = ChessPiece.PieceType.BISHOP;
                } else {
                    return "Incorrect promotion type.  please try again.";
                }
            }
            ChessMove move = new ChessMove(sp, ep, pp);
            int id = Integer.parseInt(params[0]);
            if (!socket.checkConnection(id)) {
                return "please enter a game id that you are playing in.";
            }
            socket.makeMove(id, move, new AuthData(server.authToken, userName));
            String result = "";
            return result;
        }catch (NumberFormatException e){
            return "Please enter <game id> <Start position> <end position>";
        }
    }

    private String leave(String... params){
        try {
            if (resigning) {
                return "please confirm resignation before issuing any other commands \n Or \n type 'list' to see the list of games again";
            }

            GameList games = server.getGames();
            ArrayList<Integer> nums = new ArrayList<>();
            for(GameResult game : games.games()){
                nums.add(game.gameID());
            }
            int iD;
            try {
                iD = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                return "Invalid game ID";
            }
            if(nums.contains(iD)) {

                socket.leave(new AuthData(server.authToken, userName), Integer.parseInt(params[0]));
                String result = "";
                return result;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Invalid input";

    }

    private String resign(String... params){
        if(resigning){
            return "please confirm resignation before issuing any other commands \n Or \n type 'list' to see the list of games again";
        }
        String result = "Are you Sure you want to resign?";
        resigning = true;
        try {
            resignId = Integer.parseInt(params[0]);
            return result;
        } catch (NumberFormatException e) {
            return "Please enter a valid game id";
        }
    }
    
    private String confirmRes(){
        String result = "";
        if(resigning){
            socket.resign(resignId, server.authToken);
            resigning = false;
            result = "Resignation successful";
        }
        return result;
    }
    
    private String denyRes(){
        if(resigning){
            resigning = false;
            return "Resignation Cancelled";
        }
        return "";
    }

    private String reDraw(String... params){
        if(resigning){
            return "please confirm resignation before issuing any other commands \n Or \n type 'list' to see the list of games again";
        }
        GameList games = server.getGames();
        ArrayList<Integer> nums = new ArrayList<>();
        for(GameResult game : games.games()){
            nums.add(game.gameID());
        }
        int iD;
        try{
            iD = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return "Invalid game ID";
        }
        if(nums.contains(iD)) {
            String result = "";
            socket.redraw(Integer.parseInt(params[0]));
            return result;
        }
        return "invalid input";
    }

    private String legMoves(String... params){
        if(resigning){
            return "please confirm resignation before issuing any other commands \n Or \n type 'list' to see the list of games again";
        }
        String result = "";
        String param1 = params[1];
        int sPRow = Integer.parseInt(param1.substring(0,1));
        int sPCol = Integer.parseInt(param1.substring(param1.length() - 1));
        ChessPosition sp = new ChessPosition(sPRow, sPCol);
        socket.highlight(Integer.parseInt(params[0]), sp);
        return result;
    }

    public String getuserName() {
        return userName;
    }

    public void doBoard(GameData gameDat){
        String color = "";
        if (Objects.equals(gameDat.whiteUsername(), userName)) {
            color = "white";
        } else if(Objects.equals(gameDat.blackUsername(), userName)){
            color = "black";
        }
        PrintBoard board = new PrintBoard(gameDat.game().getBoard(), color);
        String result = board.print();
        System.out.print(result);
    }

    public String gamehelp(){
        socket.help();
        return "";
    }

    public String help(){
        if(resigning){
            return "please confirm resignation before issuing any other commands \n Or \n type 'list' to see the list of games again";
        }
        if(!signedIn){
            return """
                    Please enter the command that corresponds with what you want to do.
                    To create an account, please enter: register <USERNAME> <PASSWORD> <EMAIL>  
                    To log in, please enter: login <USERNAME> <PASSWORD>
                    To close, please enter: quit
                    
                    """;
        }
        else{
            return """
                    To create a game: create <game name>
                    To list games: list
                    To join a game: join <Game ID> <WHITE|BLACK>
                    To observe a game: observe <ID>
                    To logout: logout
                    To close: quit
                    To list commands again: help
                    To list game commands: commands
                    
                    """;
        }
    }
}
