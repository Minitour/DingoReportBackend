package mobi.newsound.model;

import com.google.gson.annotations.Expose;
import mobi.newsound.database.Column;
import mobi.newsound.database.DBObject;
import mobi.newsound.database.Mappable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Antonio Zaitoun on 23/12/2017.
 */
public class Team extends DBObject{

    @Expose
    private Integer teamNum;

    @Expose
    private Officer leader;

    @Expose
    private Collection<Officer> officers;

    @Expose
    private Collection<Report> reports;

    public Team(Integer teamNum) {
        this.teamNum = teamNum;
    }

    public Team(Map<String, Object> map) {
        super(map);
    }

    public Integer getTeamNum() {
        return teamNum;
    }

    public void setTeamNum(Integer teamNum) {
        this.teamNum = teamNum;
    }

    public Officer getLeader() {
        return leader;
    }

    public void setLeader(Officer leader) {
        this.leader = leader;
    }

    public Collection<Officer> getOfficers() {
        return officers;
    }

    public void setOfficers(Collection<Officer> officers) {
        this.officers = officers;
    }

    public Collection<Report> getReports() {
        return reports;
    }

    public void setReports(Collection<Report> reports) {
        this.reports = reports;
    }

    @Override
    public Column[] db_columns() {
        return new Column[]{
                new Column("teamNum",teamNum),
                new Column<>("leader", Optional.ofNullable(leader),Officer::getBadgeNum)
        };
    }
}
