package client;

import chess.ChessBoard;
import client.ServerFacade;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import ui.PrintBoard;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    LoginResult authData;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
        //facade = new client.ServerFacade("http://127.0.0.1:8000");
    }

    @BeforeEach
    public void eachTime(){
        facade.delete();
        UserData req = new UserData("existingUser", "password", "EU@email.com");
        authData = facade.register(req);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerSuccess() {
        facade.logout(authData.username());
        UserData req = new UserData("player1", "password", "p1@email.com");
        authData = facade.register(req);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerF() {
        facade.logout(authData.username());
        UserData req = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(req);
        try{
            facade.register(req);
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("You are already registered"));
        }
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void loginSuccess() {
        facade.logout(authData.username());
        LoginRequest req = new LoginRequest("existingUser", "password");
        authData = facade.login(req);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void loginF() {
        try {
            facade.logout(authData.username());
            LoginRequest req = new LoginRequest("non-user", "password");
            LoginResult result = facade.login(req);
        } catch (Exception e) {
            assertTrue(e.getMessage().length() > 1);
        }

    }

    @Test
    public void logoutSuccess() {
        facade.logout(authData.username());
        try {
            facade.logout(authData.username());
        } catch (Exception e) {
            String[] parts = e.getMessage().split(":");
            String code = parts[parts.length - 1].trim();
            assertTrue(code.equals("401"));
        }
    }

    @Test
    public void logoutFail() {
        String auth = facade.authToken;
        try {
            facade.authToken = "beans";
            facade.logout(authData.username());
        } catch (Exception e) {
            facade.authToken = auth;
            String[] parts = e.getMessage().split(":");
            String code = parts[parts.length - 1].trim();
            assertTrue(code.equals("401"));
        }
        facade.authToken = auth;
    }

    @Test
    public void createGameSuccess(){
        CreateGameRequest req = new CreateGameRequest("beans");
        NewGameResult res = facade.newGame(req);
        assertTrue(res.gameID() == 1);
    }
    @Test
    public void createGameFail(){
        CreateGameRequest req = new CreateGameRequest("beans");
        NewGameResult res = facade.newGame(req);
        try{
            NewGameResult res2 = facade.newGame(req);
            assertTrue(res2.gameID() == 2);
        } catch (Exception e) {
            String[] parts = e.getMessage().split(":");
            String code = parts[parts.length - 1].trim();
            assertTrue(code.equals("400"));
        }
    }

    @Test
    public void getGamesSuccess(){
        CreateGameRequest req = new CreateGameRequest("beans");
        facade.newGame(req);
        GameList games = facade.getGames();
        assertFalse(games.games().isEmpty());
    }

    @Test
    public void getGamesFail() {
        String auth = facade.authToken;
        try {
            facade.authToken = "beans";
            facade.getGames();
        } catch (Exception e) {
            facade.authToken = auth;
            String[] parts = e.getMessage().split(":");
            String code = parts[parts.length - 1].trim();
            assertTrue(code.equals("401"));
        }
        facade.authToken = auth;
    }

    @Test
    public void joinSuccess(){
        CreateGameRequest req = new CreateGameRequest("beans");
        facade.newGame(req);
        JoinRequest req1 = new JoinRequest("White", 1);
        GameData game = facade.joinGame(req1);
        assertTrue(Objects.equals(game.whiteUsername(), "existingUser"));
    }

    @Test
    public void joinFail() {
        CreateGameRequest req = new CreateGameRequest("beans");
        facade.newGame(req);
        try {
            JoinRequest req1 = new JoinRequest("White", 8);
            GameData game = facade.joinGame(req1);
            assertTrue(Objects.equals(game.whiteUsername(), "existingUser"));
        } catch (Exception e) {
            String[] parts = e.getMessage().split(":");
            String code = parts[parts.length - 1].trim();
            assertTrue(code.equals("400"));
        }
    }

    @Test
    public void deleteSuccess(){
        try{
        facade.delete();
        assertEquals(0,0);
        } catch (Exception e) {
            assertEquals(0, 1);
        }
    }

    @Test
    public void printBoardSuccessW(){
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        PrintBoard printer = new PrintBoard(board, "white");
        String result = printer.print();
        System.out.print(result);
    }
    @Test
    public void printBoardSuccessB(){
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        PrintBoard printer = new PrintBoard(board, "black");
        String result = printer.print();
        System.out.print(result);
    }

}
