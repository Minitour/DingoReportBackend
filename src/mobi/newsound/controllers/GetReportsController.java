package mobi.newsound.controllers;

import com.google.gson.JsonObject;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataStore;
import mobi.newsound.model.Report;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.RESTRoute;
import spark.Request;
import spark.Response;

import java.util.List;

/**
 * Created by Antonio Zaitoun on 13/02/2018.
 */
public class GetReportsController implements RESTRoute{
    @Override
    public Object handle(Request req, Response res, JsonObject body) throws Exception {
        String id = req.headers("id");
        String sessionToken = req.headers("sessionToken");
        AuthContext context = new AuthContext(id, sessionToken);

        try (DataStore db = DataStore.getInstance() ){
            assert db != null;
            List<Report> reportList = db.getReports(context, 20, 1);

            return JSONResponse
                    .SUCCESS()
                    .data(reportList);

        }catch (DataStore.DSException e){
            return JSONResponse
                    .FAILURE()
                    .message("Error: "+e.getMessage());
        }
    }
}
