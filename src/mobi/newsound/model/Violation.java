package mobi.newsound.model;

import com.google.gson.annotations.Expose;

import java.util.Collection;

/**
 * Created by Antonio Zaitoun on 20/12/2017.
 */
public abstract class Violation {

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
}
