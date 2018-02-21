package mobi.newsound.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataAccess;
import mobi.newsound.model.Report;
import mobi.newsound.utils.RESTRoute;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import spark.Request;
import spark.Response;

import javax.xml.bind.JAXB;
import java.io.StringWriter;
import java.util.List;

/**
 * Created By Tony on 21/02/2018
 */
public class ExportToDingoProController implements RESTRoute {
    @Override
    public Object handle(Request request, Response response, JsonObject body) throws Exception {
        response.header("Content-Type","application/xml");

        AuthContext context = extractFromBody(body);

        try(DataAccess db = DataAccess.getInstance()){

            assert db != null;

            List<Report> reportList = db.getReports(context);
            String json = new Gson().toJson(reportList);
            JSONArray jsonObject = new JSONArray(json);

            return XML.toString(jsonObject);

        }catch (Exception e){
            return "<Response>" +
                    "<code>" + 400 + "</code>"
                    +"<message>" + e.getMessage() + "</message>"+
                    "</Response>";
        }
    }
}
