package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Objects;

public class ServerFacade {
    private String url;
    public ArrayList<UserData> users;
    public ArrayList<String> usernames;


    public ServerFacade(String url) {
        this.url = url;
        users = new ArrayList<>();
        usernames = new ArrayList<>();
    }


    public String addUser(UserData user) throws ResponseException {
         if (!usernames.contains(user.username())) {
            users.add(user);
            usernames.add(user.username());
        }
        var path = "/user";
        return this.makeRequest("POST", path, user, String.class,null);
    }

    public String login(UserData user) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, user, String.class, null);
    }

    public String logout(String authToken) throws ResponseException {
        var path = "/session";
        return this.makeRequest("DELETE", path, null, String.class, authToken);
    }

    public String createGame(String gameName, String authToken) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, gameName, String.class, authToken);
    };

    public String joinObserver(String gameID, String authToken) throws ResponseException {
        var path = "/game";
        var req = new JoinData(Integer.parseInt(gameID), null);
        return this.makeRequest("PUT", path, req, String.class, authToken);
    }

    public String joinGame(String gameID, String color, String authToken) throws ResponseException {
        var path = "/game";
        var req = new JoinData(Integer.parseInt(gameID), color);
        return this.makeRequest("PUT", path, req, String.class, authToken);
    }

    public GameData[] listGames(String authToken) throws ResponseException {
        var path = "/game";
        record listGameResponse(GameData[] games) {
        }
        var response = this.makeRequest("GET", path, null, listGameResponse.class, authToken);
        return response.games();
    }

        public void clearDB() throws ResponseException {
        users.clear();
        usernames.clear();
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    public ArrayList<UserData> listUsers() {
        return users;
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authHeader) throws ResponseException {
        try {
            URL url = (new URI(this.url + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http, authHeader);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http, String authHeader) throws IOException {
        if (authHeader != null) {
            http.addRequestProperty("authorization", authHeader);
        }
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
