package mobi.newsound.controllers;

import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataStore;
import spark.Filter;
import spark.Request;
import spark.Response;

import static spark.Spark.halt;

/**
 * Created By Tony on 10/02/2018
 */
public class ContextValidatorFilter implements Filter {
    @Override
    public void handle(Request request, Response response) throws Exception {
        //get user data
        String id = request.headers("id");
        String sessionToken = request.headers("sessionToken");


        AuthContext context = new AuthContext(id,sessionToken);

        String[] parts = request.pathInfo().split("/");

        try (DataStore db = DataStore.getInstance() ) {
            assert db != null;

            if (!db.isValid(context))
                halt("Invalid Context");

            if(parts.length <= 2 || !parts[2].equals(id))
                halt("Access Denied");

        }catch (DataStore.DSException e){
            halt("Error");
        }
    }
}
