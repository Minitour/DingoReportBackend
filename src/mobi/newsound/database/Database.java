package mobi.newsound.database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import mobi.newsound.auth.AuthContext;
import mobi.newsound.model.Officer;
import mobi.newsound.model.Report;
import mobi.newsound.model.Unit;
import mobi.newsound.model.Volunteer;
import net.ucanaccess.jdbc.UcanaccessDriver;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mobi.newsound.utils.Config.config;

/**
 * Created by Antonio Zaitoun on 09/02/2018.
 */
class Database implements DataStore {



    private Connection connection;

    protected Database() throws SQLException {
        String file_path = config.get("db").getAsJsonObject().get("access_file_location").getAsString();
        connection = getUcanaccessConnection(file_path);
    }

    @Override
    public AuthContext signIn(String email, String password_raw) throws DSException {
        //Context Level: NONE

        //TODO: Validate email and password

        //TODO: if Valid, create session else throw auth exception

        return null;
    }

    @Override
    public boolean updatePassword(AuthContext context, String currentPassword, String newPassword) throws DSException {
        //Context Level: ANY
        return false;
    }

    @Override
    public String resetPassword(AuthContext context) {
        //Context Level: ANY
        return null;
    }

    @Override
    public boolean createReport(AuthContext context, Report report) throws DSException {
        //Context Level: 4
        return false;
    }

    @Override
    public boolean createUnit(AuthContext context, Unit unit) throws DSException {
        //Context Level: 1
        return false;
    }

    @Override
    public boolean assignLeaderToUnit(AuthContext context, Officer officer, Unit unit) throws DSException {
        //Context Level: 1
        return false;
    }

    @Override
    public boolean assignOfficerToUnit(AuthContext context, Officer officer, Unit unit) throws DSException {
        //Context Level: 1
        return false;
    }

    @Override
    public boolean createVolunteer(AuthContext context, Volunteer volunteer) throws DSException {
        //Context Level: 3
        return false;
    }

    @Override
    public List<Report> getReports(AuthContext context, int count, int page) throws DSException {
        //Context Level: 1,2,4
        return null;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    private int isContextValid(AuthContext context){
        try {
            List<Map> data = makeQuery("SELECT Accounts.ROLE_ID " +
                    "FROM Accounts INNER JOIN Sessions ON Accounts.ID = Sessions.ID " +
                    "WHERE (((Sessions.ID)=\""+context.id+"\") AND ((Sessions.SESSION_TOKEN)=\""+context.sessionToken+"\"));");
            return data.size() == 0 ? -1 : (Integer) data.get(0).get("ROLE_ID");
        } catch (SQLException e) {
            return -1;
        }
    }

    private Connection getUcanaccessConnection(String pathNewDB) throws SQLException{
        String url = UcanaccessDriver.URL_PREFIX + pathNewDB+";Columnorder=Display;Showschema=true";
        return DriverManager.getConnection(url, null, null);
    }

    private List makeQuery(String query) throws SQLException {
        ResultSet set = connection.createStatement().executeQuery(query);
        String[] columns = new String[set.getMetaData().getColumnCount()];

        for (int i = 1; i <= columns.length; i++ )
            columns[i - 1] = set.getMetaData().getColumnName(i);

        List<Map> data = new ArrayList<>();

        while (set.next()){
            Map<String,Object> map = new HashMap<>();
            for(String name : columns)
                map.put(name,set.getObject(name));

            data.add(map);

        }

        return data;
    }

}
