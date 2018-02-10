package mobi.newsound.model;

public class HighRankOfficer extends Officer {

    public HighRankOfficer(String ID, String EMAIL, int badgeNum, String name, String phoneExtension, int rank) {
        super(ID, EMAIL, badgeNum, name, phoneExtension, rank);
        setROLE_ID(1);
    }
}
