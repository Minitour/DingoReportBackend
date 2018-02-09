package mobi.newsound.main;
import mobi.newsound.auth.AuthContext;
import mobi.newsound.utils.JSONTransformer;

import static mobi.newsound.utils.Config.config;
import static spark.Spark.get;
import static spark.Spark.port;

public class Main {

    public static void main(String[] args) {
	// write your code here

        port(config.get("port").getAsInt());

        get("/test","application/json", (request, response) -> {
            return new AuthContext("1","2");
        }, new JSONTransformer());


    }
}
