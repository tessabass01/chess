package service;


import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.Collection;

public class DataService {
    private final DataAccess dataAccess;

    public DataService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clearDB() throws DataAccessException {
        dataAccess.clearDB();
    }

    public Collection<String> listUsers() throws DataAccessException {
        return dataAccess.listUsers();
    }
}
