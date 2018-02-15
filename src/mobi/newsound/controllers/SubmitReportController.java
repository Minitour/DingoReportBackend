package mobi.newsound.controllers;

import com.google.gson.*;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataStore;
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

            try (DataStore db = DataStore.getInstance() ){
                assert db != null;
                List<VideoViolation> videosToUpload = db.createReport(context,report);

                for(VideoViolation violation : videosToUpload){

                    YoutubeVideoUploadController.upload(
                            violation.getEvidenceLink(),
                            violation.getAlphaNum(),
                            "Video Evidence, Report ID: "+report.getReportNum()
                                    + "\n\n"+violation.getDescription(),
                            (videoUrl)-> {
                                try(DataStore db1 = DataStore.getInstance()) {
                                    db1.updateEvidenceUrl(violation, videoUrl);
                                }  catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
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

    static class AbstractElementAdapter implements JsonSerializer<Violation>, JsonDeserializer<Violation> {
        @Override
        public JsonElement serialize(Violation src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
            result.add("properties", context.serialize(src, src.getClass()));

            return result;
        }

        @Override
        public Violation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            //1 = video, 0 = image
            int type = jsonObject.get("classType").getAsInt();

            return context.deserialize(json, type == 1 ? VideoViolation.class : ImageViolation.class);
        }
    }
}
