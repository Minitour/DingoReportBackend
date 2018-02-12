package mobi.newsound.model;

import com.google.gson.annotations.Expose;
import mobi.newsound.database.Column;
import mobi.newsound.database.DBObject;

import java.util.Map;
import java.util.Optional;

/**
 * Created by Antonio Zaitoun on 20/12/2017.
 */
public class Decision extends DBObject{
    @Expose
    private Officer officer;

    @Expose
    private Violation violation;

    @Expose
    private int decision;

    public Decision(Map<String, Object> map) {
        super(map);
    }

    @Override
    public Column[] db_columns() {
        return new Column[]{
                new Column<>("officer", Optional.ofNullable(officer),Officer::getBadgeNum),
                new Column<>("violation",Optional.of(violation),Violation::getAlphaNum),
                new Column("decision",decision)
        };
    }

    public Officer getOfficer() {
        return officer;
    }

    public void setOfficer(Officer officer) {
        this.officer = officer;
    }

    public Violation getViolation() {
        return violation;
    }

    public void setViolation(Violation violation) {
        this.violation = violation;
    }

    public int getDecision() {
        return decision;
    }

    public void setDecision(int decision) {
        this.decision = decision;
    }
}
