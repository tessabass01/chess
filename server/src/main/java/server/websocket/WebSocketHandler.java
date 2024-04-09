package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;


import java.io.IOException;
import java.util.Timer;

import static webSocketMessages.userCommands.UserGameCommand.CommandType.*;


@WebSocket
public class WebSocketHandler(ServerMessageObserver observer) {

    private final ServerMessageObserver observer;
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message, int gameID) throws IOException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case JOIN_PLAYER -> joinp(action.visitorName(), session);
            case JOIN_OBSERVER -> joino(action.visitorName());
            case MAKE_MOVE -> move(action.visitorName(), session);
            case LEAVE -> leave(action.visitorName());
            case RESIGN -> resign(action.visitorName(), session);
        }
    }

    private void joinp(String visitorName, Session session, int gameID, ChessGame.TeamColor playerColor) throws IOException {
        connections.add(visitorName, session);
        var message = String.format("%s joined as %s", visitorName, playerColor.toString());
//        var notification = new Notification(Notification.Type.JOINED_AS_PLAYER, message);
//        observer.notify(ServerMessage message);
        connections.broadcast("", notification);
    }

    private void joino(String visitorName, Session session, int gameID) throws IOException {
        connections.add(visitorName, session);
        var message = String.format("%s joined as an observer", visitorName);
        var notification = new Notification(Notification.Type.JOINED_AS_OBSERVER, message);
        connections.broadcast("", notification);
    }

    public void move(String visitorName, int gameID, ChessMove move) throws ResponseException {
        try {
            var message = String.format("%s moved from %s to %s", visitorName, move.getStartPosition().toString(), move.getEndPosition().toString());
            var notification = new Notification(Notification.Type.MADE_MOVE, message);
            connections.broadcast("", notification);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void leave(String visitorName, int gameID) throws IOException {
        connections.remove(visitorName);
        var message = String.format("%s left the game", visitorName);
        var notification = new Notification(Notification.Type.LEFT_GAME, message);
        connections.broadcast("", notification);
    }

    private void resign(String visitorName, int gameID) throws IOException {
        connections.remove(visitorName);
        var message = String.format("%s resigned", visitorName);
        var notification = new Notification(Notification.Type.RESIGNED_GAME, message);
        connections.broadcast(visitorName, notification);
    }

}

