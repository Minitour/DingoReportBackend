package mobi.newsound.model;

public class Volunteer {

    private long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String password;


    public Volunteer(long id, String firstName, String lastName, String phone, String email, String password) {
        setId(id);
        setFirstName(firstName);
        setLastName(lastName);
        setPhone(phone);
        setEmail(email);
        setPassword(password);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean changePassword(String oldPass, String newPass) {
        //TODO
        if (newPass == null) return false;
        return true;
    }

    public Report[] getReports() {
        //TODO
        Report[] reports = new Report[1];
        return reports;
    }

    public void createReport(String licensePlate, String model, String color) {
        //TODO
    }

    public boolean addDescription(String description, Report report) {
        report.setDesc(description);
        return true;
    }



}
