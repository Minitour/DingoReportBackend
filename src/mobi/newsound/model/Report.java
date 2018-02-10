package mobi.newsound.model;

import mobi.newsound.database.Mappable;

import java.util.*;

/**
 * Created by Antonio Zaitoun on 17/12/2017.
 */
public class Report implements Mappable {

    private String reportNum;
    private String description;
    private Date incidentDate;
    private boolean decision;
    private String plateNumber;
    private String volunteer;
    private List<Violation> violations;

    public Report(String reportNum, String description, Date incidentDate, String plateNumber, String volunteer) {
        setReportNum(reportNum);
        setDescription(description);
        this.incidentDate = incidentDate;
        this.plateNumber = plateNumber;
        this.volunteer = volunteer;
        this.violations = new ArrayList<>();
    }

    public void setReportNum(String reportNum) {
        this.reportNum = reportNum;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDecision(boolean decision) {
        this.decision = decision;
    }

    public boolean addViolation(ViolationType violation) {
        return true;
    }

    public String getReportNum() {
        return reportNum;
    }

    public String getDescription() {
        return description;
    }

    public Date getIncidentDate() {
        return incidentDate;
    }

    public boolean isDecision() {
        return decision;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getVolunteer() {
        return volunteer;
    }

    public List<Violation> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    @Override
    public void map(Map map) {
        map.get("reportNum");
    }
}
