package service;


import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class DataService {
    private final DataAccess dataAccess;

    public DataService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clearDB() throws DataAccessException {
        dataAccess.clearDB();
    }
}
