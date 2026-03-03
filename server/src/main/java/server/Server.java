package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import org.jetbrains.annotations.NotNull;
import service.AuthService;
import service.GameService;
import service.UserException;
import service.UserService;


public class Server {
    UserDataAccess UserDB;
    UserService uServe;
    GameDataAccess gameDB;
    GameService gServe;
    AuthDataAccess authDB;
    AuthService aServe;
    private final Javalin javalin;

    public Server() {
        this.UserDB = new MemUserDA();
        this.gameDB = new MemGameDA();
        this.authDB = new MemAuthDA();
        this.uServe = new UserService(UserDB, authDB);
        this.gServe = new GameService(gameDB);
        this.aServe = new AuthService(authDB);
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user", this::register);
        javalin.post("/session", this::logIn);
        javalin.delete("/session", this::logOut);
        javalin.get("/game", this::getGames);
        javalin.post("/game", this::newGame);
        javalin.put("/game", this::joinGame);


        //javalin.post("/db", this::dataStuff);
        // Register your endpoints and exception handlers here.

    }

    private void register(@NotNull Context ctx) throws UserException {
        var serializer = new Gson();
        UserData user = serializer.fromJson(ctx.body(), UserData.class);
        user = uServe.register(user);
        ctx.result(new Gson().toJson(user));
    }

    private void logIn(@NotNull Context ctx){
        try{
        var serializer = new Gson();
        LoginRequest user = serializer.fromJson(ctx.body(), LoginRequest.class);
        AuthData auth = uServe.login(user);

        }catch (UserException ex){
            System.out.print(ex);
        }
    }

    private void logOut(@NotNull Context ctx){
    var serializer = new Gson();
    LogoutRequest authToken = serializer.fromJson(ctx.body(), LogoutRequest.class);
    try{
        uServe.logOut(authToken);
    }catch(UserException error){
    }

    }

    private void getGames(@NotNull Context ctx){

    }
    private void newGame(@NotNull Context ctx){

    }
    private void joinGame(@NotNull Context ctx){

    }

    /* I've Set the Delete Fungtion up this way so that in the future
    If I want to add more methods that work on the database, I can.*/
//    private void dataStuff(@NotNull Context ctx){
//        var serializer = new Gson();
//        DataInstruction data =
//    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
