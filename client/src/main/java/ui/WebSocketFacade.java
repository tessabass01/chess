package ui;

import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserver;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

public class WebSocketFacade extends Endpoint {
    public Session session;

    public WebSocketFacade(NotificationHandler notificationHandler) throws Exception {
        URI uri = new URI("ws://localhost:8080/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<ServerMessage>() {
            public void onMessage(ServerMessage message) {
                notificationHandler.notify(message);
                System.out.println(message.toString());
            }
        });
    }

    // joinGame
    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void observe(String gameID, String authToken) throws Exception {
        var observer = new JoinObserver(authToken, gameID);
        var msg = new Gson().toJson(observer);
        send(msg);
    }
}
