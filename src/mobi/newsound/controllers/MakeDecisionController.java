package mobi.newsound.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataStore;
import mobi.newsound.model.Decision;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.RESTRoute;
import spark.Request;
import spark.Response;

/**
 * Created By Tony on 13/02/2018
 */
public class MakeDecisionController implements RESTRoute{
    @Override
    public Object handle(Request request, Response response, JsonObject body) throws Exception {
        try{
            AuthContext context = extractFromBody(body);
            JsonObject decisionJson = body.get("decision").getAsJsonObject();
            Decision decision = new Gson().fromJson(decisionJson,Decision.class);

            try (DataStore db = DataStore.getInstance() ){
                assert db != null;
                boolean val = db.makeDecision(context,decision);
                if(val)
                    return JSONResponse
                            .SUCCESS();
                else
                    return JSONResponse
                            .FAILURE()
                            .message("Already Voted.");

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
