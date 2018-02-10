package mobi.newsound.main;
import mobi.newsound.controllers.ContextValidatorFilter;
import mobi.newsound.controllers.FileUploaderController;
import mobi.newsound.database.AuthContext;
import mobi.newsound.controllers.LoginController;
import mobi.newsound.controllers.UpdatePasswordController;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.JSONTransformer;
import mobi.newsound.utils.RESTRoute;
import org.apache.log4j.BasicConfigurator;
import spark.Filter;
import spark.Request;
import spark.Response;

import javax.servlet.MultipartConfigElement;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

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

        //String res_dir = config.get("res_dir").getAsString();
        //staticFiles.location("/"+res_dir);

        //before("/" + res_dir+"/*", new ContextValidatorFilter());

        make("/signin",new LoginController());
        make("/updatePassword",new UpdatePasswordController());
        put("/uploadFile","application/json",new FileUploaderController(),new JSONTransformer());


    }

    static void make(String route, RESTRoute controller){
        post(route, "application/json", controller,new JSONTransformer());
    }
}
