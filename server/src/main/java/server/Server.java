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
        this.aServe = new AuthService(authDB);
        this.uServe = new UserService(UserDB, aServe);
        this.gServe = new GameService(gameDB, aServe);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user", this::register);
        javalin.post("/session", this::logIn);
        javalin.delete("/session", this::logOut);
        javalin.get("/game", this::getGames);
        javalin.post("/game", this::newGame);
        javalin.put("/game", this::joinGame);
        javalin.delete("/db", this::deleteDB);
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
        String authToken = ctx.attribute("authToken");
    try{
        uServe.logOut(authToken);
    }catch(UserException error){
    }

    }

    private void getGames(@NotNull Context ctx){
        try {
            String authToken = ctx.attribute("authToken");
            gServe.getGames(authToken);
        } catch (UserException e) {
           System.out.print(e.getMessage());
        }

    }

    private void newGame(@NotNull Context ctx){
        try{
        var serializer = new Gson();
        String gameName = serializer.fromJson(ctx.body(), String.class);
        String authToken = ctx.attribute("authToken");
        model.GameData game = gServe.newGame(gameName, authToken);
        }catch (UserException e){
            System.out.print(e.getMessage());
        }

    }


    // Issues, How do I get the Auth Token from the requests sent?

    private void joinGame(@NotNull Context ctx) {
        try {
            var serializer = new Gson();
            JoinRequest req = serializer.fromJson(ctx.body(), JoinRequest.class);
            String authToken = ctx.attribute("authToken");
            gServe.joinGame(authToken, req);
        }catch (UserException e){
            System.out.print(e.getMessage());
        }
    }



    private void deleteDB(@NotNull Context ctx){
        uServe.clear();
        gServe.clear();
        aServe.clear();
   }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
