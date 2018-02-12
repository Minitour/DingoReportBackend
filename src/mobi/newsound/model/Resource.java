package mobi.newsound.model;

import com.google.gson.annotations.Expose;
import mobi.newsound.database.DBObject;

/**
 * Created By Tony on 10/02/2018
 */
public class Resource extends DBObject {

    @Expose
    private int id;

    @Expose
    private String url;

    @Expose
    private String type;

    @Expose
    private Account owner;

    public Resource(String url, String type) {
        this.url = url;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }
}
