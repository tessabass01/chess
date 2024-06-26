package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.net.URI;
import java.util.HashMap;

public class WebSocketFacade extends Endpoint {
    public HashMap<String, Integer> indices;
    public Session session;

    public WebSocketFacade(NotificationHandler notificationHandler, String color) throws Exception {
        indices = new HashMap<>();
        indices.put("a", 8);
        indices.put("b", 7);
        indices.put("c", 6);
        indices.put("d", 5);
        indices.put("e", 4);
        indices.put("f", 3);
        indices.put("g", 2);
        indices.put("h", 1);

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

    public void makeMove(String authToken, String gameID, String move) throws Exception {
        var startPosition = new ChessPosition(Integer.parseInt(move.substring(1, 2)), indices.get(move.substring(0,1)));
        var endPosition = new ChessPosition(Integer.parseInt(move.substring(3)), indices.get(move.substring(2,3)));
        var makeMove = new MakeMove(authToken, Integer.parseInt(gameID), new ChessMove(startPosition, endPosition, null));
        var msg = new Gson().toJson(makeMove);
        send(msg);
    }

    public void leave(String gameID, String authToken) throws Exception {
        var leaving = new Leave(authToken, Integer.parseInt(gameID));
        var msg = new Gson().toJson(leaving);
        send(msg);
    }

    public void resign(String gameID, String authToken) throws Exception {
        var resigning = new Resign(authToken, Integer.parseInt(gameID));
        var msg = new Gson().toJson(resigning);
        send(msg);
    }

    public void redraw(String gameID, String authToken) throws Exception {
        var redraw = new Redraw(Integer.parseInt(gameID), authToken);
        var msg = new Gson().toJson(redraw);
        send(msg);
    }

    public void legalMoves(String gameID, String authToken, String startPosition) throws Exception {
        var newStartPosition = new ChessPosition(Integer.parseInt(startPosition.substring(1, 2)), indices.get(startPosition.substring(0,1)));
        var legalMoves = new LegalMoves(Integer.parseInt(gameID), authToken, newStartPosition);
        var msg = new Gson().toJson(legalMoves);
        send(msg);
    }
}
