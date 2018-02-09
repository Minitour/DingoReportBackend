package mobi.newsound.model;

public class VehicleOwner {

    private String id;
    private String licenseNumber;
    private String fullName;
    private String address;


    public VehicleOwner(String id, String licenseNumber, String fullName, String address) {
        setId(id);
        setLicenseNumber(licenseNumber);
        setFullName(fullName);
        setAddress(address);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }
}
