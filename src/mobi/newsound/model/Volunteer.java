package mobi.newsound.model;

import com.google.gson.annotations.Expose;
import mobi.newsound.database.Column;

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
}
