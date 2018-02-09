package mobi.newsound.model;

public class Officer {

    private int badgeId;
    private String firstName;
    private String lastName;
    private String extension;

    public Officer(int badgeId, String firstName, String lastName, String extension) {
        setBadgeId(badgeId);
        setFirstName(firstName);
        setLastName(lastName);
        setExtension(extension);
    }


    public int getBadgeId() {
        return badgeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getExtension() {
        return extension;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
