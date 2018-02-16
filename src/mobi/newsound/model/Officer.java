package mobi.newsound.model;

import com.google.gson.annotations.Expose;
import mobi.newsound.database.Column;

import java.util.Map;
import java.util.Optional;

public class Officer extends Account {

    @Expose
    private String badgeNum;

    @Expose
    private String name;

    @Expose
    private String phoneExtension;

    @Expose
    private int rank;

    @Expose
    private Team team;

    public Officer(String ID, String EMAIL, String badgeNum, String name, String phoneExtension, int rank) {
        super(ID, EMAIL, 2);
        this.badgeNum = badgeNum;
        this.name = name;
        this.phoneExtension = phoneExtension;
        this.rank = rank;
    }

    public Officer(Map<String, Object> map) {
        super(map);
    }

    public String getBadgeNum() {
        return badgeNum;
    }

    public void setBadgeNum(String badgeNum) {
        this.badgeNum = badgeNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneExtension() {
        return phoneExtension;
    }

    public void setPhoneExtension(String phoneExtension) {
        this.phoneExtension = phoneExtension;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public Column[] db_columns() {
        return new Column[]{
                new Column("badgeNum",badgeNum),
                new Column("account_id",getID()),
                new Column("name",name),
                new Column("phoneExtension",phoneExtension),
                new Column("rank",rank),
                new Column<>("team", Optional.ofNullable(team),Team::getTeamNum)
        };
    }

    @Override
    public String db_table() {
        return "TblOfficers";
    }
}
