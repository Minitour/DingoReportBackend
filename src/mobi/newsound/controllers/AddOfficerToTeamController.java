package mobi.newsound.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataAccess;
import mobi.newsound.model.Officer;
import mobi.newsound.model.Team;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.RESTRoute;
import spark.Request;
import spark.Response;

/**
 * Created by Antonio Zaitoun on 15/02/2018.
 */
public class AddOfficerToTeamController implements RESTRoute {

    private static final Gson gson = new Gson();

    @Override
    public Object handle(Request request, Response response, JsonObject body) throws Exception {

        AuthContext context = extractFromBody(body);
        Officer officer = gson.fromJson(body.get("officer"),Officer.class);
        Team team = gson.fromJson(body.get("team"),Team.class);

        try(DataAccess db = DataAccess.getInstance()){

            db.addOfficerToTeam(context,officer,team);

            return JSONResponse
                    .SUCCESS();

        }catch (Exception e){
            return JSONResponse
                    .FAILURE()
                    .message("Error: " + e.getMessage());
        }
    }
}
