package mobi.newsound.model;

import com.google.gson.annotations.Expose;
import mobi.newsound.database.Column;
import mobi.newsound.database.DBObject;
import mobi.newsound.database.Mappable;

import java.util.Collection;

public class Vehicle extends DBObject{

    @Expose
    private String licensePlate;

    @Expose
    private String model;

    @Expose
    private String colorHEX;

    @Expose
    private Collection<VehicleOwner> owners;

    public Vehicle(String licensePlate, String model, String colorHEX) {
        setColorHEX(colorHEX);
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

    public String getColorHEX() {
        return colorHEX;
    }

    public void setColorHEX(String colorHEX) {
        this.colorHEX = colorHEX;
    }

    public Collection<VehicleOwner> getOwners() {
        return owners;
    }

    public void setOwners(Collection<VehicleOwner> owners) {
        this.owners = owners;
    }

    @Override
    public Column[] db_columns() {
        return new Column[]{
                new Column("licensePlate",licensePlate),
                new Column("model",model),
                new Column("colorHEX",colorHEX)
        };
    }
}
