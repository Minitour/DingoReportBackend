package mobi.newsound.model;

import com.google.gson.annotations.Expose;
import mobi.newsound.database.Column;
import mobi.newsound.database.DBObject;
import mobi.newsound.database.Mappable;

import java.util.Map;

public class ViolationType extends DBObject{

    @Expose
    private int typeNum;

    @Expose
    private String name;

    @Expose
    private String description;

    @Expose
    private int points;

    @Expose
    private double fine;

    @Expose
    boolean inviteToCourt;

    public ViolationType(int typeNum, String name, String description, int points, double fine, boolean inviteToCourt) {
        this.typeNum = typeNum;
        this.name = name;
        this.description = description;
        this.points = points;
        this.fine = fine;
        this.inviteToCourt = inviteToCourt;
    }

    public ViolationType(Map<String, Object> map) {
        super(map);
    }

    public int getTypeNum() {
        return typeNum;
    }

    public void setTypeNum(int typeNum) {
        this.typeNum = typeNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public boolean isInviteToCourt() {
        return inviteToCourt;
    }

    public void setInviteToCourt(boolean inviteToCourt) {
        this.inviteToCourt = inviteToCourt;
    }

    @Override
    public Column[] db_columns() {
        return new Column[]{
                new Column("typeNum",typeNum),
                new Column("name",name),
                new Column("description",description),
                new Column("points",points),
                new Column("fine",fine),
                new Column("inviteToCourt",inviteToCourt)
        };
    }
}
