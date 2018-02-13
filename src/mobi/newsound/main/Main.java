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
import javax.xml.crypto.Data;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static mobi.newsound.utils.Config.config;
import static mobi.newsound.utils.Stub.getReportStub;
import static mobi.newsound.utils.Stub.getVehicleStub;
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
        initTests();
    }

    static void make(String route, RESTRoute controller){
        post(route, "application/json", controller,new JSONTransformer());
    }

    static void initTests(){

        //test stubs
        get("/test0","application/json",(request, response) -> {
            response.header("Content-Type","application/json");
            return JSONResponse.SUCCESS().data(getReportStub());
        },new JSONTransformer());

        //test MOTSService
        get("/test1","application/json",(request, response) -> {
            response.header("Content-Type","application/json");
            return JSONResponse.SUCCESS().data(MOTSService.getOwners(getVehicleStub()));
        },new JSONTransformer());

        //test MOTSService
        get("/test2","application/json",(request, response) -> {
            response.header("Content-Type","application/json");
            try(DataStore db = DataStore.getInstance()){
                AuthContext context = db.signIn("goldfedertomer@gmail.com","5f1MXgqBzm");
                return db.createReport(context,getReportStub());
            }catch (DataStore.DSException e){
                return JSONResponse.FAILURE().message(e.getMessage());
            }
        },new JSONTransformer());



    }

    //TODO: remove anything below here when project is finished.


}
