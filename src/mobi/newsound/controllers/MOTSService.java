package mobi.newsound.controllers;

import com.sun.scenario.Settings;
import mobi.newsound.model.Vehicle;
import mobi.newsound.model.VehicleOwner;
import mobi.newsound.utils.CSVExporter;
import mobi.newsound.utils.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created By Tony on 13/02/2018
 */
public final class MOTSService {

    private MOTSService(){}

    private static boolean executeJar(String jarFilePath, String... args) {
        // Create run arguments for the

        final List<String> actualArgs = new ArrayList<>();
        actualArgs.add(0, "java");
        actualArgs.add(1, "-jar");
        actualArgs.add(2, jarFilePath);
        actualArgs.addAll(Arrays.asList(args));
        try {
            final Runtime re = Runtime.getRuntime();
            //final Process command = re.exec(cmdString, args.toArray(new String[0]));
            final Process command = re.exec(actualArgs.toArray(new String[0]));
            // Wait for the application to Finish
            command.waitFor();
            int exitVal = command.exitValue();
            if (exitVal != 0) {
                return false;
            }
            return true;
        } catch (final IOException | InterruptedException e) {
            return false;
        }
    }

    public static List<VehicleOwner> getOwners(Vehicle vehicle){
        String path = Config.config.get("mots_file").getAsString();
        if(executeJar(path,
                vehicle.getLicensePlate(),
                vehicle.getColorHEX(),
                vehicle.getModel().getName())){

            final String file = "owners.csv";

            //move owners.csv to supporting-files
            try {
                List<VehicleOwner> owners = new ArrayList<>();
                List<String[]> data = CSVExporter.importCSV(file);
                new File(file).delete();
                data.remove(0);
                for(String[] s : data){
                    VehicleOwner owner = new VehicleOwner(s[0].trim(),s[1].trim(),s[2].trim(),s[3].trim());
                    owners.add(owner);
                }

                return owners;
            } catch (IOException e) {
                return null;
            }
        }else
            return null;
    }

}
