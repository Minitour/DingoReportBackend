package mobi.newsound.model;

public class ViolationType {
    private int id;
    private String name;
    private String description;
    private int points;
    private double fine;
    boolean inviteToCourt;


    public ViolationType (String name, String description, int points, double fine, boolean inviteToCourt) {
        setName(name);
        setDescription(description);
        setPoints(points);
        setFine(fine);
        this.inviteToCourt = false;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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
    public String toString() {
        return name;
    }
}
