package mobi.newsound.model;

public class Evidence {

    private long id;
    private String description;
    private String resoureUrl;
    private int timeStampFrom;
    private int timeStampTo;

    public Evidence(long id, String description, String resoureUrl) {
        setId(id);
        setDescription(description);
        setResoureUrl(resoureUrl);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResoureUrl() {
        return resoureUrl;
    }

    public void setResoureUrl(String resoureUrl) {
        this.resoureUrl = resoureUrl;
    }

    public void setTimeStampTo(int getTimeStampTo) {
        this.timeStampTo = getTimeStampTo;
    }

    public void setTimeStampFrom(int timeStampFrom) {
        this.timeStampFrom = timeStampFrom;
    }

    public int getTimeStampTo() {
        return timeStampTo;
    }

    public int getTimeStampFrom() {
        return timeStampFrom;
    }

    public boolean isVideo(){
        return timeStampFrom > 0 && timeStampTo > 0;
    }

    public static String displayTime(int timeStamp){
        int seconds = timeStamp % 60;
        int min = timeStamp / 60;
        return  (min <= 9 ? "0" : "")+min+":"+ (seconds <= 9 ? "0" : "") + seconds;
    }
}
