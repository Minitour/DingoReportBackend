package mobi.newsound.main;
import com.google.gson.JsonObject;
import mobi.newsound.controllers.*;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataStore;
import mobi.newsound.model.Report;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.JSONTransformer;
import mobi.newsound.utils.RESTRoute;
import org.apache.log4j.BasicConfigurator;
import spark.Request;
import spark.Response;
import spark.staticfiles.StaticFilesConfiguration;

import javax.mail.MessagingException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static mobi.newsound.utils.Config.config;
import static mobi.newsound.utils.Stub.getReportStub;
import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
	// write your code here

        /*
         * configure logger
         */
        BasicConfigurator.configure();

        /*
         * setup port
         */
        port(config.get("port").getAsInt());

        /*
         * setup static content
         */
        String res_dir = config.get("res_dir").getAsString();
        String pub = config.get("public").getAsString();

        StaticFilesConfiguration staticHandler = new StaticFilesConfiguration();
        staticHandler.configureExternal(new File(res_dir).getAbsolutePath());

        after((request, response) -> staticHandler.consume(request.raw(), response.raw()));
        before("/" + pub+"/*", new ContextValidatorFilter());

        /*
         * setup routes
         */
        make("/signin",new LoginController());
        make("/updatePassword",new UpdatePasswordController());
        make("/createVolunteer",new CreateVolunteerController());
        make("/getReports",new GetReportsController());
        put("/uploadFile","application/json",new FileUploaderController(),new JSONTransformer());
        get("/exportReports",new ExportReportsController());

        //TODO: remove this later
        get("/test","application/json",(request, response) -> {
            response.header("Content-Type","application/json");
            return JSONResponse.SUCCESS().data(getReportStub());
        },new JSONTransformer());

    }

    static void make(String route, RESTRoute controller){
        post(route, "application/json", controller,new JSONTransformer());
    }

    //TODO: remove anything below here when project is finished.


}
