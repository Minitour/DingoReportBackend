package mobi.newsound.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataStore;
import mobi.newsound.model.Team;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.RESTRoute;
import spark.Request;
import spark.Response;

/**
 * Created by Antonio Zaitoun on 15/02/2018.
 */
public class CreateTeamController implements RESTRoute {

    private static final Gson gson = new Gson();

    @Override
    public Object handle(Request request, Response response, JsonObject body) throws Exception {

        AuthContext context = extractFromBody(body);
        Team team = gson.fromJson(body.get("team"),Team.class);

        try(DataStore db = DataStore.getInstance()){

            db.createTeam(context,team);

            return JSONResponse
                    .SUCCESS();

        }catch (Exception e){
            return JSONResponse
                    .FAILURE()
                    .message("Error: " + e.getMessage());
        }
    }
}
