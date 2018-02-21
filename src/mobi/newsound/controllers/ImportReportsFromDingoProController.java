package mobi.newsound.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataAccess;
import mobi.newsound.model.Report;
import mobi.newsound.model.Vehicle;
import mobi.newsound.model.VehicleOwner;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.RESTRoute;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImportReportsFromDingoProController implements RESTRoute {

    private static final Gson gson = new Gson();

    @Override
    public Object handle(Request request, Response response, JsonObject body) throws Exception {

        AuthContext context = extractFromBody(body);

        JsonArray array = body.get("data").getAsJsonArray();

        List<Report> reports = new ArrayList<>();

        for (JsonElement element : array) {
            //getting the violation date
            JsonObject o = element.getAsJsonObject();
            Date violationDate = new Date(o.get("violationDate").getAsString());

            //getting the description
            String desc = o.get("description").getAsString();

            Vehicle v = gson.fromJson(o.get("vehicle"), Vehicle.class);

            //getting the car owner
            JsonObject carOwner = o.get("defendant").getAsJsonObject();

            Integer id = carOwner.get("ID").getAsInt();

            String drivingLicense = carOwner.get("drivingLicense").getAsString();

            String name = carOwner.get("name").getAsString();

            String address = carOwner.get("address").getAsString();

            VehicleOwner vo = new VehicleOwner(id.toString() ,drivingLicense, name, address);

            List<VehicleOwner> list = new ArrayList<>();
            list.add(vo);

            v.setOwners(list);
            //Integer reportNum, String description, Date incidentDate, Volunteer volunteer, Vehicle vehicle
            Report report = new Report(null, desc, violationDate, null, v);

            reports.add(report);

        }
        try (DataAccess db = DataAccess.getInstance()) {
            assert  db != null;

            db.importReports(context, reports);
            return JSONResponse.SUCCESS();

        }catch (Exception e) {
            return JSONResponse.FAILURE().message(e.getMessage());
        }

    }
}
