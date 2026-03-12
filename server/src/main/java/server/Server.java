package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import org.jetbrains.annotations.NotNull;
import service.*;

import java.util.Map;


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
        try {
            var serializer = new Gson();
            UserData user = serializer.fromJson(ctx.body(), UserData.class);
            LoginResult result;
            result = uServe.register(user);
            ctx.result(new Gson().toJson(result));
        }catch(UserException e){
            ctx.result(new Gson().toJson(Map.of("message",e.getMessage())));
            ctx.status(403);
        }catch (DataAccessException e){
            ctx.result(new Gson().toJson(Map.of("message",e.getMessage())));
        }
    }

    private void logIn(@NotNull Context ctx){
        try{
        var serializer = new Gson();
        LoginRequest user = serializer.fromJson(ctx.body(), LoginRequest.class);
        String authToken = ctx.header("authorization");
        AuthData auth = uServe.login(user, authToken);
            ctx.result(new Gson().toJson(auth));
        }catch (UserException e){
            ctx.result(new Gson().toJson(Map.of("message",e.getMessage())));
            ctx.status(400);
        }catch(AuthException e){
            ctx.result(new Gson().toJson(Map.of("message",e.getMessage())));
            ctx.status(401);
        }catch(DataAccessException e){
            ctx.result(new Gson().toJson(Map.of("message",e.getMessage())));
            ctx.status(500);
        }
    }

    private void logOut(@NotNull Context ctx){
    var serializer = new Gson();
        String authToken = ctx.header("authorization");
    try{
        uServe.logOut(authToken);
    }catch (UserException e){
        ctx.result(new Gson().toJson(Map.of("message",e.getMessage())));
        ctx.status(400);
    }catch(AuthException e){
        ctx.result(new Gson().toJson(Map.of("message",e.getMessage())));
        ctx.status(401);
    }catch(DataAccessException e){
        ctx.result(new Gson().toJson(Map.of("message",e.getMessage())));
        ctx.status(500);
    }

    }

    private void getGames(@NotNull Context ctx){
        try {
            String authToken = ctx.header("authorization");
            gServe.getGames(authToken);
        }catch (UserException e){
            ctx.result(new Gson().toJson(Map.of("message",e.getMessage())));
            ctx.status(400);
        }catch(AuthException e){
            ctx.result(new Gson().toJson(Map.of("message",e.getMessage())));
            ctx.status(401);
        }catch(DataAccessException e){
            ctx.result(new Gson().toJson(Map.of("message",e.getMessage())));
            ctx.status(500);
        }

    }

    private void newGame(@NotNull Context ctx){
        try{
        var serializer = new Gson();
        String gameName = serializer.fromJson(ctx.body(), String.class);
        String authToken = ctx.header("authorization");
        model.GameData game = gServe.newGame(gameName, authToken);
        }catch (UserException e){
            ctx.result(new Gson().toJson(Map.of("message",e.getMessage())));
            ctx.status(400);
        }catch(AuthException e){
            ctx.result(new Gson().toJson(Map.of("message",e.getMessage())));
            ctx.status(401);
        }catch(DataAccessException e){
            ctx.result(new Gson().toJson(Map.of("message",e.getMessage())));
            ctx.status(500);
        }

    }

    // Issues, How do I get the Auth Token from the requests sent?

    private void joinGame(@NotNull Context ctx) {
        try {
            var serializer = new Gson();
            JoinRequest req = serializer.fromJson(ctx.body(), JoinRequest.class);
            String authToken = ctx.header("authorization");
            gServe.joinGame(authToken, req);
        }catch (UserException e){
            System.out.print(e.getMessage());
        }
    }

    private void deleteDB(@NotNull Context ctx){
        try {
            uServe.clear();
            gServe.clear();
            aServe.clear();
        }catch(DataAccessException e){
            ctx.result(new Gson().toJson(Map.of("message",e.getMessage())));
            ctx.status(500);
        }
   }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
