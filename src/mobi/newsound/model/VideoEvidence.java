package mobi.newsound.model;

public class VideoEvidence extends Evidence {

    private String videoUrl;
    private int fromStamp;
    private int toStamp;

    public VideoEvidence(long id, String description, String resoureUrl, String videoUrl, int toStamp, int fromStamp) {
        super(id, description, resoureUrl);
        setVideoUrl(videoUrl);
        setToStamp(toStamp);
        setFromStamp(fromStamp);
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getFromStamp() {
        return fromStamp;
    }

    public void setFromStamp(int fromStamp) {
        this.fromStamp = fromStamp;
    }

    public int getToStamp() {
        return toStamp;
    }

    public void setToStamp(int toStamp) {
        this.toStamp = toStamp;
    }
}
