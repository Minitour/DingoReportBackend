package mobi.newsound.model;

import com.google.gson.annotations.Expose;
import mobi.newsound.database.Column;
import mobi.newsound.database.Mappable;

public class Account implements Mappable{

    @Expose
    private String ID;

    @Expose
    private String EMAIL;

    @Expose
    private int ROLE_ID;

    @Expose(serialize = false,deserialize = false)
    private String password;

    public Account(String ID, String EMAIL, int ROLE_ID) {
        this.ID = ID;
        this.EMAIL = EMAIL;
        this.ROLE_ID = ROLE_ID;
    }

    public Account(String ID, String EMAIL, int ROLE_ID, String password) {
        this.ID = ID;
        this.EMAIL = EMAIL;
        this.ROLE_ID = ROLE_ID;
        this.password = password;
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


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Column[] db_columns() {
        return new Column[]{
                new Column("ID",ID),
                new Column("EMAIL",EMAIL),
                new Column("ROLE_ID",ROLE_ID),
                new Column("PASSWORD",password)
        };
    }
}
