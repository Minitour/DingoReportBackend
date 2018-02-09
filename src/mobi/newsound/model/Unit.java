package mobi.newsound.model;

/**
 * Created by Antonio Zaitoun on 23/12/2017.
 */
public class Unit {
    private String unitName;
    private String id;

    public Unit(String unitName, String id) {
        this.unitName = unitName;
        this.id = id;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
