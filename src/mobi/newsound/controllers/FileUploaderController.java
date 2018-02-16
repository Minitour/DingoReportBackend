package mobi.newsound.controllers;

import mobi.newsound.database.AuthContext;
import static mobi.newsound.utils.Config.*;

import mobi.newsound.database.DataStore;
import mobi.newsound.model.Account;
import mobi.newsound.model.Resource;
import mobi.newsound.utils.JSONResponse;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
                return JSONResponse
                        .FAILURE()
                        .message("Invalid Context");

            String publicFolder = config.get("public").getAsString();
            String res_dir = config.get("res_dir").getAsString() + "/" + publicFolder;
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
            Resource resource = new Resource(fileName,fileType);
            int key = db.registerResource(context,resource);
            resource.setId(key);
            resource.setOwner(new Account(id,null,null));
            resource.setPath(publicFolder + "/" + context.id + "/" + fileName);

            return JSONResponse
                    .SUCCESS()
                    .data(resource);

        }catch (DataStore.DSException e){
            return JSONResponse
                    .FAILURE()
                    .message("Error: "+e.getMessage());
        }
    }
}
