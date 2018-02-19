package mobi.newsound.controllers;

import com.google.gson.JsonObject;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataAccess;
import mobi.newsound.model.Officer;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.RESTRoute;
import spark.Request;
import spark.Response;

import java.util.List;

/**
 * Created By Tony on 17/02/2018
 */
public class GetAllOfficersController implements RESTRoute {

    @Override
    public Object handle(Request request, Response response, JsonObject body) throws Exception {
        AuthContext context = extractFromBody(body);

        try(DataAccess db = DataAccess.getInstance()){
            assert db != null;

            List<Officer> officers= db.getAllOfficers(context);

            return JSONResponse
                    .SUCCESS()
                    .data(officers);

        }catch (Exception e){
            return JSONResponse
                    .FAILURE()
                    .message("Error: "+e.getMessage());
        }
    }
}
