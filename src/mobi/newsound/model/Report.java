package mobi.newsound.model;

import com.google.gson.annotations.Expose;
import mobi.newsound.database.Column;
import mobi.newsound.database.DBObject;
import mobi.newsound.database.Mappable;

import java.util.*;

/**
 * Created by Antonio Zaitoun on 17/12/2017.
 */
public class Report extends DBObject {

    @Expose
    private Integer reportNum;

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

    public Report(Integer reportNum, String description, Date incidentDate, Volunteer volunteer, Vehicle vehicle) {
        this.reportNum = reportNum;
        this.description = description;
        this.incidentDate = incidentDate;
        this.volunteer = volunteer;
        this.vehicle = vehicle;
    }

    public Report(Map<String, Object> map) {
        super(map);
    }

    public int getReportNum() {
        return reportNum;
    }

    public void setReportNum(Integer reportNum) {
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


    @Override
    public Column[] db_columns() {
        return new Column[]{
                new Column("reportNum",reportNum),
                new Column("description",description),
                new Column("incidentDate",incidentDate),
                new Column<>("volunteer", Optional.ofNullable(volunteer), Account::getID), //FK
                new Column<>("vehicle",Optional.ofNullable(vehicle), Vehicle::getLicensePlate), //FK
                new Column<>("team",Optional.ofNullable(team), Team::getTeamNum) //FK
        };
    }
}
