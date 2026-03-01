package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import model.LoginRequest;
import model.LoginResult;
import org.jetbrains.annotations.NotNull;
import model.UserData;
import service.UserException;
import service.userService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        // Register your endpoints and exception handlers here.

    }

    private void register(@NotNull Context ctx) throws UserException {

        var serializer = new Gson();
        UserData user = serializer.fromJson(ctx.body(), UserData.class);
        user = userService.register(user);
    }

    private void login(@NotNull Context ctx){
        try{
        var serializer = new Gson();
        LoginRequest user = serializer.fromJson(ctx.body(), LoginRequest.class);
        AuthData auth = userService.login(user);
        }catch (UserException ex){
            System.out.print(ex);
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
