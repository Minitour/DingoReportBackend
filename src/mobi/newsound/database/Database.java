package mobi.newsound.database;

import mobi.newsound.controllers.MOTSService;
import mobi.newsound.model.*;
import mobi.newsound.utils.Auth;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.ucanaccess.jdbc.UcanaccessDriver;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
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
    final static String DB_LOCATION = config.get("db").getAsJsonObject().get("access_file_location").getAsString();
    final static String JASPER_BIN = config.get("RF_REPORT_BINARY").getAsString();
    private Connection connection;

    Database() throws SQLException {
        connection = getUcanaccessConnection(new File(DB_LOCATION).getAbsolutePath());
    }

    @Override
    public boolean isValid(AuthContext context) throws DSException {
        return isContextValid(context) != -1;
    }

    @Override
    public AuthContext signIn(String email, String password_raw) throws DSException {
        //Context Level: NONE
        try {
            List<Map<String,Object>> user = get("SELECT * FROM ACCOUNTS WHERE EMAIL = ?",email);
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
                    int roleId = (int) user.get(0).get("ROLE_ID");
                    insert("SESSIONS",
                            new Column("ID",id),
                            new Column("SESSION_TOKEN",token),
                            new Column("CREATION_DATE",new Date())
                    );

                    //clean up old sessions
                    long timestamp = System.currentTimeMillis();
                    List<Map<String,Object>> sessions = get("SELECT * FROM SESSIONS WHERE ID = ? ORDER BY CREATION_DATE ASC",id);
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

                    AuthContext context = new AuthContext(id,token);
                    context.setRole(roleId);
                    return context;

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
                List<Map<String,Object>> user = get("SELECT (PASSWORD) FROM ACCOUNTS WHERE ID = ?",context.id);
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
    public List<VideoViolation> createReport(AuthContext context, Report report) throws DSException {
        //Context Level: 4

        //validate context
        int role = isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },4);
        if(role != 4) throw new DSAuthException("Invalid Context");

        //insert report object
        int id = 0;

        Vehicle vehicle = report.getVehicle();

        report.setReportNum(null);
        report.setIncidentDate(new Date());//set the date to now
        report.setVolunteer(new Volunteer(context.id,null,null,null));

        boolean vehicleExists = false;
        try {
            String query = "SELECT licensePlate FROM "+vehicle.db_table()+" WHERE licensePlate = ?";
            vehicleExists = get(query,vehicle.getLicensePlate()).size() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(!vehicleExists){
            try {
                insert(vehicle);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            List<VehicleOwner> owners = MOTSService.getOwners(vehicle);
            for(VehicleOwner owner : owners){
                //insert owner
                try{
                    insert(owner);
                }catch (SQLException e){
                    e.printStackTrace();
                }

                try {
                    //create many-to-many connection
                    insert("TblOwnerVehicles",
                            new Column("owner",owner.getId()),
                            new Column("vehicle",vehicle.getLicensePlate()));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }


        try {
            id = insert(report);
            report.setReportNum(id);

            List<Violation> violations = report.getViolations();
            List<VideoViolation> videoViolations = new ArrayList<>();

            for (Violation v : violations){
                String violationId = ObjectId.generate();
                v.setAlphaNum(violationId);
                v.setReport(report);

                String url = v.getEvidenceLink();
                boolean isValid;
                if(v.getClassType() == 0)
                    isValid = validateFileUrl(url,context.id,true,"png","jpg");
                else
                    isValid = validateFileUrl(url,context.id,false,"mp4");

                if(!isValid){
                    System.err.println("Invalid URL! "+url);
                    continue;
                }

                insert(v.getClassType() == 0 ? "TblImageViolations" : "TblVideoViolations",v);

                if(v instanceof VideoViolation)
                    videoViolations.add((VideoViolation)v);
            }

            return videoViolations;

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
    }

    @Override
    public boolean createTeam(AuthContext context, Team team) throws DSException {
        //Context Level: 1
        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },1);
        try {

            //get leader
            Officer officer = team.getLeader();

            //insert team and get key.
            int key = insert(team);

            //assign officer to team.
            update(officer.db_table(),
                    new Where("badgeNum = ?",officer.getBadgeNum()),
                    new Column("team",key));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean assignLeaderToTeam(AuthContext context, Officer officer, Team team) throws DSException {
        //Context Level: 1
        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },1);
        try {
            update("TblTeams",
                    new Where("teamNum = ?",team.getTeamNum()),
                    new Column("leader",officer.getBadgeNum()));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean assignOfficerToTeam(AuthContext context, Officer officer, Team team) throws DSException {
        //Context Level: 1
        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },1);

        try {
            update("TblOfficers",
                    new Where("badgeNum = ?",officer.getBadgeNum()),
                    new Column("team",team.getTeamNum()));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createVolunteer(AuthContext context, Volunteer volunteer) throws DSException {
        //Context Level: 3
        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },3);

        try{
            //create account
            String id = ObjectId.generate();
            Account account = new Account(id,volunteer.getEMAIL(),4,volunteer.getPassword());
            insert("Accounts",account);

            //create volunteer
            volunteer.setID(id);
            insert("TblVolunteers",volunteer);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void updateEvidenceUrl(Violation violation,String url){ ;
        try {
            update(violation.db_table(),new Where("alphaNum = ?",violation.getAlphaNum()),new Column("evidenceLink",url));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DSFormatException(e.getMessage());
        }
    }

    @Override
    public List<Report> getReports(AuthContext context) throws DSException {
        //Context Level: 1,2,4

        //if role is not one of the following (0,1,2,4) then throw an exception.
        final int role = isContextValidFor(context,(roleId -> {
            if(roleId == -1)
                throw new DSAuthException("Invalid Context");
        }),1,2,4);

        try {
            //limit for pagination
            String limit = "";//" LIMIT " + count + " OFFSET " + ((page - 1) * count);

            String select = "SELECT * FROM TblReports";

            String where = null;

            String orderBy = " ORDER BY incidentDate DESC";

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
                    where = " WHERE volunteer = \""+context.id+"\"";
                    break;
            }
            String query = select + where + limit + orderBy;

            List<Map<String,Object>> data = get(query);
            List<Report> reports = new ArrayList<>();

            for(Map<String,Object> res : data){
                Report report = new Report(res);

                int reportNum = report.getReportNum();
                String volId = report.getForeignKey("volunteer");
                String vehicleId = report.getForeignKey("vehicle");
                Integer teamNum = report.getForeignKey("team");

                //get violations
                List<Violation> violations = new ArrayList<>();
                List<Map<String,Object>> videpViolations = get("SELECT * FROM TblVideoViolations WHERE report = ?",reportNum);
                List<Map<String,Object>> imageViolations = get("SELECT * FROM TblImageViolations WHERE report = ?",reportNum);

                for(Map<String,Object> iv : imageViolations){
                    ImageViolation imageViolation = new ImageViolation(iv);
                    String id = imageViolation.getAlphaNum();

                    List<Decision> decisionList = new ArrayList<>();
                    List<Map<String,Object>> decisions = get("SELECT * FROM TblImageViolationDecisions WHERE violation = ?",id);
                    for (Map<String,Object> map : decisions)
                        decisionList.add(new Decision(map));

                    imageViolation.setDecisions(decisionList);
                    violations.add(imageViolation);
                }

                for(Map<String,Object> vv : videpViolations){
                    VideoViolation videoViolation = new VideoViolation(vv);
                    String id = videoViolation.getAlphaNum();

                    List<Decision> decisionList = new ArrayList<>();
                    List<Map<String,Object>> decisions = get("SELECT * FROM TblVideoViolationDecisions WHERE violation = ?",id);
                    for (Map<String,Object> map : decisions)
                        decisionList.add(new Decision(map));

                    videoViolation.setDecisions(decisionList);
                    violations.add(videoViolation);
                }

                List<Map<String,Object>> violationTypes = get("SELECT * FROM TblViolationTypes");
                Map<Integer,ViolationType> violationTypeMap = new HashMap<>();
                for(Map<String,Object> map : violationTypes){
                    ViolationType type = new ViolationType(map);
                    violationTypeMap.put(type.getTypeNum(),type);
                }
                for(Violation violation : violations){
                    int key = violation.getForeignKey("type");
                    violation.setType(violationTypeMap.get(key));
                }

                report.setViolations(violations);

                //get volunteer
                if(volId != null) {
                    Volunteer volunteer = new Volunteer(get("SELECT * FROM TblVolunteers INNER JOIN Accounts ON TblVolunteers.ID = Accounts.ID WHERE id = ?", volId).get(0));
                    report.setVolunteer(volunteer);
                }

                //get vehicle
                if(vehicleId != null){
                    Vehicle vehicle = new Vehicle(get("SELECT * FROM TblVehicles WHERE licensePlate = ?",vehicleId).get(0));
                    int modelNum = vehicle.getForeignKey("model");

                    VehicleModel model = new VehicleModel(get("SELECT * FROM TblVehicleModel WHERE modelNum = ?",modelNum).get(0));
                    vehicle.setModel(model);
                    if(role != 4) {
                        List<Map<String, Object>> ownerIds = get("SELECT * FROM TblOwnerVehicles WHERE vehicle = ?", vehicleId);
                        List<VehicleOwner> owners = new ArrayList<>();
                        for (Map<String, Object> map : ownerIds) {
                            String ownerId = (String) map.get("owner");
                            VehicleOwner owner = new VehicleOwner(get("SELECT * FROM TblVehicleOwners WHERE id = ?", ownerId).get(0));
                            owners.add(owner);
                        }
                        vehicle.setOwners(owners);
                    }
                    report.setVehicle(vehicle);
                }

                //get team
                if(teamNum != null){
                    report.setTeam(new Team(get("SELECT * FROM TblTeams WHERE teamNum = ?",teamNum).get(0)));
                }

                reports.add(report);
            }

            return reports;
        }catch (SQLException e){
            e.printStackTrace();
            throw new DSFormatException(e.getMessage());
        }
    }

    @Override
    public int registerResource(AuthContext context, Resource resource) {
        //context should be valid here. no need to check.
        try {
            int key = insert("Resources",
                    new Column("url",resource.getUrl()),
                    new Column("type",resource.getType()),
                    new Column("owner",context.id));

            return key;
        } catch (SQLException e) {
            throw new DSFormatException("Something went wrong: "+e.getMessage());
        }
    }

    @Override
    public void getReportExport(AuthContext context, Date from, Date to,OutputStream os) throws DSException {

        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },1);

        java.sql.Date sqlFromDate = new java.sql.Date(from.getTime());
        java.sql.Date sqlToDate = new java.sql.Date(to.getTime());
        String jasperFilePath = new File(JASPER_BIN).getPath();


        Map<String,Object> params = new HashMap<>();
        params.put("fromDate",sqlFromDate);
        params.put("toDate",sqlToDate);

        try {
            JasperPrint print = JasperFillManager.fillReport(jasperFilePath, params , connection);
            byte[] arr = serialize(print);
            os.write(arr);
        } catch (JRException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createViolationType(AuthContext context, ViolationType violationType) throws DSException {
        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },1);
        try {
            violationType.setTypeNum(null);
            insert("TblViolationTypes",violationType);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DSFormatException(e.getMessage());
        }
    }

    @Override
    public List<VehicleModel> getVehicleModels(AuthContext context) throws DSException {
        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },1,2,4);
        List<VehicleModel> vehicleModels = new ArrayList<>();
        try {
            List<Map<String,Object>> data = get("SELECT * FROM TblVehicleModel");
            for(Map<String,Object> map : data)
                vehicleModels.add(new VehicleModel(map));
            return vehicleModels;
        } catch (SQLException e) {
            throw new DSFormatException(e.getMessage());
        }
    }

    @Override
    public List<ViolationType> getViolationTypes(AuthContext context) throws DSException {
        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },1,2,4);
        List<ViolationType> violationTypes = new ArrayList<>();
        try {
            List<Map<String,Object>> data = get("SELECT * FROM TblViolationTypes");
            for(Map<String,Object> map : data)
                violationTypes.add(new ViolationType(map));
            return violationTypes;
        } catch (SQLException e) {
            throw new DSFormatException(e.getMessage());
        }
    }

    @Override
    public boolean makeDecision(AuthContext context, Decision decision) throws DSException {
        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },1,2);
        try{
            String tableName = decision.getViolation().db_table();
            String badgeNum = decision.getOfficer().getBadgeNum();
            String violation = decision.getViolation().getAlphaNum();

            List<Map<String,Object>> decisions = get("SELECT * FROM "+tableName+" WHERE violation = ?",violation);

            for(Map<String,Object> item : decisions){
                Decision deci = new Decision(item);
                String officerId = deci.getForeignKey("officer");

                //officer is trying to vote again.
                if(officerId.equals(decision.getOfficer().getBadgeNum()))
                    return false;
            }


            update(tableName,
                    new Where("officer = ? AND violation = ?",
                            badgeNum,
                            violation),
                    new Column("decision",decision.getDecision()));

            return true;
        }catch (SQLException e){
            return false;
        }
    }

    @Override
    public void createUser(AuthContext context, Account account) throws DSException {
        int role = isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },1);

        if(role == 1 && account.getROLE_ID() != 2)
            throw new DSAuthException("Head Officer can only add an officer.");
        try{
            //create account
            String id = ObjectId.generate();
            account.setID(id);

            //make copy
            Account toInsert = new Account(id,account.getEMAIL(),account.getROLE_ID(),account.getPassword());
            //insert copy
            insert(toInsert);

            switch (account.getROLE_ID()){
                case 1: //case officer
                case 2: //case high rank officer
                case 4: //case volunteer
                    insert(account); //insert auto-infers the table using db_table()
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DSFormatException(e.getMessage());
        }
    }

    @Override
    public List<Officer> getUnassignedOfficers(AuthContext context) throws DSException {
        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },1);
        try {
            List<Map<String,Object>> rs = get("SELECT * FROM TblOfficers WHERE team IS NULL");
            List<Officer> officers = new ArrayList<>();

            for(Map<String,Object> m : rs)
                officers.add(new Officer(m));

            return officers;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DSFormatException(e.getMessage());
        }
    }

    @Override
    public List<Report> getUnassignedReports(AuthContext context) throws DSException {
        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },1);
        try {
            List<Map<String,Object>> d = get("SELECT * FROM TblReports WHERE team IS NULL");
            List<Report> reports = new ArrayList<>();

            for(Map<String,Object> m : d){ reports.add(new Report(m)); }

            return reports;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DSFormatException(e.getMessage());
        }
    }

    @Override
    public List<Team> getAllTeams(AuthContext context) throws DSException {
        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },1);
        try {
            List<Map<String,Object>> rs = get("SELECT * FROM TblTeams");
            List<Team> teams = new ArrayList<>();
            for(Map<String,Object> m : rs){
                Team team = new Team(m);

                String leaderBadgeNum = team.getForeignKey("leader");
                if(leaderBadgeNum != null){
                    Officer officer = new Officer(get("SELECT * FROM TblOfficers WHERE badgeNum = ?",leaderBadgeNum).get(0));
                    team.setLeader(officer);
                }

                List<Officer> teamMembers = new ArrayList<>();
                List<Map<String,Object>> rs1 = get("SELECT * FROM TblOfficers WHERE team = ?",team.getTeamNum());
                for(Map<String,Object> m1 : rs1){
                    teamMembers.add(new Officer(m1));
                }
                team.setOfficers(teamMembers);

                List<Report> assignedReports = new ArrayList<>();
                List<Map<String,Object>> rs2 = get("SELECT * FROM TblReports WHERE team = ?",team.getTeamNum());
                for(Map<String,Object> m1 : rs2){
                    assignedReports.add(new Report(m1));
                }
                team.setReports(assignedReports);

                teams.add(team);
            }
            return teams;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DSFormatException(e.getMessage());
        }
    }

    @Override
    public void addOfficerToTeam(AuthContext context, Officer officer, Team team) throws DSException {
        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },1);
        try {

            //check if officer is unassigned.
            Integer existingTeam = (Integer) get("SELECT team FROM TblOfficers WHERE badgeNum = ?",officer.getBadgeNum()).get(0).get("team");
            if(existingTeam != null)
                throw new DSFormatException("Officer already assigned to team "+existingTeam);

            //check if team is not full
            if(get("SELECT badgeNum FROM TblOfficers WHERE team = ?",team.getTeamNum()).size() >= 6)
                throw new DSFormatException("Team is already full");

            //update
            update(officer.db_table(),
                    new Where("badgeNum = ?",officer.getBadgeNum())
                    ,new Column("team",team.getTeamNum()));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DSFormatException(e.getMessage());
        }
    }

    @Override
    public void addReportToTeam(AuthContext context, Report report, Team team) throws DSException {
        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },1);
        try {
            update(report.db_table(),
                    new Where("reportNum = ?",report.getReportNum())
                    ,new Column("team",team.getTeamNum()));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DSFormatException(e.getMessage());
        }
    }

    @Override
    public void getUndecidedViolations(AuthContext context) throws DSException {

    }

    @Override
    public List<Account> getAccounts(AuthContext context) throws DSException {
        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); });
        try{
            List<Account> accounts = new ArrayList<>();
            get("SELECT ID,EMAIL,ROLE_ID FROM Accounts").forEach(
                    stringObjectMap -> accounts.add(new Account(stringObjectMap))
            );
            return accounts;
        }catch (SQLException e){
            throw new DSFormatException(e.getMessage());
        }
    }

    @Override
    public List<Officer> getAllOfficers(AuthContext context) throws DSException {
        isContextValidFor(context,roleId -> { if(roleId == -1) throw new DSAuthException("Invalid Context"); },1);
        try {
            List<Map<String,Object>> rs = get("SELECT * FROM TblOfficers");
            List<Officer> officers = new ArrayList<>();

            for(Map<String,Object> m : rs)
                officers.add(new Officer(m));

            return officers;
        } catch (SQLException e) {
            e.printStackTrace();
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
        if(context.isValid())
            return context.getRole();

        try {
            List<Map<String,Object>> data = get("SELECT Accounts.ROLE_ID " +
                    "FROM Accounts INNER JOIN Sessions ON Accounts.ID = Sessions.ID " +
                    "WHERE (((Sessions.ID)= ? ) AND ((Sessions.SESSION_TOKEN)= ? ))", context.id, context.sessionToken);
            int role =  data.size() == 0 ? -1 : (Integer) data.get(0).get("ROLE_ID");
            context.setRole(role);
            return role;
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
    protected<T> T insert(String table, Column...values) throws SQLException {

        StringBuilder builder1 = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();

        for(Column value : values){
            if(!value.shouldIgnore()) {
                builder1.append(value.getKey()).append(",");
                builder2.append("?").append(",");
            }
        }

        builder1.deleteCharAt(builder1.length() - 1);
        builder2.deleteCharAt(builder2.length() - 1);

        String query  ="INSERT INTO "+table+" ("+builder1.toString()+") VALUES ("+builder2.toString()+");";

        PreparedStatement statement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);

        int index = 1;
        for(Column obj : values)
            if(!obj.shouldIgnore())
                statement.setObject(index++,obj.getValue());

        statement.executeUpdate();

        ResultSet rs = statement.getGeneratedKeys();

        return rs.next() ? (T) rs.getObject(1) : null;
    }

    /**
     *
     * @param table
     * @param dbObject
     * @param <T>
     * @return
     * @throws SQLException
     */
    protected <T> T insert(String table,DBObject dbObject) throws SQLException {
        return insert(table,dbObject.db_columns());
    }

    /**
     *
     * @param object
     * @param <T>
     * @return
     * @throws SQLException
     */
    protected <T> T insert(DBObject object) throws SQLException{
        return insert(object.db_table(),object.db_columns());
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
    protected List<Map<String,Object>> get(String query,Object...args) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);

        int index = 1;
        for(Object val : args)
            statement.setObject(index++,val);

         ResultSet set = statement.executeQuery();

        String[] columns = new String[set.getMetaData().getColumnCount()];

        for (int i = 1; i <= columns.length; i++ )
            columns[i - 1] = set.getMetaData().getColumnName(i);

        List<Map<String,Object>> data = new ArrayList<>();

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
    private boolean update(String table,Where where, Column...values) throws SQLException {

        StringBuilder builder = new StringBuilder();

        for(Column value : values)
            builder.append(value.getKey()).append(" = ").append("?,");

        builder.deleteCharAt(builder.length()-1);

        String query  ="UPDATE "+table+" SET "+builder.toString()+" WHERE "+where.syntax;
        PreparedStatement statement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);

        int index = 1;
        for(Column obj : values)
            if(!obj.shouldIgnore())
                statement.setObject(index++,obj.getValue());

        for(Object o : where.values)
            statement.setObject(index++,o);

        return statement.executeUpdate() != 0;
    }

    private boolean validateFileUrl(String url,String ownerId,boolean canBeNullOrEmpty,String... types){
        if(canBeNullOrEmpty && (url == null || url.isEmpty()))
            return true;
        else if (url == null || url.isEmpty())
            return false;

        // "/resources/{owner_id}/{file_name}.{type}"
        String[] contents = url.split("/");

        if(contents.length != 3)
            return false;

        String owner = contents[1];

        if(!owner.equals(ownerId))
            return false;

        String file = contents[2];
        String type = file.split("\\.")[1];

        for(String s : types){
            if(s.equalsIgnoreCase(type))
                    return true;
        }
        return false;
    }

    @FunctionalInterface
    interface ContextValidator{
        void validWithRoleId(int roleId) throws DSException;
    }

    /**
     * Class used to create Where Predicates
     */
    class Where {
        final String syntax;
        final Object[] values;

        public Where(String syntax, Object...values) {
            this.syntax = syntax;
            this.values = values;
        }
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }
    public static <T> T deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (T)is.readObject();
    }

}
