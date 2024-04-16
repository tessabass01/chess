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
import java.sql.Array;
import java.util.ArrayList;
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
            case REDRAW -> redraw(message);
            case LEGAL_MOVES -> legalMoves(message);
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
        var board = new LoadGame(game.game(), connection.playerColor);
        connection.send(new Gson().toJson(board));
        var notifyMsg = String.format("%s joined as %s", dataAccess.getAuth(player.getAuthString()).username(), player.playerColor);
        var notification = new Notification(notifyMsg);
        connections.broadcast(player.getAuthString(), notification, String.valueOf(player.gameID));
    }

    private void joino(String message, Session session) throws Exception {
        var observer = new Gson().fromJson(message, JoinObserver.class);
//        var connection = new Connection(observer.getAuthString(), session, null);
        if (!dataAccess.checkAuth(observer.getAuthString())) {
            var error = new Gson().toJson(new Error("Error: You are not authorized"));
            var connection = new Connection(observer.getAuthString(), session, null);
            connection.send(error);
            return;
        }
        var game = dataAccess.getGame(Integer.parseInt(observer.gameID));
        if (game == null) {
            var error = new Gson().toJson(new Error("Error: game doesn't exist"));
            var connection = new Connection(observer.getAuthString(), session, null);
            connection.send(error);
            return;
        }
        var connection = new Connection(observer.getAuthString(), session, null);
        connections.add(observer.gameID, connection);
        var board = new LoadGame(game.game(), ChessGame.TeamColor.WHITE);
        connection.send(new Gson().toJson(board));
        var notifyMsg = String.format("%s joined as an observer", dataAccess.getAuth(observer.getAuthString()).username());
        var notification = new Notification(notifyMsg);
        connections.broadcast(observer.getAuthString(), notification, observer.gameID);
    }

    public void move(String message) throws Exception {
        indices.put("1", "h");
        indices.put("2", "g");
        indices.put("3", "f");
        indices.put("4", "e");
        indices.put("5", "d");
        indices.put("6", "c");
        indices.put("7", "b");
        indices.put("8", "a");
        var makeMove = new Gson().fromJson(message, MakeMove.class);
        var startPosition = indices.get(Integer.toString(makeMove.move.getStartPosition().getColumn())) +
                makeMove.move.getStartPosition().getRow();
        var endPosition = indices.get(Integer.toString(makeMove.move.getEndPosition().getColumn())) +
                makeMove.move.getEndPosition().getRow();
        var game = dataAccess.getGame(makeMove.gameID);
        var connection = connections.getConnection(String.valueOf(makeMove.gameID), makeMove.getAuthString());
        if (game.game().isInCheckmate(ChessGame.TeamColor.BLACK) || game.game().isInCheckmate(ChessGame.TeamColor.WHITE) || game.game().getTeamTurn() == ChessGame.TeamColor.GAME_OVER) {
            var error = new Gson().toJson(new Error("Error: The game is over. No more moves can be made."));
            connection = connections.getConnection(String.valueOf(makeMove.gameID), makeMove.getAuthString());
            connection.send(error);
        } else if (!game.game().getTeamTurn().equals(connection.playerColor)) {
            var error = new Gson().toJson(new Error("Error: It's not your turn"));
            connection = connections.getConnection(String.valueOf(makeMove.gameID), makeMove.getAuthString());
            connection.send(error);
        } else if (!game.game().getBoard().getPiece(makeMove.move.getStartPosition()).getTeamColor().equals(connection.playerColor)) {
            var error = new Gson().toJson(new Error("Error: You are not authorized to move this piece"));
            connection = connections.getConnection(String.valueOf(makeMove.gameID), makeMove.getAuthString());
            connection.send(error);
        } else if (!game.game().validMoves(makeMove.move.getStartPosition()).contains(makeMove.move)) {
            var error = new Gson().toJson(new Error("Error: This is an invalid move"));
            connection = connections.getConnection(String.valueOf(makeMove.gameID), makeMove.getAuthString());
            connection.send(error);
        } else if (!dataAccess.getAuth(makeMove.getAuthString()).username().equals(game.whiteUsername()) && !dataAccess.getAuth(makeMove.getAuthString()).username().equals(game.blackUsername())) {
            var error = new Gson().toJson(new Error("Error: You cannot make moves as an observer"));
            connection = connections.getConnection(String.valueOf(makeMove.gameID), makeMove.getAuthString());
            connection.send(error);
        } else {
            game.game().makeMove(makeMove.move);
//            var board = new LoadGame(game.game(), connection.playerColor);
            var gameConn = connections.getGame(String.valueOf(makeMove.gameID));
            for (Connection conn : gameConn) {
                try {
                    LoadGame board;
                    if (conn.playerColor == null) {
                        board = new LoadGame(game.game(), ChessGame.TeamColor.WHITE);
                    } else {
                        board = new LoadGame(game.game(), conn.playerColor);
                    }
                    conn.send(new Gson().toJson(board));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            var notifyMsg = String.format("%s moved from %s to %s", dataAccess.getAuth(makeMove.getAuthString()).username(), startPosition, endPosition);
            var notification = new Notification(notifyMsg);
            connections.broadcast(makeMove.getAuthString(), notification, String.valueOf(makeMove.gameID));
            if (game.game().isInCheck(ChessGame.TeamColor.BLACK)) {
                var checkMsg = "black is in check";
                var checkNotification = new Notification(checkMsg);
                connections.broadcast("", checkNotification, String.valueOf(makeMove.gameID));
            }
            if (game.game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
                var checkMsg = "white is in check";
                var checkNotification = new Notification(checkMsg);
                connections.broadcast("", checkNotification, String.valueOf(makeMove.gameID));
            }
                dataAccess.updateGame(makeMove.gameID, game.game());
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
        connections.broadcast(leaving.getAuthString(), notification, String.valueOf(leaving.gameID));
    }

    private void resign(String message) throws IOException, DataAccessException {
        var resigning = new Gson().fromJson(message, Resign.class);
        var game = dataAccess.getGame(resigning.gameID);
        if (!dataAccess.getAuth(resigning.getAuthString()).username().equals(game.whiteUsername()) && !dataAccess.getAuth(resigning.getAuthString()).username().equals(game.blackUsername())) {
            var error = new Gson().toJson(new Error("Error: Observers cannot resign"));
            var connection = connections.getConnection(String.valueOf(resigning.gameID), resigning.getAuthString());
            connection.send(error);
        } else if (game.game().getTeamTurn().equals(ChessGame.TeamColor.GAME_OVER)) {
            var error = new Gson().toJson(new Error("Error: This game is already over"));
            var connection = connections.getConnection(String.valueOf(resigning.gameID), resigning.getAuthString());
            connection.send(error);
//            connections.removeGame(String.valueOf(resigning.gameID));
        } else {
            game.game().setTeamTurn();
            var notifyMsg = String.format("%s resigned", dataAccess.getAuth(resigning.getAuthString()).username());
            var notification = new Notification(notifyMsg);
            connections.broadcast("", notification, String.valueOf(resigning.gameID));
            dataAccess.updateGame(resigning.gameID, game.game());
        }
    }

    public void clear() {
        var keyList = new ArrayList<String>();
        keyList.addAll(connections.getKeys());
        for (var key : keyList) {
            connections.removeGame(key);
        }
    }

    public void redraw(String message) throws DataAccessException, IOException {
        var redraw = new Gson().fromJson(message, Redraw.class);
        var connection = connections.getConnection(String.valueOf(redraw.gameID), redraw.getAuthString());
        var game = dataAccess.getGame(redraw.gameID);
        LoadGame board;
        if  (connection.playerColor == null) {
            board = new LoadGame(game.game(), ChessGame.TeamColor.WHITE);
        } else {
            board = new LoadGame(game.game(), connection.playerColor);
        }
        connection.send(new Gson().toJson(board));
    }

    public void legalMoves(String message) throws DataAccessException, IOException {
        var legalMoves = new Gson().fromJson(message, LegalMoves.class);
        var connection = connections.getConnection(String.valueOf(legalMoves.gameID), legalMoves.getAuthString());
        var game = dataAccess.getGame(legalMoves.gameID);
        var moves = game.game().validMoves(legalMoves.startPosition);
        Highlight board;
        if  (connection.playerColor == null) {
            board = new Highlight(game.game(), ChessGame.TeamColor.WHITE, legalMoves.startPosition, moves);
        } else {
            board = new Highlight(game.game(), connection.playerColor, legalMoves.startPosition, moves);
        }
        connection.send(new Gson().toJson(board));
    }
}

