package mobi.newsound.controllers;

import mobi.newsound.utils.PropertiesLoader;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import static mobi.newsound.utils.Config.config;
/**
 * Created By Tony on 12/02/2018
 */
public class MailServiceController {
    private static final Session session;

    private static final String SEPARATOR = "========================================================================";

    static {

        PropertiesLoader creds = new PropertiesLoader(config.get("mail_settings").getAsString());

        final String username = creds.get("email");
        final String password = creds.get("password");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    public static void sendMail(String from,String to,String subject,String content) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from,"Dingo Admin"));
        message.setRecipient(Message.RecipientType.TO,new InternetAddress(to));
        message.setSubject(subject);
        message.setText(content);
        Transport.send(message);
        LogMail(from,to,subject,content);
    }

    public static void sendMail(String to,String subject,String content) throws MessagingException, UnsupportedEncodingException {
        sendMail("no-reply@dingoland.net",to,subject,content);
    }

    private static void LogMail(String from,String to,String subject,String content){
        String builder = SEPARATOR +
                "FROM: " + from + "\n" +
                "TO: " + to + "\n" +
                "SUBJECT: " + subject + "\n" +
                "CONTENT: " + content + "\n" +
                SEPARATOR;
        System.out.println(builder);
    }
}
