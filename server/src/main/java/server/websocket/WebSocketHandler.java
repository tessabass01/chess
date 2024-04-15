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
import java.util.HashMap;


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
    private final HashMap<String, String> indices = new HashMap<>();


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case JOIN_PLAYER -> joinp(message, session);
            case JOIN_OBSERVER -> joino(message, session);
            case MAKE_MOVE -> move(message);
            case LEAVE -> leave(message);
            case RESIGN -> resign(message);
        }
    }

    private void joinp(String message, Session session) throws Exception {
        var player = new Gson().fromJson(message, JoinPlayer.class);
        var connection = new Connection(player.getAuthString(), session, player.playerColor);
        if (!dataAccess.checkAuth(player.getAuthString())) {
            var error = new Gson().toJson(new Error("Error: game doesn't exist"));
            connection.send(error);
            return;
        }
        var game = dataAccess.getGame(player.gameID);
        if (game == null) {
            var error = new Gson().toJson(new Error("Error: game doesn't exist"));
            connection.send(error);
            return;
        }
        if (player.playerColor.equals(ChessGame.TeamColor.WHITE)) {
            if (game.whiteUsername() == null || !game.whiteUsername().equals(dataAccess.getAuth(player.getAuthString()).username())) {
                var error = new Gson().toJson(new Error("Error: already taken"));
                connection.send(error);
                return;
            }
        } else if (player.playerColor.equals(ChessGame.TeamColor.BLACK)) {
            if (game.blackUsername() == null || !game.blackUsername().equals(dataAccess.getAuth(player.getAuthString()).username())) {
                var error = new Error("Error: already taken");
                connection.send(new Gson().toJson(error));
                return;
            }
        }
        connections.add(Integer.toString(player.gameID), connection);
        var board = new LoadGame(game.game());
        connection.send(new Gson().toJson(board));
        var notifyMsg = String.format("%s joined as %s", dataAccess.getAuth(player.getAuthString()).username(), player.playerColor);
        var notification = new Notification(notifyMsg);
        connections.broadcast(player.getAuthString(), notification);
    }

    private void joino(String message, Session session) throws Exception {
        var observer = new Gson().fromJson(message, JoinObserver.class);
        var connection = new Connection(observer.getAuthString(), session, null);
        if (!dataAccess.checkAuth(observer.getAuthString())) {
            var error = new Gson().toJson(new Error("Error: game doesn't exist"));
            connection.send(error);
            return;
        }
        var game = dataAccess.getGame(Integer.parseInt(observer.gameID));
        if (game == null) {
            var error = new Gson().toJson(new Error("Error: game doesn't exist"));
            connection.send(error);
            return;
        }
        connections.add(observer.gameID, connection);
        var board = new LoadGame(game.game());
        connection.send(new Gson().toJson(board));
        var notifyMsg = String.format("%s joined as an observer", dataAccess.getAuth(observer.getAuthString()).username());
        var notification = new Notification(notifyMsg);
        connections.broadcast(observer.getAuthString(), notification);
    }

    public void move(String message) throws Exception {
        indices.put("1", "a");
        indices.put("2", "b");
        indices.put("3", "c");
        indices.put("4", "d");
        indices.put("5", "e");
        indices.put("6", "f");
        indices.put("7", "g");
        indices.put("8", "h");
        var makeMove = new Gson().fromJson(message, MakeMove.class);
        var startPosition = indices.get(Integer.toString(makeMove.move.getStartPosition().getRow())) +
                makeMove.move.getStartPosition().getRow();
        var endPosition = indices.get(Integer.toString(makeMove.move.getEndPosition().getRow())) +
                makeMove.move.getEndPosition().getRow();
        var game = dataAccess.getGame(makeMove.gameID);
        var connection = connections.getConnection(String.valueOf(makeMove.gameID), makeMove.getAuthString());
        if (game.game().isInStalemate(ChessGame.TeamColor.BLACK) || game.game().isInStalemate(ChessGame.TeamColor.WHITE)) {
            var error = new Gson().toJson(new Error("Error: The game is over. No more moves can be made."));
            connection.send(error);
        } else if (!game.game().getTeamTurn().equals(connection.playerColor)) {
            var error = new Gson().toJson(new Error("Error: It's not your turn"));
            connection.send(error);
        } else if (game.game().getBoard().getPiece(makeMove.move.getStartPosition()).getTeamColor().equals(connection.playerColor)) {
            var error = new Gson().toJson(new Error("Error: You are not authorized to move this piece"));
            connection.send(error);
        } else if (!game.game().validMoves(makeMove.move.getStartPosition()).contains(makeMove.move)) {
            var error = new Gson().toJson(new Error("Error: This is an invalid move"));
            connection.send(error);
//        } else if () {
//            try to move as an observer
        } else {
            game.game().makeMove(makeMove.move);
            var board = new LoadGame(game.game());
            for (Connection conn : connections.getGame(String.valueOf(makeMove.gameID))) {
                try {
                    conn.send(new Gson().toJson(board));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            var notifyMsg = String.format("%s moved from %s to %s", dataAccess.getAuth(makeMove.getAuthString()).username(), startPosition, endPosition);
            var notification = new Notification(notifyMsg);
            connections.broadcast(makeMove.getAuthString(), notification);
            if (game.game().isInStalemate(ChessGame.TeamColor.BLACK) || game.game().isInStalemate(ChessGame.TeamColor.BLACK)) {
                connections.removeGame(String.valueOf(makeMove.gameID));
//                dataAccess.delGame(makeMove.gameID);
            }
        }
    }

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

