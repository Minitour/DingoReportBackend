package mobi.newsound.model;

import com.google.gson.annotations.Expose;

import java.util.Collection;

public class VehicleOwner {

    @Expose
    private String id;

    @Expose
    private String drivingLicense;

    @Expose
    private String name;

    @Expose
    private String address;

    @Expose
    private Collection<Vehicle> vehicles;

    public VehicleOwner(String id, String drivingLicense, String name, String address) {
        setId(id);
        setDrivingLicense(drivingLicense);
        setName(name);
        setAddress(address);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDrivingLicense(String drivingLicense) {
        this.drivingLicense = drivingLicense;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getDrivingLicense() {
        return drivingLicense;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Collection<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(Collection<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }
}
