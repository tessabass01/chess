package server.websocket;
import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MySqlDataAccess;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.*;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.userCommands.*;
import java.io.IOException;


@WebSocket
public class WebSocketHandler {
    private final DataAccess dataAccess;

    {
        try {
            dataAccess = new MySqlDataAccess();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case JOIN_PLAYER -> joinp(message, session);
            case JOIN_OBSERVER -> joino(message, session);
//            case MAKE_MOVE -> move(action.visitorName(), session);
            case LEAVE -> leave(message);
            case RESIGN -> resign(message);
        }
    }

    private void joinp(String message, Session session) throws Exception {
        var player = new Gson().fromJson(message, JoinPlayer.class);
        var connection = new Connection(player.getAuthString(), session, player.playerColor);
        var game = dataAccess.getGame(player.gameID);
        if (player.playerColor.equals(ChessGame.TeamColor.WHITE)) {
            if (!game.whiteUsername().equals(dataAccess.getAuth(player.getAuthString()).username())) {
                var error = new Gson().toJson(new Error("Error: already taken"));
                connection.send(error);
                return;
            }
        } else if (player.playerColor.equals(ChessGame.TeamColor.BLACK)) {
            if (!game.blackUsername().equals(dataAccess.getAuth(player.getAuthString()).username())) {
                var error = new Error("Error: already taken");
                connection.send(new Gson().toJson(error));
                return;
            }
        }
        connections.add(Integer.toString(player.gameID), connection);
        var board = new LoadGame(new ChessGame());
        connection.send(new Gson().toJson(board));
        var notifyMsg = String.format("%s joined as %s", dataAccess.getAuth(player.getAuthString()).username(), player.playerColor);
        var notification = new Notification(notifyMsg);
        connections.broadcast(player.getAuthString(), notification);
    }

    private void joino(String message, Session session) throws Exception {
        var observer = new Gson().fromJson(message, JoinObserver.class);
        var connection = new Connection(observer.getAuthString(), session, null);
        connections.add(observer.gameID, connection);
        // needs to send a LOAD_GAME msg to root client
        var board = new LoadGame(new ChessGame());
        connection.send(new Gson().toJson(board));
        var notifyMsg = String.format("%s joined as an observer", dataAccess.getAuth(observer.getAuthString()).username());
        var notification = new Notification(notifyMsg);
        connections.broadcast(observer.getAuthString(), notification);
    }

//    public void move(String visitorName, int gameID, ChessMove move) throws ResponseException {
//        try {
//            var message = String.format("%s moved from %s to %s", visitorName, move.getStartPosition().toString(), move.getEndPosition().toString());
//            var notification = new Notification(Notification.Type.MADE_MOVE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }

    private void leave(String message) throws IOException, DataAccessException {
        var leaving = new Gson().fromJson(message, Leave.class);
        var playerColor = connections.getConnection(Integer.toString(leaving.gameID), leaving.getAuthString()).playerColor;
        connections.removeConnection(Integer.toString(leaving.gameID), leaving.getAuthString());
        var reply = dataAccess.leaveGame(leaving.gameID, playerColor);
        System.out.print(reply);
        var notifyMsg = String.format("%s left the game", dataAccess.getAuth(leaving.getAuthString()).username());
        var notification = new Notification(notifyMsg);
        connections.broadcast(leaving.getAuthString(), notification);
    }

    private void resign(String message) throws IOException, DataAccessException {
        var resigning = new Gson().fromJson(message, Resign.class);
        connections.removeGame(Integer.toString(resigning.gameID));
        var notifyMsg = String.format("%s resigned", dataAccess.getAuth(resigning.getAuthString()).username());
        var notification = new Notification(notifyMsg);
        connections.broadcast("", notification);
    }

}

