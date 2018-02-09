package mobi.newsound.database;

import mobi.newsound.auth.AuthContext;
import mobi.newsound.model.Officer;
import mobi.newsound.model.Report;
import mobi.newsound.model.Unit;
import mobi.newsound.model.Volunteer;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Antonio Zaitoun on 09/02/2018.
 */
public interface DataStore extends AutoCloseable,Serializable{

    /**
     * Sign in with email and password
     * @param email
     * @param password_raw
     * @return Auth Context
     */
    AuthContext signIn(String email,String password_raw) throws DSException;

    /**
     *
     * @param context
     * @param currentPassword
     * @param newPassword
     * @return true if the password was updated.
     */
    boolean updatePassword(AuthContext context,String currentPassword,String newPassword) throws DSException;

    /**
     * Reset the password and return an auto-generated password
     * @param context
     * @return The generated password.
     */
    String resetPassword(AuthContext context);


    /**
     *
     * @param context
     * @param report
     * @return
     */
    boolean createReport(AuthContext context,Report report) throws DSException;


    /**
     *
     * @param context
     * @param unit
     * @return
     */
    boolean createUnit(AuthContext context,Unit unit) throws DSException;

    /**
     *
     * @param context
     * @param officer
     * @param unit
     * @return
     */
    boolean assignLeaderToUnit(AuthContext context, Officer officer,Unit unit) throws DSException;

    /**
     *
     * @param context
     * @param officer
     * @param unit
     * @return
     */
    boolean assignOfficerToUnit(AuthContext context,Officer officer, Unit unit) throws DSException;

    /**
     *
     * @param context
     * @param volunteer
     * @return
     */
    boolean createVolunteer(AuthContext context, Volunteer volunteer) throws DSException;

    /**
     *
     * @param context
     * @param count
     * @param page
     * @return
     */
    List<Report> getReports(AuthContext context,int count,int page) throws DSException;


    abstract class DSException extends RuntimeException {}

    class DSFormatException extends DSException {}

    class DSAuthException extends DSException{}
}
