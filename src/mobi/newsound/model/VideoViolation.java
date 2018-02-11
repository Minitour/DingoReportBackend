package mobi.newsound.model;

import com.google.gson.annotations.Expose;
import mobi.newsound.database.Column;

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
    }

    @Override
    public Column[] db_columns() {
        return new Column[]{
                new Column("alphaNum",getAlphaNum()),
                new Column("evidenceLink",getEvidenceLink()),
                new Column<>("type", Optional.of(getType()),ViolationType::getTypeNum),
                new Column("from",from),
                new Column("to",to),
                new Column("description",description)
        };
    }
}
