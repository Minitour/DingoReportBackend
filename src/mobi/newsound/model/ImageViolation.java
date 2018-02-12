package mobi.newsound.model;

import java.util.Map;

public class ImageViolation extends Violation {

    public ImageViolation(String alphaNum, String evidenceLink, ViolationType type) {
        super(alphaNum, evidenceLink, type);
    }

    public ImageViolation(Map<String, Object> map) {
        super(map);
    }
}
