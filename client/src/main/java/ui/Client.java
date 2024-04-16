package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import server.ServerFacade;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;

import java.util.Arrays;
import java.util.Objects;
import static java.lang.String.join;

public class Client {

    private final NotificationHandler notificationHandler;
    private final ServerFacade serverFacade;
    private String currentUser;
    private String currentAuth;
    private String currentGameID;
    private State state;
    private boolean inGame;
    private boolean isPlayer;
    private WebSocketFacade ws;

    public Client(String serverUrl, NotificationHandler notificationHandler) {
        serverFacade = new ServerFacade(serverUrl);
        currentUser = null;
        currentAuth = null;
        currentGameID = null;

        this.notificationHandler = notificationHandler;
        state = State.SIGNEDOUT;
        inGame = false;
        isPlayer = false;
    }

    public String eval(String input) throws Exception {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "clear" -> clearDB();
                case "create-game" -> createGame(params);
                case "join-game" -> joinGame(params);
                case "join-observer" -> joinGame(params);
                case "list-games" -> listGames();
                case "logout" -> logout();
                case "quit" -> "quit";
                case "leave" -> leave();
                case "resign" -> resign();
                case "make-move" -> makeMove(params);
                case "redraw" -> redraw();
                case "show-moves" -> showMoves(params);
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
            currentAuth = serverFacade.addUser(user).authToken();
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
        assertSignedIn();
        if (params.length >= 1) {
            var gameName = join("-", params);
            var game = new GameData(null, null, null, gameName, new ChessGame());
            var gameRes = serverFacade.createGame(game, currentAuth);
            return String.format("You have created a new game called %s. The game ID is %s, invite a friend!", gameName, gameRes.gameID());
        }
        throw new ResponseException(400, "Expected: create-game <game name>");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        var games = serverFacade.listGames(currentAuth);
        return Arrays.toString(games);
    }


    public String joinGame(String... params) throws Exception {
        assertSignedIn();
        currentGameID = params[0];
        if (params.length == 1) {
            try {
                serverFacade.joinObserver(currentGameID, currentAuth);
                ws = new WebSocketFacade(notificationHandler, null);
                ws.observe(currentGameID, currentAuth);
                inGame = true;
                return "Enjoy the show!\n";
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Expected: join-observer <game ID>\n" +
                                                 "\t\t\t\tor\n" +
                                                 "\t\tjoin-game <game ID> <WHITE|BLACK>");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            if (params[1].equalsIgnoreCase("white")) {
                serverFacade.joinGame(currentGameID, "WHITE", currentAuth);
                ws = new WebSocketFacade(notificationHandler, "white");
                ws.joinGame(currentGameID, currentAuth, ChessGame.TeamColor.WHITE);
                inGame = true;
                isPlayer = true;
                return "Go get 'em, WHITE!\n";
            } else  if (Objects.equals(params[1], "black")) {
                serverFacade.joinGame(currentGameID, "BLACK", currentAuth);
                ws = new WebSocketFacade(notificationHandler, "black");
                ws.joinGame(currentGameID, currentAuth, ChessGame.TeamColor.BLACK);
                inGame = true;
                isPlayer = true;
                return "You got this, BLACK!\n";
            } else {
                throw new ResponseException(400, "Expected: join-game <game ID> <WHITE|BLACK>");
            }
        }
    }

    public String leave() throws Exception {
        ws.leave(currentGameID, currentAuth);
        ws = null;
        inGame = false;
        isPlayer = false;
        return "You left the game";
    }

    public String resign() throws Exception {
        ws.resign(currentGameID, currentAuth);
//        ws = null;
        inGame = false;
        isPlayer = false;
        return "";
    }

    public String makeMove(String... params) throws Exception {
        ws.makeMove(currentAuth, currentGameID, params[0]);
        return "";
    }

    public String redraw() throws Exception {
        ws.redraw(currentGameID, currentAuth);
        return "";
    }

    public String showMoves(String... params) throws Exception {
        ws.legalMoves(currentGameID, currentAuth, params[0]);
        return "";
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                    - help
                    """;
        } else if (inGame) {
            if (isPlayer) {
                return """
                    - redraw
                    - show-moves <start position of piece>
                    - make-move <start position><end position>
                    - leave
                    - resign
                    """;
            } else {
                return """
                    - redraw
                    - show-moves <start position of piece>
                    - leave
                    """;
            }
        } else {
            return """
                    - list-games
                    - create-game <game name>
                    - join-game <game ID> <WHITE|BLACK>
                    - join-observer <game ID>
                    - logout
                    - help
                    """;
        }
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must login");
        }
    }
}