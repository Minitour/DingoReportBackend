package mobi.newsound.model;

import com.google.gson.annotations.Expose;
import mobi.newsound.database.DBObject;

import java.util.Map;

/**
 * Created By Tony on 12/02/2018
 */
public class VehicleModel extends DBObject {

    @Expose
    private int modelNum;

    @Expose
    private String name;

    public VehicleModel(int modelNum, String name) {
        this.modelNum = modelNum;
        this.name = name;
    }

    public VehicleModel(Map<String, Object> map) {
        super(map);
    }

    public int getModelNum() {
        return modelNum;
    }

    public void setModelNum(int modelNum) {
        this.modelNum = modelNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String db_table() {
        return "TblVehicleModel";
    }
}
