package mobi.newsound.main;
import mobi.newsound.database.AuthContext;
import mobi.newsound.controllers.LoginController;
import mobi.newsound.controllers.UpdatePasswordController;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.JSONTransformer;
import mobi.newsound.utils.RESTRoute;
import org.apache.log4j.BasicConfigurator;

import static mobi.newsound.utils.Config.config;
import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
	// write your code here
        BasicConfigurator.configure();
        port(config.get("port").getAsInt());

        get("/test","application/json", (request, response) -> {
            JSONResponse<AuthContext> resp =  new JSONResponse(100,"whatever");
            resp.setData(new AuthContext("1","1"));
            return resp;
        }, new JSONTransformer());

        make("/signin",new LoginController());
        make("/updatePassword",new UpdatePasswordController());


    }

    static void make(String route, RESTRoute controller){
        post(route, "application/json", controller,new JSONTransformer());
    }
}
