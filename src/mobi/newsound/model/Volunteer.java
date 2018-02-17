package mobi.newsound.model;

import com.google.gson.annotations.Expose;
import mobi.newsound.database.Column;

import java.util.Map;

public class Volunteer extends Account{

    @Expose
    private String name;

    @Expose
    private String phone;

    public Volunteer(String ID, String EMAIL, String name, String phone) {
        super(ID, EMAIL, 4);
        this.name = name;
        this.phone = phone;
    }

    public Volunteer(String EMAIL, int ROLE_ID, String password, String name, String phone) {
        super(EMAIL, ROLE_ID, password);
        this.name = name;
        this.phone = phone;
    }

    public Volunteer(Map<String, Object> map) {
        super(map);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public Column[] db_columns() {
        return new Column[]{
                new Column("ID",getID()),
                new Column("name",name),
                new Column("phone",phone)
        };
    }

    @Override
    public String db_table() {
        return "TblVolunteers";
    }
}
