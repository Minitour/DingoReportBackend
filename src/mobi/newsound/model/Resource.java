package mobi.newsound.model;

import com.google.gson.annotations.Expose;
import mobi.newsound.database.DBObject;

import java.util.Map;

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

    @Expose
    private String path;

    public Resource(String url, String type) {
        this.url = url;
        this.type = type;
    }

    public Resource(Map<String, Object> map) {
        super(map);
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
