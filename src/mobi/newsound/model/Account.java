package mobi.newsound.model;

import com.google.gson.annotations.Expose;

public class Account {

    @Expose
    private String ID;

    @Expose
    private String EMAIL;

    @Expose
    private int ROLE_ID;

    public Account(String ID, String EMAIL, int ROLE_ID) {
        this.ID = ID;
        this.EMAIL = EMAIL;
        this.ROLE_ID = ROLE_ID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public int getROLE_ID() {
        return ROLE_ID;
    }

    public void setROLE_ID(int ROLE_ID) {
        this.ROLE_ID = ROLE_ID;
    }
}
