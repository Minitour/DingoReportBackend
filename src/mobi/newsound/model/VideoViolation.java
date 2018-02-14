package mobi.newsound.model;

import com.google.gson.annotations.Expose;
import mobi.newsound.database.Column;

import java.util.Map;
import java.util.Optional;

public class VideoViolation extends Violation {

    @Expose
    private int from;

    @Expose
    private int to;

    @Expose
    private String description;

    public VideoViolation(String alphaNum, String evidenceLink, ViolationType type, int from, int to, String description) {
        super(alphaNum, evidenceLink, type);
        this.from = from;
        this.to = to;
        this.description = description;
        setClassType(1);
    }

    public VideoViolation(Map<String, Object> map) {
        super(map);
    }

    @Override
    public Column[] db_columns() {
        return new Column[]{
                new Column("alphaNum",getAlphaNum()),
                new Column("evidenceLink",getEvidenceLink()),
                new Column<>("report",Optional.ofNullable(getReport()),Report::getReportNum),
                new Column<>("type", Optional.of(getType()),ViolationType::getTypeNum),
                new Column("from",from),
                new Column("to",to),
                new Column("description",description)
        };
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
