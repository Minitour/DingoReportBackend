package mobi.newsound.model;

import com.google.gson.annotations.Expose;
import mobi.newsound.database.Column;
import mobi.newsound.database.Mappable;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by Antonio Zaitoun on 20/12/2017.
 */
public abstract class Violation implements Mappable{

    @Expose
    private String alphaNum;

    @Expose
    private String evidenceLink;

    @Expose
    private ViolationType type;

    @Expose
    private Collection<Decision> decisions;

    public Violation(String alphaNum, String evidenceLink, ViolationType type) {
        this.alphaNum = alphaNum;
        this.evidenceLink = evidenceLink;
        this.type = type;
    }

    public String getAlphaNum() {
        return alphaNum;
    }

    public void setAlphaNum(String alphaNum) {
        this.alphaNum = alphaNum;
    }

    public String getEvidenceLink() {
        return evidenceLink;
    }

    public void setEvidenceLink(String evidenceLink) {
        this.evidenceLink = evidenceLink;
    }

    public ViolationType getType() {
        return type;
    }

    public void setType(ViolationType type) {
        this.type = type;
    }

    public Collection<Decision> getDecisions() {
        return decisions;
    }

    public void setDecisions(Collection<Decision> decisions) {
        this.decisions = decisions;
    }

    @Override
    public Column[] db_columns() {
        return new Column[]{
                new Column("alphaNum",alphaNum),
                new Column("evidenceLink",evidenceLink),
                new Column<>("type", Optional.of(type),ViolationType::getTypeNum)
        };
    }


}
