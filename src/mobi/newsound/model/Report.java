package mobi.newsound.model;

import com.google.gson.annotations.Expose;
import mobi.newsound.database.Mappable;

import java.util.*;

/**
 * Created by Antonio Zaitoun on 17/12/2017.
 */
public class Report implements Mappable {

    @Expose
    private String reportNum;

    @Expose
    private String description;

    @Expose
    private Date incidentDate;

    @Expose
    private Volunteer volunteer;

    @Expose
    private Vehicle vehicle;

    @Expose
    private Team team;

    @Expose
    private List<Violation> violations;

    public Report(String reportNum, String description, Date incidentDate, Volunteer volunteer, Vehicle vehicle) {
        this.reportNum = reportNum;
        this.description = description;
        this.incidentDate = incidentDate;
        this.volunteer = volunteer;
        this.vehicle = vehicle;
    }

    public String getReportNum() {
        return reportNum;
    }

    public void setReportNum(String reportNum) {
        this.reportNum = reportNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(Date incidentDate) {
        this.incidentDate = incidentDate;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public void setViolations(List<Violation> violations) {
        this.violations = violations;
    }
}
