package server;

import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import dataAccess.MySqlDataAccess;
import model.*;
import spark.*;
import dataAccess.DataAccessException;
import service.*;
import java.sql.SQLException;
import java.util.Objects;


public class Server {
    private final UserService uservice;
    private final DataService dservice;
    private final GameService gservice;

    public Server() {
        DataAccess dataAccess = null;
        try {
            dataAccess = new MySqlDataAccess();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        uservice = new UserService(dataAccess);
        dservice = new DataService(dataAccess);
        gservice = new GameService(dataAccess);

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);

//        Spark.exception(DataAccessException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object registerUser(Request req, Response res) throws DataAccessException {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        var response = uservice.registerUser(user);
        if (response.contains("already taken")) {
            res.status(403);
            var error = new ErrorMessage("Error: already taken");
            return new Gson().toJson(error);
        } else if (response.contains("no password")) {
            res.status(400);
            var error = new ErrorMessage("Error: bad request");
            return new Gson().toJson(error);
        } else {
            res.status(200);
        }
        return response;
    }

    private Object login(Request req, Response res) throws DataAccessException {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        var authToken = uservice.login(user);
        var response = new Gson().toJson(authToken);
        if (response.contains("does not exist") || response.contains("wrong password")) {
            res.status(401);
            var error = new ErrorMessage("Error: unauthorized");
            return new Gson().toJson(error);
        } else {
            res.status(200);
            return response;
        }
    }

    private Object logout(Request req, Response res) throws DataAccessException {
        String authToken = new Gson().fromJson(req.headers("authorization"), String.class);
        var response = uservice.logout(authToken);
        if (Objects.equals(response, "unauthorized")) {
            res.status(401);
            var error = new ErrorMessage("Error: unauthorized");
            return new Gson().toJson(error);
        } else {
            res.status(200);
            return "";
        }
    }

    private Object createGame(Request req, Response res) throws DataAccessException {
        var gameData = new Gson().fromJson(req.body(), GameData.class);
        var authToken = new Gson().fromJson(req.headers("authorization"), String.class);
        var gameData2 = gservice.createGame(authToken, gameData.gameName());
        if (Objects.equals(gameData2.gameName(), "not logged in")) {
            res.status(401);
            var error = new ErrorMessage("Error: unauthorized");
            return new Gson().toJson(error);
        }
        var response = new Gson().toJson(gameData2);
        res.status(200);
        return response;
        }

        private Object listGames(Request req, Response res) throws DataAccessException {
            var gameData = new Gson().fromJson(req.body(), GameData.class);
            var authToken = new Gson().fromJson(req.headers("authorization"), String.class);
            var gameList = gservice.listGames(authToken);
            if (gameList.get("games") == null) {
                res.status(401);
                var error = new ErrorMessage("Error: unauthorized");
                return new Gson().toJson(error);
            }
            var response = new Gson().toJson(gameList);
            res.status(200);
            return response;
        }

    private Object joinGame(Request req, Response res) throws DataAccessException, SQLException {
        var gameData = new Gson().fromJson(req.body(), JoinData.class);
        var authToken = req.headers("authorization");
        var response = gservice.joinGame(authToken, gameData.gameID(), gameData.playerColor());
        if (Objects.equals(response, "unauthorized")) {
            res.status(401);
            var error = new ErrorMessage("Error: unauthorized");
            return new Gson().toJson(error);
        } else if (Objects.equals(response, "does not exist")) {
            res.status(400);
            var error = new ErrorMessage("Error: bad request");
            return new Gson().toJson(error);
        } else if (Objects.equals(response, "already taken")) {
            res.status(403);
            var error = new ErrorMessage("Error: already taken");
            return new Gson().toJson(error);
        }
        res.status(200);
        return "";
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        dservice.clearDB();
        res.status(200);
        return "";
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
