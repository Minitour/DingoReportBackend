package mobi.newsound.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import mobi.newsound.database.AuthContext;
import mobi.newsound.database.DataStore;
import mobi.newsound.database.TokenGenerator;
import mobi.newsound.model.Volunteer;
import mobi.newsound.utils.EmailValidator;
import mobi.newsound.utils.JSONResponse;
import mobi.newsound.utils.RESTRoute;
import org.mindrot.jbcrypt.BCrypt;
import spark.Request;
import spark.Response;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

/**
 * Created By Tony on 12/02/2018
 */
public class CreateVolunteerController implements RESTRoute {
    @Override
    public Object handle(Request request, Response response, JsonObject body) throws Exception {
        try{
            String id = body.get("id").getAsString();
            String sessionToken = body.get("sessionToken").getAsString();
            JsonObject volunteerJson = body.get("volunteer").getAsJsonObject();
            Volunteer volunteer = new Gson().fromJson(volunteerJson,Volunteer.class);

            if(!EmailValidator.validate(volunteer.getEMAIL()))
                throw new IllegalArgumentException("Invalid Email");

            AuthContext context = new AuthContext(id,sessionToken);

            try (DataStore db = DataStore.getInstance() ){
                assert db != null;
                String generatedPassword = TokenGenerator.generateToken(10);
                String hashedPassword = BCrypt.hashpw(generatedPassword,BCrypt.gensalt());

                //set hashed password
                volunteer.setPassword(hashedPassword);
                db.createVolunteer(context,volunteer);

                //send email to users
                try {
                    MailServiceController
                            .sendMail("no-reply@dingoland.net",
                                    volunteer.getEMAIL(),
                                    "Account Credentials",
                                    "Thanks For Signing Up to DingoLand VRS\n\n\n" +
                                            "Your Login Info:\n" +
                                            "email: "+volunteer.getEMAIL()+"\n" +
                                            "password: "+generatedPassword);
                } catch (MessagingException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                return JSONResponse
                        .SUCCESS()
                        .message("Account Created.");

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
