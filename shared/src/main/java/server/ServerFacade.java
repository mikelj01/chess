package server;


import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {


    private final String serverURL;
    public String authToken;
    public ServerFacade(String url){
        serverURL = url;
        this.authToken = null;
    }

    public LoginResult register (UserData req){
        try {
            var path = "/user";
            LoginResult result = this.makeRequest("POST", path, req, LoginResult.class);
            authToken = result.authToken();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("You are already logged in");
        }
    }

    public LoginResult Login (LoginRequest req){
        try {
            var path = "/session";
            LoginResult result = this.makeRequest("POST", path, req, LoginResult.class);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void Logout (String req){
        try {
            var path = "/session";
             this.makeRequest("DELETE", path, req, null);
             authToken = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public GameResult newGame (CreateGameRequest req){
        try {
            var path = "/game";
            return this.makeRequest("POST", path, req, GameResult.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public GameList getGames (){
        try {
            var path = "/game";
            return this.makeRequest("GET", path,null, GameList.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public GameData joinGame (JoinRequest req){
        try {
            var path = "/game";
            return this.makeRequest("PUT", path, req, GameData.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(){
        try {
            var path = "/db";
            this.makeRequest("DELETE", path,null , null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setRequestProperty("authorization", authToken);
            http.setDoOutput(true);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());

            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);

        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() > 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
                if(responseClass == null){
                    return null;
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) { return status / 100 == 2; }
}