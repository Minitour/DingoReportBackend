package mobi.newsound.controllers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mobi.newsound.auth.AuthContext;
import mobi.newsound.database.DataStore;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.RESTRoute;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.xml.crypto.Data;
import java.util.Objects;


/**
 * Created by Antonio Zaitoun on 09/02/2018.
 */
public class LoginController implements RESTRoute {

    @Override
    public Object handle(Request request, Response response, JsonObject body) throws Exception {
        //get login parameters: (email,password)
        try {
            String email = body.get("email").getAsString();
            String raw_password = body.get("password").getAsString();
            //check db and create context
            try (DataStore db = DataStore.getInstance() ){
                assert db != null;
                AuthContext context = db.signIn(email,raw_password);
                JSONResponse<AuthContext> res = new JSONResponse<>(200,"Success");
                res.setData(context);
                return res;
            }catch (DataStore.DSException e){
                return new JSONResponse<>(400,"Error: "+e.getMessage());
            }
        }catch (NullPointerException e){
            return new JSONResponse<>(400,"Error: "+e.getMessage());
        }
    }
}
