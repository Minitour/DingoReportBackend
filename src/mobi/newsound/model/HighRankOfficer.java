package mobi.newsound.model;

public class HighRankOfficer extends Officer {

    public HighRankOfficer(String ID, String EMAIL, String badgeNum, String name, String phoneExtension, int rank) {
        super(ID, EMAIL, badgeNum, name, phoneExtension, rank);
        setROLE_ID(1);
    }

    @Override
    public String db_table() {
        return super.db_table();
    }
}
