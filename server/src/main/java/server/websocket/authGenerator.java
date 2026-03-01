package server.websocket;
import model.AuthData;

import java.util.UUID;

public class AuthGenerator {
    public static AuthData genAuth(String username){
        String authToken = UUID.randomUUID().toString();
        return new AuthData(authToken, username);
    }
}
