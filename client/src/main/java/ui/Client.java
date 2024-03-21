package ui;

import exception.ResponseException;
import model.UserData;
import server.ServerFacade;

import java.util.Arrays;

import static java.lang.String.join;

public class Client {
    private final ServerFacade serverFacade;
    private String currentUser;
    private String currentAuth;
    private final String serverUrl;
    private State state;

    public Client(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        currentUser = null;
        currentAuth = null;
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
                case "clear" -> clearDB();
                case "create-game" -> createGame(params);
//                case "list" -> listGames();
                case "logout" -> logout();
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
            currentAuth = serverFacade.login(user);
            currentUser = user.username();
            return String.format("Get ready to play some chess, %s!", currentUser);
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            var user = new UserData(params[0], params[1], params[2]);
            currentAuth = serverFacade.addUser(user);
            state = State.SIGNEDIN;
            return "Thank you for registering with us!";
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String clearDB() throws ResponseException {
        serverFacade.clearDB();
        return "Database successfully cleared";
    }

    public String logout() throws ResponseException {
        assertSignedIn();
        serverFacade.logout(currentAuth);
        state = State.SIGNEDOUT;
        return "You have successfully logged out";
        }

    public String createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            var gameName = join("-", params);
            var gameID = serverFacade.createGame(gameName, currentAuth);
            return String.format("You have created a new game called %s. The game ID is %s, invite a friend!", gameName, gameID);
        }
        throw new ResponseException(400, "Expected: create-game <game name>");
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