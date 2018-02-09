package mobi.newsound.controllers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mobi.newsound.utils.RESTRoute;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Created by Antonio Zaitoun on 09/02/2018.
 */
public class LoginController implements RESTRoute {

    @Override
    public Object handle(Request request, Response response, JsonObject body) throws Exception {
        //get login parameters: (email,password)
        String email = body.get("email").getAsString();
        String raw_password = body.get("password").getAsString();

        //check db and create context

        //return context and role id.
        return null;
    }
}
