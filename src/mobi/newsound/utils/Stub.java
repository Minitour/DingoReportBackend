package mobi.newsound.utils;

import mobi.newsound.model.*;

import java.util.*;

/**
 * Created by Antonio Zaitoun on 11/02/2018.
 */
public final class Stub {

    //TODO: replace UUID.randomUUID().toString() with real stubs using a stub lib.

    public static Report getReportStub() {
        int reportNum = new Random().nextInt(1000);
        String description = UUID.randomUUID().toString();
        Date date = new Date();
        Volunteer volunteer = getVolunteerStub();
        Vehicle vehicle = getVehicleStub();
        Report report = new Report(reportNum,description,date,volunteer,vehicle);

        int rnd = new Random().nextInt(10) + 1;
        List<Violation> violationList = new ArrayList<>();

        for (int i = 0; i < rnd; i++)
            violationList.add(getViolationStub());

        report.setViolations(violationList);

        return report;
    }

    public static Vehicle getVehicleStub() {
        String licesne = UUID.randomUUID().toString();
        VehicleModel model = new VehicleModel(1,"some model");
        String color = UUID.randomUUID().toString();
        return new Vehicle(licesne,model,color);
    }

    public static Volunteer getVolunteerStub() {
        String id = UUID.randomUUID().toString();
        String email = UUID.randomUUID().toString();
        String name = UUID.randomUUID().toString();
        String phone = "" + new Random().nextLong();
        return new Volunteer(id,email,name,phone);
    }

    public static Violation getViolationStub(){
        Random r = new Random();
        boolean isVideo = r.nextBoolean();
        Violation violation;

        String alphaNum = UUID.randomUUID().toString();
        String link = "http://localhost:8080/"+ UUID.randomUUID().toString() + (isVideo ? ".mp4" : ".png");
        ViolationType type = getViolationTypeStub();

        if(isVideo){
            String description = UUID.randomUUID().toString();
            int from = r.nextInt(120);
            int to = r.nextInt(120) + from;
            violation = new VideoViolation(alphaNum,link,type,from,to,description);
        }else{
            violation = new ImageViolation(alphaNum,link,type);
        }

        return violation;
    }

    public static ViolationType getViolationTypeStub(){
        Random r = new Random();
        int type = r.nextInt(100);
        String name = UUID.randomUUID().toString();
        String description = UUID.randomUUID().toString();
        int points = r.nextInt(5) + 1;
        double fine = r.nextInt(5000);
        boolean inviteToCoute = r.nextBoolean();

        return new ViolationType(type,name,description,points,fine,inviteToCoute);
    }

    //TODO: add other data type stubs.
}
