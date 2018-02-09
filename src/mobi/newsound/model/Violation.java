package mobi.newsound.model;

/**
 * Created by Antonio Zaitoun on 20/12/2017.
 */
public class Violation {
    private int id;
    private String description;
    private Decision decision;
    private ViolationType type;
    private int reportId;
    private int evidanceId;

    public int getEvidanceId() {
        return evidanceId;
    }

    public void setEvidanceId(int evidanceId) {
        this.evidanceId = evidanceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public ViolationType getType() {
        return type;
    }

    public void setType(ViolationType type) {
        this.type = type;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }
}
