package mobi.newsound.controllers;

import com.google.gson.JsonObject;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataStore;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.RESTRoute;
import spark.Request;
import spark.Response;

/**
 * Created By Tony on 10/02/2018
 */
public class UpdatePasswordController implements RESTRoute {
    @Override
    public Object handle(Request request, Response response, JsonObject body) throws Exception {
        try {
            String id = body.get("id").getAsString();
            String sessionToken = body.get("sessionToken").getAsString();
            String currentPassword = body.get("currentPassword").getAsString();
            String newPassword = body.get("newPassword").getAsString();

            AuthContext context = new AuthContext(id,sessionToken);

            //check db and create context
            try (DataStore db = DataStore.getInstance() ){
                assert db != null;
                boolean didChange = db.updatePassword(context,currentPassword,newPassword);
                if(didChange)
                    return JSONResponse.SUCCESS();
                else
                    return JSONResponse
                            .FAILURE()
                            .message("Error: Incorrect Password!");

            }catch (DataStore.DSException e){
                return JSONResponse
                        .FAILURE()
                        .message("Error: "+e.getMessage());
            }
        }catch (NullPointerException e){
            return JSONResponse
                    .FAILURE()
                    .message("Error: "+e.getMessage());
        }
    }
}
