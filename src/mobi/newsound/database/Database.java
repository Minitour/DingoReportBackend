package mobi.newsound.database;

import javafx.util.Pair;
import mobi.newsound.model.Officer;
import mobi.newsound.model.Report;
import mobi.newsound.model.Team;
import mobi.newsound.model.Volunteer;
import net.ucanaccess.jdbc.UcanaccessDriver;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.*;
import java.util.Date;

import static mobi.newsound.utils.Config.config;

/**
 * Created by Antonio Zaitoun on 09/02/2018.
 */
class Database implements DataStore {

    final static int MAX_ALLOWED_SESSIONS = config.get("user").getAsJsonObject().get("max_allowed_sessions").getAsInt();
    final static long MAX_TIME_OUT = config.get("user").getAsJsonObject().get("session_time_out").getAsInt();

    private Connection connection;

    protected Database() throws SQLException {
        String file_path = config.get("db").getAsJsonObject().get("access_file_location").getAsString();
        connection = getUcanaccessConnection(file_path);
    }

    @Override
    public AuthContext signIn(String email, String password_raw) throws DSException {
        //Context Level: NONE
        try {
            List<Map> user = get("SELECT * FROM ACCOUNTS WHERE EMAIL = ?",email);
            if(user.size() != 1)
                //no such user
                throw new DSAuthException("Account Does Not Exist");
            else{
                //check password
                boolean doesMatch = BCrypt.checkpw(password_raw,(String) user.get(0).get("PASSWORD"));
                if(doesMatch){
                    //password is correct
                    String token = TokenGenerator.generateToken();
                    String id = (String) user.get(0).get("ID");
                    insert("SESSIONS",
                            new Column("ID",id),
                            new Column("SESSION_TOKEN",token),
                            new Column("CREATION_DATE",new Date())
                    );

                    //clean up old sessions
                    long timestamp = System.currentTimeMillis();
                    List<Map> sessions = get("SELECT * FROM SESSIONS WHERE ID = ? ORDER BY CREATION_DATE ASC",id);
                    Set<String> toRemove = new HashSet<>();

                    for(Map<String,Object> map : sessions){
                        String sessionToken = (String) map.get("SESSION_TOKEN");

                        if(MAX_TIME_OUT > 0){
                            //check if timed out
                            Date date = (Date) map.get("CREATION_DATE");
                            if(timestamp - date.getTime() > MAX_TIME_OUT)
                                toRemove.add(sessionToken);
                            continue;
                        }

                        if(sessions.size() - toRemove.size() > MAX_ALLOWED_SESSIONS)
                            toRemove.add(sessionToken);

                    }
                    if(toRemove.size() > 0)
                        deleteMany("SESSIONS",
                                "SESSION_TOKEN in (#)",
                                toRemove.toArray(new String[toRemove.size()]));

                    return new AuthContext(id,token);

                }else throw new DSAuthException("Invalid Password");
            }
        } catch (SQLException e) {
            throw new DSFormatException(e.getMessage());
        }
    }

    @Override
    public boolean updatePassword(AuthContext context, String currentPassword, String newPassword) throws DSException {
        try{
            //Context Level: ANY
            if(isContextValid(context) != -1){
                List<Map> user = get("SELECT (PASSWORD) FROM ACCOUNTS WHERE ID = ?",context.id);
                if(user.size() == 1){
                    String hashedPassword = (String) user.get(0).get("PASSWORD");
                    if(BCrypt.checkpw(currentPassword,hashedPassword))
                        //update password
                        return update("ACCOUNTS",
                                new Where("ID = ?",context.id),
                                new Column("PASSWORD",BCrypt.hashpw(newPassword,BCrypt.gensalt())));

                    else
                        throw new DSAuthException("Incorrect password");

                }else
                    throw new DSAuthException("Account Not Found");

            }else throw new DSAuthException("Invalid Context");
        }catch (SQLException e){
            throw new DSFormatException(e.getMessage());
        }
    }

    @Override
    public String resetPassword(AuthContext context) throws DSException {
        //Context Level: ANY
        return null;
    }

    @Override
    public boolean createReport(AuthContext context, Report report) throws DSException {
        //Context Level: 4

        //validate context
        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },4);

        //insert report object
        long id = 0;
        try {
            //TODO: test if the returned id is correct.
            id = insert("TblReports"/*TODO: add columns*/);

            //for each violation in report:
            report.getViolations().forEach(violation -> {

                //generate id
                String violationId = ObjectId.generate();

                //insert violation

            });

        } catch (SQLException e) {
            //roll back changes
            try {
                delete("TblVideoViolations","report = ?",id);
                delete("TblImageViolations","report = ?",id);
                delete("TblReports","reportNum = ?",id);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw new DSFormatException(e.getMessage());
        }

        return false;
    }

    @Override
    public boolean createTeam(AuthContext context, Team team) throws DSException {
        //Context Level: 1
        return false;
    }

    @Override
    public boolean assignLeaderToTeam(AuthContext context, Officer officer, Team team) throws DSException {
        //Context Level: 1
        return false;
    }

    @Override
    public boolean assignOfficerToTeam(AuthContext context, Officer officer, Team team) throws DSException {
        //Context Level: 1
        return false;
    }

    @Override
    public boolean createVolunteer(AuthContext context, Volunteer volunteer) throws DSException {
        //Context Level: 3
        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },3);
        try{
            //create account

            //create volunteer
        }catch (Exception e){

        }
        return false;
    }

    @Override
    public List<Report> getReports(AuthContext context, int count, int page) throws DSException {
        //Context Level: 1,2,4

        //if role is not one of the following (0,1,2,4) then throw an exception.
        final int role = isContextValidFor(context,(roleId -> {
            if(roleId == -1)
                throw new DSAuthException("Invalid Context");
        }),1,2,4);

        try {
            //limit for pagination
            String limit = " LIMIT " + (page - 1) * count + ", " + count;

            String select = "SELECT * FROM TblReports";

            String where = null;
            //context is valid
            switch (role) {
                case 0://superuser
                case 1://high rank officer
                    where = "";
                    break;
                case 2://officer
                    int unit = (int) get("SELECT team FROM TblOfficers WHERE account_id = ?",context.id).get(0).get("team");
                    where = " WHERE team = "+unit;
                    break;
                case 4://volunteer
                    where = " WHERE volunteer = "+context.id;
                    break;
            }
            String query = select + where + limit;

            List<Map> data = get(query);
            //TODO: convert data to List<Report>
            return null;
        }catch (SQLException e){
            throw new DSFormatException(e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    /**
     * This method checks if the given context is valid and returns the role id for that given context.
     * In other words, this method validates the session and then returns the role of the user.
     * @param context The auth context.
     * @return The role id of a given context or -1 if invalid.
     */
    private int isContextValid(AuthContext context){
        try {
            List<Map> data = get("SELECT Accounts.ROLE_ID " +
                    "FROM Accounts INNER JOIN Sessions ON Accounts.ID = Sessions.ID " +
                    "WHERE (((Sessions.ID)= ? ) AND ((Sessions.SESSION_TOKEN)= ? ))", context.id, context.sessionToken);
            return data.size() == 0 ? -1 : (Integer) data.get(0).get("ROLE_ID");
        } catch (SQLException e) {
            return -1;
        }
    }

    /**
     *
     * @param context The auth context to check.
     * @param validator The closure to activate when we get a validation.
     * @param roles The roles that are allowed for this context.
     * @return The role id if valid, else -1.
     * @throws DSException
     */
    private int isContextValidFor(AuthContext context,ContextValidator validator,int...roles) throws DSException{
        int roleId = isContextValid(context);

        //invalid role id.
        if(roleId == -1) {
            validator.validWithRoleId(-1);
            return -1;
        }

        //if superuser always return true.
        if(roleId == 0){
            validator.validWithRoleId(0);
            return 0;
        }

        //check if the role id found is included in one of the roles given.
        for(int role : roles){
            if (role == roleId){
                validator.validWithRoleId(role);
                return role;
            }
        }

        validator.validWithRoleId(-1);
        return -1;
    }

    private Connection getUcanaccessConnection(String pathNewDB) throws SQLException{
        String url = UcanaccessDriver.URL_PREFIX + pathNewDB+";Columnorder=Display;Showschema=true";
        return DriverManager.getConnection(url, null, null);
    }

    /**
     * Use this method to delete entries.
     *
     * Usage Example:
     *      @code {
     *
     *          //Example for deleting a session via token or id
     *          delete("SESSIONS","ID = ? OR TOKEN = ?",id,token);
     *
     *          //Simple Example for Deleting an account with a certain email.
     *          delete("ACCOUNTS","EMAIL = ?",email);
     *
     *          //Deleting multiple accounts with ids 32,542,22
     *          delete("ACCOUNTS","ID in (?, ?, ?)",32,542,22);
     *      }
     *
     * @param table The name of the table.
     * @param where The predicate/condition.
     * @param values The values.
     * @throws SQLException
     */
    protected boolean delete(String table, String where, Object... values) throws SQLException{

        String query = "DELETE FROM "+ table +" WHERE "+ where;

        PreparedStatement statement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);

        int index = 1;
        for(Object obj : values)
            statement.setObject(index++,obj);

        return statement.executeUpdate() != 0;
    }

    /**
     * Use this method to delete multiple entries. Instead of inserting many wildcards use `#` operator.
     *
     * @code {
     *
     *          deleteMany("ACCOUNTS","ID in (#)",32,542,22)
     *
     *          //Is the same as:
     *          delete("ACCOUNTS","ID in (?, ?, ?)",32,542,22);
     *      }
     *
     * @param table
     * @param where
     * @param values
     * @return
     * @throws SQLException
     */
    protected boolean deleteMany(String table,String where,String... values) throws SQLException{
        StringBuilder builder = new StringBuilder();

        for(String ignored : values)
            builder.append("?").append(",");

        builder.deleteCharAt(builder.length()-1);

        return delete(table,where.replace("#",builder.toString()),values);
    }

    /**
     *
     * Use this method to insert data into a certain table.
     *
     * Usage Example:
     *      @code {
     *          insert("SESSIONS",
     *               new Column("ID",id),
     *               new Column("SESSION_TOKEN",token),
     *               new Column("CREATION_DATE",new Date())
     *          );
     *      }
     *
     *      @see Column
     *
     *
     * @param table The name of the table.
     * @param values Var args of Pairs of Type (String:Object). Use Column for ease of use.
     * @param <T> The type of the generated key.
     * @return
     * @throws SQLException
     */
    protected<T> T insert(String table, Pair<String,Object>...values) throws SQLException {

        StringBuilder builder1 = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();

        for(Pair<String,Object> value : values){
            builder1.append(value.getKey()).append(",");
            builder2.append("?").append(",");
        }

        builder1.deleteCharAt(builder1.length() - 1);
        builder2.deleteCharAt(builder2.length() - 1);

        String query  ="INSERT INTO "+table+" ("+builder1.toString()+") VALUES ("+builder2.toString()+");";

        PreparedStatement statement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);

        int index = 1;
        for(Pair<String,Object> obj : values)
            statement.setObject(index++,obj.getValue());

        statement.executeUpdate();

        ResultSet rs = statement.getGeneratedKeys();

        return rs.next() ? (T) rs.getObject(1) : null;
    }

    /**
     * Use this method to make Database Queries.
     *
     * Usage Example:
     *      @code { get("SELECT * FROM USERS WHERE EMAIL = ?",email) }
     *
     * @param query The Query String.
     * @return A List Of Hash Maps of Type (String:Object)
     * @throws SQLException
     */
    protected List<Map> get(String query,String...args) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);

        int index = 1;
        for(String val : args)
            statement.setObject(index++,val);

         ResultSet set = statement.executeQuery();

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

    /**
     * Use this method to update an existing entry.
     *
     * @param table The table
     * @param where The condition
     * @param values The values to set/update
     * @return
     * @throws SQLException
     */
    private boolean update(String table,Where where, Pair<String,Object>...values) throws SQLException {

        StringBuilder builder = new StringBuilder();

        for(Pair<String,Object> value : values)
            builder.append(value.getKey()).append(" = ").append("?,");

        builder.deleteCharAt(builder.length()-1);

        String query  ="UPDATE "+table+" SET "+builder.toString()+" WHERE "+where.syntax;
        PreparedStatement statement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);

        int index = 1;
        for(Pair<String,Object> obj : values)
            statement.setObject(index++,obj.getValue());

        for(Object o : where.values)
            statement.setObject(index++,o);

        return statement.executeUpdate() != 0;
    }

    protected static class Column extends Pair<String,Object>{

        /**
         * Creates a new pair
         *
         * @param key   The key for this pair
         * @param value The value to use for this pair
         */
        public Column(String key, Object value) {
            super(key, value);
        }
    }

    @FunctionalInterface
    interface ContextValidator{
        void validWithRoleId(int roleId) throws DSException;
    }

    class Where {
        final String syntax;
        final Object[] values;

        public Where(String syntax, Object...values) {
            this.syntax = syntax;
            this.values = values;
        }
    }

}
