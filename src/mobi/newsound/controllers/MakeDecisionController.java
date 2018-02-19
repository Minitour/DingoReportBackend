package mobi.newsound.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataAccess;
import mobi.newsound.model.Decision;
import mobi.newsound.model.Violation;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.RESTRoute;
import spark.Request;
import spark.Response;

/**
 * Created By Tony on 13/02/2018
 */
public class MakeDecisionController implements RESTRoute{

    private static Gson gson;

    static {
        GsonBuilder gsonBilder = new GsonBuilder();
        gsonBilder.registerTypeAdapter(Violation.class, new SubmitReportController.AbstractElementAdapter());
        gson = gsonBilder.create();
    }

    @Override
    public Object handle(Request request, Response response, JsonObject body) throws Exception {
        try{
            AuthContext context = extractFromBody(body);
            JsonObject decisionJson = body.get("decision").getAsJsonObject();
            Decision decision = gson.fromJson(decisionJson,Decision.class);

            try (DataAccess db = DataAccess.getInstance() ){
                assert db != null;
                boolean val = db.makeDecision(context,decision);
                if(val)
                    return JSONResponse
                            .SUCCESS();
                else
                    return JSONResponse
                            .FAILURE()
                            .message("Already Voted.");

            }catch (DataAccess.DSException e){
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
