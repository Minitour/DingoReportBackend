package mobi.newsound.model;

public class HighRankOfficer extends Officer {

    private int rank;

    public HighRankOfficer(int badgeId, String firstName, String lastName, String extension, int rank) {
        super(badgeId, firstName, lastName, extension);

    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return this.rank;
    }


}
