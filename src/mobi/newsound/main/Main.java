package mobi.newsound.main;
import mobi.newsound.controllers.*;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataAccess;
import mobi.newsound.model.Report;
import mobi.newsound.model.Vehicle;
import mobi.newsound.model.VideoViolation;
import mobi.newsound.model.Violation;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.JSONTransformer;
import mobi.newsound.utils.RESTRoute;
import mobi.newsound.utils.Stub;
import org.apache.log4j.BasicConfigurator;
import spark.staticfiles.StaticFilesConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static mobi.newsound.utils.Config.config;
import static mobi.newsound.utils.Stub.*;
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
        make("/getVehicleModels",new GetVehicleModelsController());
        make("/getAllOfficers",new GetAllOfficersController());
        make("/getAccounts",new GetAccountsController());

        put("/uploadFile","application/json",new FileUploaderController(),new JSONTransformer());
        get("/exportReports",new ExportReportsController());

        post("/exportToDingoPro",new ExportToDingoProController());
        make("/ImportReports",new ImportReportsFromDingoProController());

    }

    static void make(String route, RESTRoute controller){
        post(route, "application/json", controller,new JSONTransformer());
    }


}
