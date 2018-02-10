package mobi.newsound.model;

import com.google.gson.annotations.Expose;

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
}
