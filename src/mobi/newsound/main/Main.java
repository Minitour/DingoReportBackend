package mobi.newsound.main;
import com.google.gson.JsonObject;
import mobi.newsound.controllers.*;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataStore;
import mobi.newsound.model.Report;
import mobi.newsound.model.Vehicle;
import mobi.newsound.model.VideoViolation;
import mobi.newsound.model.Violation;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.JSONTransformer;
import mobi.newsound.utils.RESTRoute;
import mobi.newsound.utils.Stub;
import org.apache.log4j.BasicConfigurator;
import spark.Request;
import spark.Response;
import spark.staticfiles.StaticFilesConfiguration;

import javax.mail.MessagingException;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static mobi.newsound.utils.Config.config;
import static mobi.newsound.utils.Stub.getReportStub;
import static mobi.newsound.utils.Stub.getVehicleStub;
import static mobi.newsound.utils.Stub.getViolationTypeStub;
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

        make("/makeDecision",new MakeDecisionController());
        make("/submitReport",new SubmitReportController());

        make("/addOfficerToTeam",new AddOfficerToTeamController());
        make("/addReportToTeam",new AddReportToTeamController());

        make("/createTeam",new CreateTeamController());
        make("/createUser",new CreateUserController());
        make("/createVolunteer",new CreateVolunteerController());

        make("/getReports",new GetReportsController());
        make("/getTeams",new GetAllTeamsController());
        make("/getUnassignedOfficers",new GetUnassignedOfficersController());
        make("/getUnassignedReports",new GetUnassignedReportsController());
        make("/getUndecidedViolations",new SubmitReportController());
        make("/getViolationTypes",new GetViolationTypesController());

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

        get("/test3","application/json",((request, response) -> {
            response.header("Content-Type","application/json");
            Vehicle vehicle = Stub.getVehicleStub();
            Report report = new Report(null,"My Description",new Date(),null,vehicle);
            //(String alphaNum, String evidenceLink, ViolationType type, int from, int to, String description)
            VideoViolation videoViolation = new VideoViolation(null,
                    "resources/ababb5480d9f11e8ba890ed5f89f718b/8692075b-bc3f-4d22-b296-eeaac6df17df.mp4",
                    getViolationTypeStub(),
                    0,
                    0,
                    "My Custom Description");
            List<Violation> violations = new ArrayList<>();
            violations.add(videoViolation);
            report.setViolations(violations);
            return report;
        }),new JSONTransformer());



    }

    //TODO: remove anything below here when project is finished.


}
