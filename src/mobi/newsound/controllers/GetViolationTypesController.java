package mobi.newsound.controllers;

import com.google.gson.JsonObject;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataStore;
import mobi.newsound.model.Report;
import mobi.newsound.model.ViolationType;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.RESTRoute;
import spark.Request;
import spark.Response;

import java.util.List;

/**
 * Created by Antonio Zaitoun on 15/02/2018.
 */
public class GetViolationTypesController implements RESTRoute{
    @Override
    public Object handle(Request request, Response response, JsonObject body) throws Exception {
        AuthContext context = extractFromBody(body);

        try(DataStore db = DataStore.getInstance()){
            assert db != null;

            List<ViolationType> violationTypes= db.getViolationTypes(context);

            return JSONResponse
                    .SUCCESS()
                    .data(violationTypes);

        }catch (Exception e){
            return JSONResponse
                    .FAILURE()
                    .message("Error: "+e.getMessage());
        }
    }
}
