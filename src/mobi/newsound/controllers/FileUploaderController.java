package mobi.newsound.controllers;

import mobi.newsound.database.AuthContext;
import static mobi.newsound.utils.Config.*;

import mobi.newsound.database.DataStore;
import mobi.newsound.model.Resource;
import mobi.newsound.utils.JSONResponse;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.util.UUID;

/**
 * Created By Tony on 10/02/2018
 */
public class FileUploaderController implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {

        //get user data
        String id = request.headers("id");
        String sessionToken = request.headers("sessionToken");
        String fileType = request.headers("fileType");

        //get context
        AuthContext context = new AuthContext(id,sessionToken);

        try (DataStore db = DataStore.getInstance() ){
            assert db != null;

            if(!db.isValid(context))
                return new JSONResponse<>(400,"Invalid Context");

            String res_dir = config.get("res_dir").getAsString() + "/" + config.get("public").getAsString();
            String fileName = UUID.randomUUID().toString() + "." + fileType;

            String path = new File(".").getAbsolutePath() + "/" + res_dir + "/" + context.id + "/";
            new File(path).mkdirs();

            try (InputStream is = request.raw().getInputStream()) {
                // Use the input stream to create a file
                OutputStream os = new FileOutputStream(path+fileName);
                byte[] buffer = new byte[1024];
                int read;
                while((read= is.read(buffer)) != -1)
                    os.write(buffer,0,read);
                is.close();
                os.flush();
                os.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            //file uploaded - move to resources dir.
            db.registerResource(context,new Resource(fileName,fileType));
            JSONResponse<String> res = new JSONResponse<String>(200,"Success");
            res.setData(fileName);
            return res;

        }catch (DataStore.DSException e){
            return new JSONResponse<>(400,"Error: "+e.getMessage());
        }
    }
}
