package mobi.newsound.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataStore;
import mobi.newsound.model.Decision;
import mobi.newsound.model.Report;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.RESTRoute;
import spark.Request;
import spark.Response;

/**
 * Created By Tony on 13/02/2018
 */
public class SubmitReportController implements RESTRoute {
    @Override
    public Object handle(Request request, Response response, JsonObject body) throws Exception {
        try{
            AuthContext context = extractFromBody(body);
            JsonObject reportJson = body.get("report").getAsJsonObject();
            Report report = new Gson().fromJson(reportJson,Report.class);

            try (DataStore db = DataStore.getInstance() ){
                assert db != null;
                boolean val = db.createReport(context,report);
                if(val)
                    return JSONResponse
                            .SUCCESS();
                else
                    return JSONResponse
                            .FAILURE()
                            .message("Unable to create report.");

            }catch (DataStore.DSException e){
                return JSONResponse
                        .FAILURE()
                        .message("Error: "+e.getMessage());
            }
        }catch (Exception e){
            return JSONResponse
                    .FAILURE()
                    .message("Error: "+e.getMessage());
        }
    }
}
