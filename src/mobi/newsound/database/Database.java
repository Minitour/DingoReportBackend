package mobi.newsound.database;

import mobi.newsound.auth.AuthContext;
import mobi.newsound.model.Officer;
import mobi.newsound.model.Report;
import mobi.newsound.model.Unit;
import mobi.newsound.model.Volunteer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static mobi.newsound.utils.Config.config;

/**
 * Created by Antonio Zaitoun on 09/02/2018.
 */
public class Database implements DataStore {

    private DBConnection connection;

    private Database() throws IOException, SQLException {
        String file_path = config.get("db").getAsJsonObject().get("access_file_location").getAsString();
        connection = new DBConnection(file_path);
    }

    @Override
    public AuthContext signIn(String email, String password_raw) throws DSException {
        return null;
    }

    @Override
    public boolean updatePassword(AuthContext context, String currentPassword, String newPassword) throws DSException {
        return false;
    }

    @Override
    public String resetPassword(AuthContext context) {
        return null;
    }

    @Override
    public boolean createReport(AuthContext context, Report report) throws DSException {
        return false;
    }

    @Override
    public boolean createUnit(AuthContext context, Unit unit) throws DSException {
        return false;
    }

    @Override
    public boolean assignLeaderToUnit(AuthContext context, Officer officer, Unit unit) throws DSException {
        return false;
    }

    @Override
    public boolean assignOfficerToUnit(AuthContext context, Officer officer, Unit unit) throws DSException {
        return false;
    }

    @Override
    public boolean createVolunteer(AuthContext context, Volunteer volunteer) throws DSException {
        return false;
    }

    @Override
    public List<Report> getReports(AuthContext context, int count, int page) throws DSException {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
