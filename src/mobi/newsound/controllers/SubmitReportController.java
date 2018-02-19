package mobi.newsound.controllers;

import com.google.gson.*;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataAccess;
import mobi.newsound.model.ImageViolation;
import mobi.newsound.model.Report;
import mobi.newsound.model.VideoViolation;
import mobi.newsound.model.Violation;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.RESTRoute;
import spark.Request;
import spark.Response;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created By Tony on 13/02/2018
 */
public class SubmitReportController implements RESTRoute {

    private static Gson gson;

    static {
        GsonBuilder gsonBilder = new GsonBuilder();
        gsonBilder.registerTypeAdapter(Violation.class, new AbstractElementAdapter());
         gson = gsonBilder.create();
    }

    @Override
    public Object handle(Request request, Response response, JsonObject body) throws Exception {
        try{
            AuthContext context = extractFromBody(body);
            JsonObject reportJson = body.get("report").getAsJsonObject();
            Report report = gson.fromJson(reportJson,Report.class);

            try (DataAccess db = DataAccess.getInstance() ){
                assert db != null;
                List<VideoViolation> videosToUpload = db.createReport(context,report);

                for(VideoViolation violation : videosToUpload){

                    YoutubeVideoUploadController.upload(
                            violation.getEvidenceLink(),
                            violation.getAlphaNum(),
                            "Video Evidence, Report ID: "+report.getReportNum()
                                    + "\n\n"+violation.getDescription(),
                            (videoUrl)-> {
                                try(DataAccess db1 = DataAccess.getInstance()) {
                                    db1.updateEvidenceUrl(violation, videoUrl);
                                }  catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                }

                return JSONResponse
                        .SUCCESS();

            }catch (DataAccess.DSException e){
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

    static class AbstractElementAdapter implements JsonDeserializer<Violation> {
        @Override
        public Violation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            boolean hasDescription = jsonObject.has("description");

            return context.deserialize(json, hasDescription ? VideoViolation.class : ImageViolation.class);
        }
    }
}
