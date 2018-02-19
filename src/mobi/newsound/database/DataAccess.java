package mobi.newsound.database;

import mobi.newsound.model.*;

import java.io.OutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by Antonio Zaitoun on 09/02/2018.
 */

//TODO: document interface methods
public interface DataAccess extends AutoCloseable,Serializable{

    static DataAccess getInstance(){
        try {
            return new Database();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    default boolean isValid(AuthContext context) throws DSException { throw new DSUnimplementedException();}

    /**
     * Sign in with email and password
     *
     * @param email
     * @param password_raw
     * @return Auth Context
     */
    default AuthContext signIn(String email,String password_raw) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @param currentPassword
     * @param newPassword
     * @return true if the password was updated.
     */
    default boolean updatePassword(AuthContext context,String currentPassword,String newPassword) throws DSException {throw new DSUnimplementedException();}

    /**
     * Reset the password and return an auto-generated password
     * @param context
     * @return The generated password.
     */
    default String resetPassword(AuthContext context) throws DSException {throw new DSUnimplementedException();}


    /**
     *
     * @param context
     * @param report
     * @return
     */
    default List<VideoViolation> createReport(AuthContext context,Report report) throws DSException {throw new DSUnimplementedException();}


    /**
     *
     * @param context
     * @param team
     * @return
     */
    default boolean createTeam(AuthContext context, Team team) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @param officer
     * @param team
     * @return
     */
    default boolean assignLeaderToTeam(AuthContext context, Officer officer, Team team) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @param officer
     * @param team
     * @return
     */
    default boolean assignOfficerToTeam(AuthContext context, Officer officer, Team team) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @param volunteer
     * @return
     */
    default boolean createVolunteer(AuthContext context, Volunteer volunteer) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @return
     */
    default List<Report> getReports(AuthContext context) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @param from
     * @param to
     * @return
     * @throws DSException
     */
    default void getReportExport(AuthContext context, Date from,Date to,OutputStream os) throws DSException {throw  new DSUnimplementedException();}

    /**
     *
     * @param context
     * @param violationType
     * @throws DSException
     */
    default void createViolationType(AuthContext context,ViolationType violationType) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @param decision
     * @return
     * @throws DSException
     */
    default boolean makeDecision(AuthContext context,Decision decision) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @param resource
     * @return
     */
    default int registerResource(AuthContext context, Resource resource) throws DSException { throw new DSUnimplementedException();}

    /**
     *
     * @param violation
     * @param url
     */
    default void updateEvidenceUrl(Violation violation,String url) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @return
     * @throws DSException
     */
    default List<ViolationType> getViolationTypes(AuthContext context) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @return
     * @throws DSException
     */
    default List<VehicleModel> getVehicleModels(AuthContext context) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @return
     * @throws DSException
     */
    default List<Officer> getUnassignedOfficers(AuthContext context) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @return
     * @throws DSException
     */
    default List<Report> getUnassignedReports(AuthContext context) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @return
     * @throws DSException
     */
    default List<Team> getAllTeams(AuthContext context) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @param officer
     * @param team
     * @throws DSException
     */
    default void addOfficerToTeam(AuthContext context,Officer officer,Team team) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @param report
     * @param team
     * @throws DSException
     */
    default void addReportToTeam(AuthContext context,Report report,Team team) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @throws DSException
     */
    default void getUndecidedViolations(AuthContext context) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @param account
     * @throws DSException
     */
    default void createUser(AuthContext context,Account account) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @return
     * @throws DSException
     */
    default List<Officer> getAllOfficers(AuthContext context) throws DSException {throw  new DSUnimplementedException();}

    /**
     * Get all accounts in the system.
     * @param context
     * @return
     * @throws DSException
     */
    default List<Account> getAccounts(AuthContext context) throws DSException {throw new DSUnimplementedException();}


    abstract class DSException extends RuntimeException {
        DSException(){}
        DSException(String message){
            super(message);
        }
    }

    class DSUnimplementedException extends DSException{
        public DSUnimplementedException() {
            super("Unimplemented");
        }

        DSUnimplementedException(String message) {
            super(message);
        }
    }

    class DSFormatException extends DSException {
        public DSFormatException() {
        }

        DSFormatException(String message) {
            super(message);
        }
    }

    class DSAuthException extends DSException{
        public DSAuthException() {
        }

        DSAuthException(String message) {
            super(message);
        }
    }
}
