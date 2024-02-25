package server;

import com.google.gson.Gson;
import dataAccess.DataAccess;
import model.AuthData;
import model.UserData;
import spark.*;
import dataAccess.DataAccessException;
import service.*;

public class Server {

    private final UserService service;

    public Server(DataAccess dataAccess) {

        service = new UserService(dataAccess);

    }
//
//    public Server(UserService service) {
//        this.service = service;
//    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
//        Spark.post("/session", this::login);
//        Spark.delete("/session", this::logout);
//        Spark.get("/game", this::listGames);
//        Spark.get("/game", this::createGame);
//        Spark.put("/game", this::joinGame);
//        Spark.delete("/db", this::clearDB);

//        Spark.exception(DataAccessException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object registerUser(Request req, Response res) throws DataAccessException {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        var authData = service.registerUser(user);
        return new Gson().toJson(authData);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
