package mobi.newsound.main;
import mobi.newsound.controllers.ContextValidatorFilter;
import mobi.newsound.controllers.FileUploaderController;
import mobi.newsound.controllers.LoginController;
import mobi.newsound.controllers.UpdatePasswordController;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataStore;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.JSONTransformer;
import mobi.newsound.utils.RESTRoute;
import org.apache.log4j.BasicConfigurator;
import spark.staticfiles.StaticFilesConfiguration;

import java.io.File;

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
        put("/uploadFile","application/json",new FileUploaderController(),new JSONTransformer());

        //TODO: remove this later
        get("/test","application/json",(request, response) -> {
            response.header("Content-Type","application/json");
            return JSONResponse.SUCCESS().data(getReportStub());
        },new JSONTransformer());

        post("/getreport", "application/json", (req,res) -> {

            String id = req.headers("id");
            String sessionToken = req.headers("sessionToken");
            AuthContext context = new AuthContext(id, sessionToken);

            try (DataStore db = DataStore.getInstance() ){
                assert db != null;
                db.getReports(context, 20, 0);

                return JSONResponse
                        .SUCCESS()
                        .data(context);

            }catch (DataStore.DSException e){
                return JSONResponse
                        .FAILURE()
                        .message("Error: "+e.getMessage());
            }
        }, new JSONTransformer());

    }

    static void make(String route, RESTRoute controller){
        post(route, "application/json", controller,new JSONTransformer());
    }

    //TODO: remove anything below here when project is finished.


}
