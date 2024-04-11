package ui;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.Leave;

import javax.websocket.*;
import java.net.URI;

public class WebSocketFacade extends Endpoint {
    public Session session;

    public WebSocketFacade(NotificationHandler notificationHandler, String color) throws Exception {
        URI uri = new URI("ws://localhost:8080/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
//                ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                notificationHandler.notify(message);
            }
        })
    ;}

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

    public void joinGame(String gameID, String authToken, ChessGame.TeamColor playerColor) throws Exception {
        var player = new JoinPlayer(authToken, Integer.parseInt(gameID), playerColor);
        var msg = new Gson().toJson(player);
        send(msg);
    }

    public void leave(String gameID, String authToken) throws Exception {
        var leaving = new Leave(authToken, Integer.parseInt(gameID));
        var msg = new Gson().toJson(leaving);
        send(msg);
    }
}
