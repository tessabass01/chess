package server.websocket;

//import webSocketMessages.serverMessages.*;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class ConnectionManager {
//    public final ConcurrentHashMap<String, ArrayList<Connection>> connections = new ConcurrentHashMap<>();
//
//    public void add(String gameID, Connection connection) {
//        if (!connections.containsKey(gameID)) {
//            connections.put(gameID, new ArrayList<>());
//            connections.get(gameID).add(connection);
//        } else {
//            connections.get(gameID).add(connection);
//        }
//    }
//
//    public Connection getConnection(String gameID, String authToken) {
//        for (var conn : connections.get(gameID)) {
//            if (conn.authToken.equals(authToken)) {
//                return conn;
//            }
//        }
//        return null;
//    }
//
////    public ArrayList<Connection> getGame(String gameID) {
////        return connections.get(gameID);
////    }
//
//    public ArrayList<Connection> getGame(String gameID) {
//        synchronized (connections) {
//            return connections.get(gameID);
//        }
//    }
//
//    public void removeConnection(String gameID, String authToken) {
//        var gameConnections = connections.get(gameID);
//        var copy = new ArrayList<>(gameConnections);
//        for (var conn : copy) {
//            if (conn.authToken.equals(authToken)) {
//                connections.get(gameID).remove(conn);
//                break;
//            }
//        }
//    }
//
//    public void removeGame(String gameID) {
//        connections.remove(gameID);
//    }
//
//    public void broadcast(String excludeAuthToken, Notification notification, int gameID) throws IOException {
//        var removeMap = new ConcurrentHashMap<String, Connection>();
//        var strGameID = Integer.toString(gameID);
//        var game = connections.get(strGameID);
//        for (var connection : game) {
//            if (connection.session.isOpen()) {
//                if (!connection.authToken.equals(excludeAuthToken)) {
//                    connection.send(notification.toString());
//                }
//            } else {
//                removeMap.put(strGameID, connection);
//            }
//        }
//
//        // Clean up any connections that were left open.
//        for (var closedConnection : removeMap.keySet()) {
//            connections.remove(closedConnection);
//        }
//    }
//}

import webSocketMessages.serverMessages.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    // Map to store connections for each session
    private final ConcurrentHashMap<String, Map<String, Connection>> connections = new ConcurrentHashMap<>();

    public void add(String gameID, Connection connection) {
        connections.computeIfAbsent(gameID, k -> new ConcurrentHashMap<>()).put(connection.authToken, connection);
    }

    public Connection getConnection(String gameID, String authToken) {
        return connections.getOrDefault(gameID, new ConcurrentHashMap<>()).get(authToken);
    }

    public ArrayList<Connection> getGame(String gameID) {
        return new ArrayList<>(connections.getOrDefault(gameID, new ConcurrentHashMap<>()).values());
    }

    public void removeConnection(String gameID, String authToken) {
        connections.getOrDefault(gameID, new ConcurrentHashMap<>()).remove(authToken);
    }

    public ConcurrentHashMap.KeySetView<String, Map<String, Connection>> getKeys() {
        return connections.keySet();
    }

    public void removeGame(String gameID) {
        connections.remove(gameID);
    }

    public void broadcast(String excludeAuthToken, Notification notification, String gameID) throws IOException {
        var gameConnections = connections.getOrDefault(gameID, new ConcurrentHashMap<>());
        for (var connection : gameConnections.values()) {
            if (connection.session.isOpen() && !connection.authToken.equals(excludeAuthToken)) {
                connection.send(notification.toString());
            }
        }
    }
}
