package mobi.newsound.model;

import com.google.gson.annotations.Expose;

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
}
