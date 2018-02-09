package mobi.newsound.database;

import mobi.newsound.auth.AuthContext;
import mobi.newsound.model.Officer;
import mobi.newsound.model.Report;
import mobi.newsound.model.Unit;
import mobi.newsound.model.Volunteer;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Antonio Zaitoun on 09/02/2018.
 */
public interface DataStore extends AutoCloseable,Serializable{

    static DataStore getInstance(){
        try {
            return new Database();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sign in with email and password
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
    default boolean createReport(AuthContext context,Report report) throws DSException {throw new DSUnimplementedException();}


    /**
     *
     * @param context
     * @param unit
     * @return
     */
    default boolean createUnit(AuthContext context,Unit unit) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @param officer
     * @param unit
     * @return
     */
    default boolean assignLeaderToUnit(AuthContext context, Officer officer,Unit unit) throws DSException {throw new DSUnimplementedException();}

    /**
     *
     * @param context
     * @param officer
     * @param unit
     * @return
     */
    default boolean assignOfficerToUnit(AuthContext context,Officer officer, Unit unit) throws DSException {throw new DSUnimplementedException();}

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
     * @param count
     * @param page
     * @return
     */
    default List<Report> getReports(AuthContext context,int count,int page) throws DSException {throw new DSUnimplementedException();}


    abstract class DSException extends RuntimeException {}

    class DSUnimplementedException extends DSException{}

    class DSFormatException extends DSException {}

    class DSAuthException extends DSException{}
}
