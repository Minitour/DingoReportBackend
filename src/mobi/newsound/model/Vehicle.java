package mobi.newsound.model;

import com.google.gson.annotations.Expose;
import mobi.newsound.database.Column;
import mobi.newsound.database.DBObject;
import mobi.newsound.database.Mappable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class Vehicle extends DBObject{

    @Expose
    private String licensePlate;

    @Expose
    private VehicleModel model;

    @Expose
    private String colorHEX;

    @Expose
    private Collection<VehicleOwner> owners;

    public Vehicle(String licensePlate, VehicleModel model, String colorHEX) {
        setColorHEX(colorHEX);
        setLicensePlate(licensePlate);
        setModel(model);
    }

    public Vehicle(Map<String, Object> map) {
        super(map);
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public VehicleModel getModel() {
        return model;
    }

    public void setModel(VehicleModel model) {
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
                new Column<>("model", Optional.ofNullable(model),VehicleModel::getModelNum),
                new Column("colorHEX",colorHEX)
        };
    }
}
