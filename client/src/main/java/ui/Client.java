package ui;

import exception.ResponseException;
import model.UserData;
import server.ServerFacade;

import java.util.Arrays;

public class Client {
    private final ServerFacade serverFacade;
    private String currentUser;
    private final String serverUrl;
    private State state;

    public Client(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        currentUser = null;
        state = State.SIGNEDOUT;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
//                case "list" -> listGames();
//                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            state = State.SIGNEDIN;
            var user = new UserData(params[0], params[1], null);
            var authToken = serverFacade.login(user);
            currentUser = user.username();
            return String.format("Get ready to play some chess, %s!", currentUser);
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            var user = new UserData(params[0], params[1], params[2]);
            String authToken = serverFacade.addUser(user);
            return "Thank you for registering with us!";
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String clearDB() throws ResponseException {
        try {
            serverFacade.clearDB();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        return "Database successfully cleared";
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                    - help
                    """;
        }
        return """
                - list-games
                - create-game <game name>
                - join-game <game ID> <WHITE|BLACK>
                - join-observer <game ID>
                - logout
                - help
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}