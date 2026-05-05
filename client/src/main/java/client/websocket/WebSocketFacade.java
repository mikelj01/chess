package client.websocket;



import com.google.gson.Gson;


import jakarta.websocket.*;
import model.AuthData;
import model.ResponseException;
import ui.UI;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebSocketFacade extends Endpoint {

    Session session;


    public WebSocketFacade(String url, UI ui) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");


            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    ServerMessage.ServerMessageType type = notification.getServerMessageType();
                    if(type == ServerMessage.ServerMessageType.ERROR){
                        ErrorHandler eh = new ErrorHandler();
                        eh.notify(notification, ui);
                    }

                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(AuthData auth, String color, int id) throws ResponseException {
        try {
            var command = new ConnectCommand(UserGameCommand.CommandType.CONNECT, color, auth.authToken(), id);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500,ex.getMessage());
        }
    }



}
