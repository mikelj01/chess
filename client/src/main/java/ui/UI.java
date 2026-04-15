package ui;

import chess.ChessBoard;
import chess.ChessGame;
import jdk.jshell.spi.ExecutionControl;
import model.*;
import server.ServerFacade;

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
                int ID = Integer.parseInt(gameID);
                JoinRequest req = new JoinRequest(params[1], ID);
                GameData game = server.joinGame(req);
                PrintBoard board = new PrintBoard(game.game().getBoard(), params[1]);
                String result = board.print();
                return result;
            } catch (NumberFormatException e) {
                return "Not a valid game ID";
            } catch (Exception e) {
                throw new Exception(e);
            }

        }
        return "You are not signed in";
    }


    private String register(String... params){
        try {
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
        GameList games = server.getGames();
        ArrayList<Integer> nums = new ArrayList<>();
        for(GameResult game : games.games()){
            nums.add(game.gameID());
        }
        int ID = Integer.parseInt(params[0]);
        if(nums.contains(ID)){
            for(GameResult game : games.games()){
                if(game.gameID() == ID) {
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
            server.Logout(userName);
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
                result += game.gameID() + ". " + game.gameName() + "\n";
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
                server.Login(req);
                signedIn = true;
                return String.format("You signed in as %s. \n " + help(), userName);
            } catch (Exception e) {
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
