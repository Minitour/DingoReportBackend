package mobi.newsound.main;
import com.google.gson.JsonObject;
import mobi.newsound.auth.AuthContext;
import mobi.newsound.database.DataStore;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.JSONTransformer;
import mobi.newsound.utils.RESTRoute;
import spark.Request;
import spark.Response;

import static mobi.newsound.utils.Config.config;
import static spark.Spark.get;
import static spark.Spark.port;

public class Main {

    public static void main(String[] args) {
	// write your code here

        port(config.get("port").getAsInt());

        get("/test","application/json", (request, response) -> {
            JSONResponse<AuthContext> resp =  new JSONResponse(100,"whatever");
            resp.setData(new AuthContext("1","1"));
            return resp;
        }, new JSONTransformer());

        get("/createVolunteer", "application/json", (request, response) -> {
            try(DataStore db = DataStore.getInstance()){
                assert db!=null;
            }
            return null;
        },new JSONTransformer());


    }
}
