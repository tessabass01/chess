package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, ArrayList<Connection>> connections = new ConcurrentHashMap<>();

    public void add(String gameID, String authToken, Session session) {
        var connection = new Connection(authToken, session);
        if (!connections.containsKey(gameID)) {
            connections.put(gameID, new ArrayList<>());
            connections.get(gameID).add(connection);
        } else {
            connections.get(gameID).add(connection);
        }
    }

    public void remove(String gameID, String authToken) {
        var gameConnections = connections.get(gameID);
        for (var conn : gameConnections) {
            if (conn.authToken.equals(authToken)) {
                connections.get(gameID).remove(conn);
            }
        }
    }

    public void broadcast(String excludeAuthToken, Notification notification) throws IOException {
        var removeMap = new ConcurrentHashMap<String, Connection>();
        for (var gameID : connections.keySet()) {
            for (var connection : connections.get(gameID)) {
                if (connection.session.isOpen()) {
                    if (!connection.authToken.equals(excludeAuthToken)) {
                        connection.send(notification.toString());
                    }
                } else {
                    removeMap.put(gameID, connection);
                }
            }

            // Clean up any connections that were left open.
            for (var closedConnection : removeMap.keySet()) {
                connections.remove(closedConnection);
            }
        }
    }
}
