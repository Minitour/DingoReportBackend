package mobi.newsound.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataStore;
import mobi.newsound.model.Decision;
import mobi.newsound.model.Report;
import mobi.newsound.model.VideoViolation;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.RESTRoute;
import spark.Request;
import spark.Response;

import java.util.List;

/**
 * Created By Tony on 13/02/2018
 */
public class SubmitReportController implements RESTRoute {
    @Override
    public Object handle(Request request, Response response, JsonObject body) throws Exception {
        try{
            AuthContext context = extractFromBody(body);
            JsonObject reportJson = body.get("report").getAsJsonObject();
            Report report = new Gson().fromJson(reportJson,Report.class);

            //TODO: Test this controller with VideoViolations
            try (DataStore db = DataStore.getInstance() ){
                assert db != null;
                List<VideoViolation> videosToUpload = db.createReport(context,report);

                for(VideoViolation violation : videosToUpload){
                    YoutubeVideoUploadController.upload(
                            violation.getEvidenceLink(),
                            violation.getAlphaNum(),
                            "Video Evidence, Report ID: "+report.getReportNum(),
                            (videoUrl)-> db.updateEvidenceUrl(context,violation,videoUrl));
                }

                return JSONResponse
                        .SUCCESS();

            }catch (DataStore.DSException e){
                return JSONResponse
                        .FAILURE()
                        .message("Error: "+e.getMessage());
            }
        }catch (Exception e){
            return JSONResponse
                    .FAILURE()
                    .message("Error: "+e.getMessage());
        }
    }
}
