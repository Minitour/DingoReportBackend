package mobi.newsound.database;

import net.ucanaccess.jdbc.UcanaccessDriver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Antonio Zaitoun on 09/02/2018.
 */
public class DBConnection implements AutoCloseable {

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public DBConnection(String path) throws SQLException,IOException{
        connection = getUcanaccessConnection(path);
    }

    private Connection getUcanaccessConnection(String pathNewDB) throws SQLException,
            IOException {
        String url = UcanaccessDriver.URL_PREFIX + pathNewDB+";Columnorder=Display;Showschema=true";

        return DriverManager.getConnection(url, null, null);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        cl();
    }

    @Override
    public void close() throws Exception {
        cl();
    }

    private void cl() throws SQLException{
        if(connection != null && !connection.isClosed())
            connection.close();

    }
}
