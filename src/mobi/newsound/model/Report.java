package mobi.newsound.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Antonio Zaitoun on 17/12/2017.
 */
public class Report {

    private String id;
    private String desc;
    private Date date;
    private boolean decision;
    private String plateNumber;
    private String volunteerId;
    private List<Violation> violations;

    public Report(String id, String desc, Date date, String plateNumber, String volunteerId) {
        setId(id);
        setDesc(desc);
        this.date = date;
        this.plateNumber = plateNumber;
        this.volunteerId = volunteerId;
        this.violations = new ArrayList<>();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setDecision(boolean decision) {
        this.decision = decision;
    }

    public boolean addViolation(ViolationType violation) {
        return true;
    }

    public String getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public Date getDate() {
        return date;
    }

    public boolean isDecision() {
        return decision;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getVolunteerId() {
        return volunteerId;
    }

    public List<Violation> getViolations() {
        return Collections.unmodifiableList(violations);
    }
}
