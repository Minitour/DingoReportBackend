package mobi.newsound.model;

public class Vehicle {

    private String licensePlate;
    private String model;
    private String color;


    public Vehicle(String licensePlate, String model, String color) {
        setColor(color);
        setLicensePlate(licensePlate);
        setModel(model);
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }



}
