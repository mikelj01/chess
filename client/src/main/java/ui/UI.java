package ui;

import chess.ChessGame;
import model.*;
import client.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;



public class UI {
    private String userName;
    private boolean signedIn = false;
    ServerFacade server;

    public UI(ServerFacade f) {
        this.server = f;
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
        System.out.println(message);
    }

    public String eval(String input){
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> logIn(params);
                case "register" -> register(params);
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "logout" -> logOut();
                case "observe" -> observeGame(params);
                case "join" -> joinGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String joinGame(String... params) throws Exception {
        if(signedIn){
            String gameID = params[0];
            try {
                int iD= Integer.parseInt(gameID);
                JoinRequest req = new JoinRequest(params[1], iD);
                GameData game = server.joinGame(req);
                PrintBoard board = new PrintBoard(game.game().getBoard(), params[1]);
                String result = board.print();
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
        try {
            if(params.length != 3){
                return "Expected <USERNAME> <PASSWORD> <EMAIL>";
            }
            UserData req = new UserData(params[0], params[1], params[2]);
            server.register(req);
            signedIn = true;
            return req.username() + " signed in \n" + help();
        } catch (RuntimeException e) {
            return e.getMessage() + "\n" + help();
        }
    }

    private String observeGame(String... params) {
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
            for(GameResult game : games.games()){
                if(game.gameID() == iD) {
                    ChessGame myGame = new ChessGame();
                    PrintBoard board = new PrintBoard(myGame.getBoard(), "white");
                    String result = board.print();
                    return result;
                }
            }

        }
        return "Invalid input";
    }  //(Get valid gamenumber and return board)

    private String logOut() throws Exception {
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
        if(signedIn) {
            CreateGameRequest req = new CreateGameRequest(params[0]);
            server.newGame(req);
            return "Creation Sucessful \n" + listGames();
        }
        return "You are not signed in";
    }

    private String logIn(String... params) throws Exception {
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

    public String help(){
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
                    
                    """;
        }
    }
}
