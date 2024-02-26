package server;

import com.google.gson.Gson;
import dataAccess.DataAccess;
import model.AuthData;
import model.ErrorMessage;
import model.UserData;
import spark.*;
import dataAccess.DataAccessException;
import service.*;


public class Server {

    private final UserService uservice;
    private final DataService dservice;

    public Server(DataAccess dataAccess) {

        uservice = new UserService(dataAccess);
        dservice = new DataService(dataAccess);

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
//        Spark.get("/game", this::listGames);
//        Spark.get("/game", this::createGame);
//        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);

//        Spark.exception(DataAccessException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object registerUser(Request req, Response res) throws DataAccessException {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        var authData = uservice.registerUser(user);
        var response = new Gson().toJson(authData);
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
        var authData = uservice.login(user);
        var response = new Gson().toJson(authData);
        System.out.println(response);
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
        if (response == "unauthorized") {
            res.status(401);
            var error = new ErrorMessage("Error: unauthorized");
            return new Gson().toJson(error);
        } else {
            res.status(200);
            return "";
        }
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
