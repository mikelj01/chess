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
        this.uServe = new UserService(UserDB);
        this.gServe = new GameService(gameDB);
        this.aServe = new AuthService(authDB);
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user", this::register);
        javalin.post("/session", this::logInOut);

        //javalin.post("/db", this::dataStuff);
        // Register your endpoints and exception handlers here.

    }

    private void register(@NotNull Context ctx) throws UserException {

        var serializer = new Gson();
        UserData user = serializer.fromJson(ctx.body(), UserData.class);
        user = uServe.register(user);
    }

    private void logInOut(@NotNull Context ctx){
        try{
        var serializer = new Gson();
        LoginRequest user = serializer.fromJson(ctx.body(), LoginRequest.class);
        AuthData auth = uServe.login(user);
        }catch (UserException ex){
            System.out.print(ex);
        }
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
